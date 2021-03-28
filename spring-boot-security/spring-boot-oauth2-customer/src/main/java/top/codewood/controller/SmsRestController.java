package top.codewood.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.codewood.config.security.oauth2.provider.sms.SmsCodeService;

@RestController
@RequestMapping("/sms")
public class SmsRestController {

    static final Logger LOGGER = LoggerFactory.getLogger(SmsRestController.class);

    @Autowired(required = false)
    private SmsCodeService smsCodeService;

    @RequestMapping("/code")
    public String code(String phone) {
        if (smsCodeService == null) throw new RuntimeException("尚未开启验证码功能！");
        String code = smsCodeService.createCode(phone);
        LOGGER.info("phone: {}, code: {}", phone, code);
        return "SUCCESS";
    }

}
