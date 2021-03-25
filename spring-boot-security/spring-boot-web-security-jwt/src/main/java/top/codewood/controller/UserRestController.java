package top.codewood.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/user")
public class UserRestController {

    @RequestMapping("/authentication")
    public Authentication authentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
