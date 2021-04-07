package top.codewood.wx.controller.rest;

import com.google.zxing.WriterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.codewood.wx.common.api.WxConstants;
import top.codewood.wx.common.util.net.NetUtils;
import top.codewood.wx.common.util.xml.XStreamConverter;
import top.codewood.wx.common.util.xml.XmlUtils;
import top.codewood.wx.config.property.WxAppProperties;
import top.codewood.wx.pay.v2.bean.notify.WxPayV2Notify;
import top.codewood.wx.pay.v2.bean.request.*;
import top.codewood.wx.pay.v2.bean.result.*;
import top.codewood.wx.pay.v2.common.WxPayConstants;
import top.codewood.wx.service.ProfitSharingService;
import top.codewood.wx.service.WxPayService;
import top.codewood.wx.util.Strings;
import top.codewood.wx.util.qrcode.QRCodeUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wx/pay/rest")
public class WxPayRestController {

    static Logger LOGGER = LoggerFactory.getLogger(WxPayRestController.class);

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private ProfitSharingService profitSharingService;

    @Autowired
    private WxAppProperties wxAppProperties;

    @RequestMapping("/unifiedorder")
    public Map<String, String> unifiedorder(@RequestParam("appid") String appid,
                                            @RequestParam("payType") String payType,
                                            @RequestParam("tradeNo") String tradeNo,
                                            @RequestParam("description") String description,
                                            @RequestParam("amount") double amount,
                                            @RequestParam(value = "openid", required = false) String openid,
                                            @RequestParam(value = "profitSharing", required = false, defaultValue = "false") boolean profitSharing,
                                            HttpServletResponse response) {


        String nonceStr = Strings.randomString(32);
        long timeStamp = System.currentTimeMillis() / 1000;
        Double total = amount * 100;

        WxPayUnifiedOrderV2Request.Builder builder = new WxPayUnifiedOrderV2Request.Builder();
        builder.appid(appid)
                .mchid(wxAppProperties.getPay().getMchid())
                .nonceStr(nonceStr)
                .body(description)
                .outTradeNo(tradeNo)
                .totalFee(total.intValue())
                .spbillCreateIp(NetUtils.localAddress())
                .notifyUrl(wxAppProperties.getPay().getNotifyUrl())
                .tradeType(payType)
                .profitSharing(profitSharing ? "Y" : "N");
        if (WxPayConstants.PayType.JSAPI.name().equalsIgnoreCase(payType)) {
            builder.openid(openid);
        }
        WxPayUnifiedOrderV2Request unifiedOrderV2Request = builder.build();
        WxPayUnifiedOrderV2Result unifiedOrderV2Result = wxPayService.unifiedOrder(unifiedOrderV2Request);

        Map<String, String> map = new HashMap<>();

        if (WxPayConstants.PayType.NATIVE.name().equalsIgnoreCase(payType)) {
            map.put("codeUrl", unifiedOrderV2Result.getCodeUrl());
        } else {
            map.put("appId", appid);
            map.put("timeStamp", String.valueOf(timeStamp));
            map.put("nonceStr", nonceStr);

            map.put("signType", WxPayConstants.SignType.MD5);
            map.put("package", "prepay_id=" + unifiedOrderV2Result.getPrepayId());
            map.put("paySign", wxPayService.sign(map));
        }

        return map;
    }


    @RequestMapping("/notify")
    public String notify(@RequestBody String notifyBody) {
        try {
            LOGGER.debug("notify body: {}", notifyBody);
            wxPayService.checkResultSign(notifyBody);
            WxPayV2Notify wxPayNotify = XStreamConverter.fromXml(WxPayV2Notify.class, notifyBody);
            LOGGER.debug("wxPayNotify: {}", wxPayNotify);
        } catch (Exception e) {
            LOGGER.info("微信支付 - 通知结果：{}", notifyBody);
            e.printStackTrace();
        }
        return notifyCallbackStr(WxConstants.SUCCESS.toUpperCase(), "成功");
    }

