package top.codewood.wx.controller.rest;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.codewood.wx.common.api.WxConstants;
import top.codewood.wx.common.util.xml.XStreamConverter;
import top.codewood.wx.mp.bean.message.WxMpTextRespXmlMessage;
import top.codewood.wx.mp.bean.message.WxMpTransferKefuRespXMLMessage;
import top.codewood.wx.mp.bean.message.WxMpXmlMessage;
import top.codewood.wx.service.WxMpService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

@RestController
@RequestMapping("/wx/mp/rest")
public class WxMpRestController {

    static final Logger LOGGER = LoggerFactory.getLogger(WxMpRestController.class);

    private static final String WX_MP_TOKEN = "codewoodtoken";

    @Autowired
    private WxMpService wxMpService;

    @RequestMapping("/access_token")
    public String accessToken() {
        return wxMpService.getAccessToken();
    }

    @RequestMapping("/js_signature")
    public String jsSignature(@RequestParam("nonceStr") String nonceStr,
                              @RequestParam("timeStamp") Long timeStamp,
                              @RequestParam("url") String url) {
        return wxMpService.jsSignature(nonceStr, timeStamp, url);
    }

    @RequestMapping("/handle")
    public String handle(HttpServletRequest request) {
        String echoStr = request.getParameter("echostr");
        if (StringUtils.hasText(echoStr)) {
            String signature = request.getParameter("signature"),
                timestamp = request.getParameter("timestamp"),
                nonce = request.getParameter("nonce");
            if (!signature.equals(generateSignature(timestamp, nonce, WX_MP_TOKEN))) {
                LOGGER.debug("check signature failure, signature: {}, timestamp: {}, nonce: {}", signature, timestamp, nonce);
                return WxConstants.FAILURE;
            }
            return echoStr;
        } else {
            try {
                WxMpXmlMessage mpXmlMessage = XStreamConverter.fromXml(WxMpXmlMessage.class, request.getInputStream());
                String msg = null;
                switch (mpXmlMessage.getMsgType()) {
                    case WxConstants.XmlMsgType.TEXT:
                        if ("openid".equals(mpXmlMessage.getContent())) {
                            msg = mpXmlMessage.getFromUser();
                        }
                        //msg = "??????_" + mpXmlMessage.getContent();
                        break;
                    case WxConstants.XmlMsgType.EVENT:
                        switch (mpXmlMessage.getEvent()) {
                            case WxConstants.EventType.SUBSCRIBE:
                                msg = "???????????? codewood.";
                                break;
                            case WxConstants.EventType.UNSUBSCRIBE:
                                return WxConstants.SUCCESS.toLowerCase();
                            case WxConstants.EventType.SCAN:
                                break;
                            case WxConstants.EventType.CLICK:
                                break;
                        }
                        break;
                    case WxConstants.XmlMsgType.IMAGE:
                        //msg = "??????_" + mpXmlMessage.getMediaId() ;
                        break;
                    case WxConstants.XmlMsgType.VOICE:
                        //msg = "??????_" + mpXmlMessage.getMediaId();
                        break;
                    case WxConstants.XmlMsgType.VIDEO:
                        //msg = "??????_" + mpXmlMessage.getMediaId() + "_" + mpXmlMessage.getThumbMediaId();
                        break;
                    case WxConstants.XmlMsgType.SHORT_VIDEO:
                        //msg = "?????????_" + mpXmlMessage.getMediaId() + "_" + mpXmlMessage.getThumbMediaId();
                        break;
                    case WxConstants.XmlMsgType.LINK:
                        //msg = String.format("??????: <a href='%s'>%s</a>_%s", mpXmlMessage.getUrl(), mpXmlMessage.getTitle(), mpXmlMessage.getDescription());
                        break;
                    case WxConstants.XmlMsgType.LOCATION:
                        //msg = String.format("?????? [%s](%s): %s,%s", mpXmlMessage.getLabel(), mpXmlMessage.getScale(), mpXmlMessage.getLatitude(), mpXmlMessage.getLongitude());
                        break;
                    default:
                        msg = "??????????????????" + mpXmlMessage.getMsgType();
                }
                if (msg == null) {
                    WxMpTransferKefuRespXMLMessage mpTransferKefuRespXMLMessage = new WxMpTransferKefuRespXMLMessage();
                    mpTransferKefuRespXMLMessage.setFromUser(mpXmlMessage.getToUser());
                    mpTransferKefuRespXMLMessage.setToUser(mpXmlMessage.getFromUser());
                    //mpTransferKefuRespXMLMessage.setTransInfo(new WxMpTransferKefuRespXMLMessage.TransInfo(""));
                    return XStreamConverter.toXml(mpTransferKefuRespXMLMessage);
                } else {
                    WxMpTextRespXmlMessage mpTextRespXmlMessage = new WxMpTextRespXmlMessage();
                    mpTextRespXmlMessage.setContent(msg);
                    mpTextRespXmlMessage.setFromUser(mpXmlMessage.getToUser());
                    mpTextRespXmlMessage.setToUser(mpXmlMessage.getFromUser());
                    return XStreamConverter.toXml(mpTextRespXmlMessage);
                }

            } catch (IOException e) {
                LOGGER.error("err: {}", e.getMessage());
            }
        }
        return WxConstants.SUCCESS.toLowerCase();
    }

    private String generateSignature(String timestamp, String nonce, String token) {
        String[] tmpArr = new String[]{ timestamp, nonce, token };
        Arrays.sort(tmpArr);
        StringBuilder sb = new StringBuilder();
        for (String s : tmpArr) {
            sb.append(s);
        }
        return DigestUtils.sha1Hex(sb.toString());
    }

}
