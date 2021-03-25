package top.codewood.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.codewood.config.security.jwt.token.JwtTokenStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/authorize")
public class AuthorizeRestController {

    @Autowired
    private JwtTokenStore jwtTokenStore;

    /**
     *
     * @param username
     * @param password 示例暂未用上password
     * @return
     */
    @RequestMapping("/token")
    public String token(@RequestParam("username") String username,
                        @RequestParam("password") String password) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorityList.add(new SimpleGrantedAuthority("ROLE_TEST"));
        List<String> authorities = authorityList.stream().map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toList());
        claims.put("authorities", authorities);

        return jwtTokenStore.createToken(claims);
    }

}
