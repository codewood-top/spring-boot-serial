package top.codewood.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.codewood.service.OAuthService;

@Controller
public class IndexController {

    @Autowired
    private OAuthService oAuthService;

    @GetMapping({"/"})
    public String index() {
        return "index/index";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "redirect_uri", required = false) String redirectUri, Model model) {
        model.addAttribute("redirectUri", redirectUri);
        return "index/login";
    }

    @GetMapping("/oauth")
    public String oauth(String code, Model model) {
        if (code == null) throw new RuntimeException("参数错误");
        OAuthService.CodeAuth codeAuth = oAuthService.getCodeAuth(code);
        oAuthService.setCode(code, ((org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        model.addAttribute("codeAuth", codeAuth);
        return "index/oauth";
    }

}
