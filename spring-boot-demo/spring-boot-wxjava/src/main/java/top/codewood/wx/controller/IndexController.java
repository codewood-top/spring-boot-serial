package top.codewood.wx.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    static Logger logger = LoggerFactory.getLogger(IndexController.class);

    @ResponseBody
    @RequestMapping("/MP_verify_{code}.txt")
    public String wxMpTxtVerify(@PathVariable("code") String code) {
        logger.debug("code: {}", code);
        return code;
    }

}
