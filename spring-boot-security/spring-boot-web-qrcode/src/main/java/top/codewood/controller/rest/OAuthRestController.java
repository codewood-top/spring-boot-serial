package top.codewood.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.codewood.service.OAuthService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth/rest")
public class OAuthRestController {

    @Autowired
    private OAuthService oAuthService;

    @RequestMapping("code_url")
    public Map<String, Object> codeUrl() {
        String code = oAuthService.generateCode();
        String codeUrl = String.format("http://192.168.0.121:8080/oauth?code=%s", code);

        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("codeUrl", codeUrl);
        return map;
    }

    @RequestMapping("get_code_state")
    public Object getCodeState(String code) {
        OAuthService.CodeAuth codeAuth = oAuthService.getCodeAuth(code);
        Map<String, Object> map = new HashMap<>();
        map.put("status", codeAuth.getStatus());

        String message = null;
        if (codeAuth.getStatus() == 0) {
            message = "等待扫描";
        } else if (codeAuth.getStatus() == 1) {
            message = "已确认";
        } else if (codeAuth.getStatus() == 2) {
            message = "已扫描，等待确认";
        } else if (codeAuth.getStatus() == -1) {
            message = "二维码已过期";
        }
        map.put("message", message);

        return map;
    }

    @RequestMapping("confirm")
    public Object confirm(String code) {
        oAuthService.confirmCode(code);
        return "ok";
    }

}
