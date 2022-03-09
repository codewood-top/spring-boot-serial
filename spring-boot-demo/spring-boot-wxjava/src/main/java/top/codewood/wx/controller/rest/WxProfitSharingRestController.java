package top.codewood.wx.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.codewood.wx.common.api.WxConstants;
import top.codewood.wx.common.util.json.WxGsonBaseBuilder;
import top.codewood.wx.config.property.WxAppProperties;
import top.codewood.wx.pay.v2.bean.profitsharing.*;
import top.codewood.wx.service.ProfitSharingService;
import top.codewood.wx.util.Strings;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wx/profitsharing/rest")
public class WxProfitSharingRestController {

    final Logger logger = LoggerFactory.getLogger(WxProfitSharingRestController.class);

    private WxAppProperties wxAppProperties;
    private ProfitSharingService profitSharingService;

    public WxProfitSharingRestController(WxAppProperties wxAppProperties, ProfitSharingService profitSharingService) {
        this.wxAppProperties = wxAppProperties;
        this.profitSharingService = profitSharingService;
    }

    @RequestMapping("/addreceiver")
    public String addReceiver(@RequestParam("appid") String appid,
                              @RequestParam("type") String type,
                              @RequestParam("account") String account,
                              @RequestParam(value = "name", required = false) String name,
                              @RequestParam("relationType") String relationType,
                              @RequestParam("customRelation") String customRelation) {
        Receiver receiver = new Receiver(type, account, name, relationType, customRelation);

        ProfitSharingReceiverV2Request receiverV2Request = new ProfitSharingReceiverV2Request.Builder()
                .appid(appid)
                .mchid(wxAppProperties.getPay().getMchid())
                .nonceStr(Strings.randomString(32))
                .receiver(WxGsonBaseBuilder.create().toJson(receiver))
                .build();
        ProfitSharingReceiverV2Result receiverV2Result = profitSharingService.addReceiver(receiverV2Request);
        logger.debug("receiver result: {}", receiverV2Result);
        return WxConstants.SUCCESS;
    }

    @RequestMapping("/removereceiver")
    public String removeReceiver(@RequestParam("appid") String appid,
                                 @RequestParam("type") String type,
                                 @RequestParam("account") String account) {
        Receiver receiver = new Receiver(type, account);
        ProfitSharingReceiverV2Request receiverV2Request = new ProfitSharingReceiverV2Request.Builder()
                .appid(appid)
                .mchid(wxAppProperties.getPay().getMchid())
                .nonceStr(Strings.randomString(32))
                .receiver(WxGsonBaseBuilder.create().toJson(receiver))
                .build();
        ProfitSharingReceiverV2Result receiverV2Result = profitSharingService.removeReceiver(receiverV2Request);
        logger.debug("receiver result: {}", receiverV2Result);
        return WxConstants.SUCCESS;
    }

    @RequestMapping("/profitsharing")
    public String profitSharing(@RequestParam("appid") String appid,
                                @RequestParam("transactionId") String transactionId,
                                @RequestParam("tradeNo") String outTradeNo,
                                @RequestParam(value = "type", required = false, defaultValue = "PERSONAL_OPENID") String type,
                                @RequestParam("account") String account,
                                @RequestParam("amount") double amout,
                                @RequestParam("description") String description,
                                @RequestParam(value = "name", required = false) String name,
                                @RequestParam(value = "multi", required = false, defaultValue = "false") boolean multi
    ) {
        Double amountd = amout * 100;
        Receiver receiver = new Receiver(type, account, amountd.intValue(), description);
        if (StringUtils.hasText(name)) {
            receiver.setName(name);
        }

        String outOrderNo = String.format("P%s%s", outTradeNo, DateTimeFormatter.ofPattern("HHMMss").format(LocalDateTime.now()));

        ProfitSharingV2Request profitSharingV2Request = new ProfitSharingV2Request.Builder()
                .appid(appid)
                .mchid(wxAppProperties.getPay().getMchid())
                .nonceStr(Strings.randomString(32))
                .transactionId(transactionId)
                .outOrderNo(outOrderNo)
                .receivers(WxGsonBaseBuilder.create().toJson(Arrays.asList(receiver)))
                .build();
        ProfitSharingV2Result profitSharingV2Result = profitSharingService.profitSharing(profitSharingV2Request, multi);
        logger.debug("profit sharing result: {}", profitSharingV2Result);
        return WxConstants.SUCCESS;
    }

    @RequestMapping("/query")
    public ProfitSharingQueryV2Result profitSharingQuery(@RequestParam("transactionId") String transactionId,
                                                         @RequestParam("outOrderNo") String outOrderNo) {
        ProfitSharingQueryV2Request queryV2Request = new ProfitSharingQueryV2Request.Builder()
                .nonceStr(Strings.randomString(32))
                .mchid(wxAppProperties.getPay().getMchid())
                .transactionId(transactionId)
                .outOrderNo(outOrderNo)
                .build();

        return profitSharingService.query(queryV2Request);
    }

    @RequestMapping("/finish")
    public ProfitSharingV2Result finish(@RequestParam("appid") String appid,
                                        @RequestParam("transactionId") String transactionId,
                                        @RequestParam("outTradeNo") String outTradeNo,
                                        @RequestParam("description") String description) {
        String outOrderNo = String.format("P%s%s", outTradeNo, DateTimeFormatter.ofPattern("HHMMss").format(LocalDateTime.now()));

        ProfitSharingFinishV2Request finishV2Request = new ProfitSharingFinishV2Request.Builder()
                .appid(appid)
                .mchid(wxAppProperties.getPay().getMchid())
                .nonceStr(Strings.randomString(32))
                .transactionId(transactionId)
                .outOrderNo(outOrderNo)
                .description(description)
                .build();

        return profitSharingService.finish(finishV2Request);
    }

    @RequestMapping("/amountquery")
    public Map profitSharingAmountQuery(
            @RequestParam("transactionId") String transactionId) {
        ProfitSharingAmountQueryV2Request amountQueryV2Request = new ProfitSharingAmountQueryV2Request.Builder()
                .mchid(wxAppProperties.getPay().getMchid())
                .nonceStr(Strings.randomString(32))
                .transactionId(transactionId)
                .build();
        ProfitSharingAmountQueryV2Result amountQueryV2Result = profitSharingService.amountQuery(amountQueryV2Request);
        Map<String, Object> map = new HashMap<>();
        map.put("transactionId", amountQueryV2Result.getTransactionId());
        map.put("unSplitAmount", amountQueryV2Result.getUnSplitAmount() / 100.0);
        return map;
    }

}