    @RequestMapping("/query")
    public WxPayOrderQueryV2Result query(
            @RequestParam("appid") String appid,
            @RequestParam(value = "transactionId", required = false) String transactionId,
            @RequestParam(value = "tradeNo", required = false) String outTradeNo) {
        if (!StringUtils.hasText(transactionId) && !StringUtils.hasText(outTradeNo)) {
            throw new RuntimeException("transactionId,outTradeNo不能都为空！");
        }

        WxPayOrderQueryV2Request orderQueryV2Request = new WxPayOrderQueryV2Request.Builder()
                .appid(appid)
                .mchid(wxAppProperties.getPay().getMchid())
                .nonceStr(Strings.randomString(32))
                .transactionId(transactionId)
                .outTradeNo(outTradeNo)
                .build();

        return wxPayService.orderQuery(orderQueryV2Request);
    }

    @RequestMapping("/close")
    public String close(@RequestParam("appid") String appid,
                        @RequestParam("tradeNo") String outTradeNo) {
        WxPayOrderCloseV2Request orderCloseV2Request = new WxPayOrderCloseV2Request.Builder()
                .appid(appid)
                .mchid(wxAppProperties.getPay().getMchid())
                .nonceStr(Strings.randomString(32))
                .outTradeNo(outTradeNo)
                .build();
        WxPayOrderCloseV2Result orderCloseV2Result = wxPayService.closeOrder(orderCloseV2Request);
        LOGGER.debug("order close result: {}", orderCloseV2Result);
        return WxConstants.SUCCESS;
    }

    @RequestMapping("/refund")
    public String refund(@RequestParam("appid") String appid,
                         @RequestParam("tradeNo") String tradeNo,
                         @RequestParam(value = "reason", required = false) String reason,
                         @RequestParam(value = "total", required = false) Double total,
                         @RequestParam("refund") Double refund) {
        if (total != null) {
            total = total * 100;
        }
        refund = refund * 100;

        String outRefundNo = String.format("P%s%s", tradeNo, new SimpleDateFormat("mmss").format(new Date()));
        WxPayRefundV2Request refundV2Request = new WxPayRefundV2Request.Builder()
                .appid(appid)
                .mchid(wxAppProperties.getPay().getMchid())
                .nonceStr(Strings.randomString(32))
                .outTradeNo(tradeNo)
                .outRefundNo(outRefundNo)
                .totalFee(total != null ? total.intValue() : refund.intValue())
                .refundFee(refund.intValue())
                .notifyUrl(wxAppProperties.getPay().getRefundNotifyUrl())
                .build();

        WxPayRefundV2Result refundV2Result = wxPayService.refund(refundV2Request);
        LOGGER.debug("refund result: {}", refundV2Result);

        return WxConstants.SUCCESS;
    }

    @RequestMapping("/refundnotify")
    public String refundNotify(@RequestBody String notifyBody) {
        try {
            LOGGER.debug("refund notify body: {}", notifyBody);
            Map<String, Object> resultMap = XmlUtils.xml2Map(notifyBody);
            String reqInfo = (String) resultMap.get("req_info");
            String decryptedStr = wxPayService.decrypt(reqInfo);
            WxPayRefundNotifyV2Result refundNotifyV2Result = XStreamConverter.fromXml(WxPayRefundNotifyV2Result.class, decryptedStr);
            LOGGER.debug("refund notify result: {} ", refundNotifyV2Result);
        } catch (Exception e) {
            LOGGER.info("微信退款 - 通知结果：{}", notifyBody);
            e.printStackTrace();
        }
        return notifyCallbackStr(WxConstants.SUCCESS.toUpperCase(), "成功");
    }

    @RequestMapping("refundquery")
    public WxPayRefundQueryV2Result refundQuery(@RequestParam("appid") String appid,
                                                @RequestParam("tradeNo") String outTradeNo) {

        WxPayRefundQueryV2Request refundQueryV2Request = new WxPayRefundQueryV2Request.Builder()
                .appid(appid)
                .mchid(wxAppProperties.getPay().getMchid())
                .nonceStr(Strings.randomString(32))
                .outTradeNo(outTradeNo)
                .build();

        return wxPayService.refundQuery(refundQueryV2Request);
    }

    private String notifyCallbackStr(String code, String message) {
        return String.format("<xml>\n" +
                "  <return_code><![CDATA[%s]]></return_code>\n" +
                "  <return_msg><![CDATA[%s]]></return_msg>\n" +
                "</xml>", code, message);
    }

}
