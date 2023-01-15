package top.codewood.wx.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.codewood.wx.util.qrcode.QRCodeUtils;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/qrcode")
public class QrcodeController {

    final Logger logger = LoggerFactory.getLogger(QrcodeController.class);

    static final String LOGO = "http://img1.codewood.top/developer/images/code-logo-large.png";

    @RequestMapping("/create")
    public void create(@RequestParam("content") String content, HttpServletResponse response) {
        try {
            QRCodeUtils.write(QRCodeUtils.create(content, LOGO),
                    response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            logger.error("wx native pay err, ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

}
