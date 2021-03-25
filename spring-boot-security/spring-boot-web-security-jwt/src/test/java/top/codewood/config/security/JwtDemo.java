package top.codewood.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import top.codewood.config.security.jwt.token.JwtTokenStore;

import java.util.*;
import java.util.stream.Collectors;

public class JwtDemo {

    @Test
    public void createTokenTest() {
        String secret = UUID.randomUUID().toString().replaceAll("-", "");
        secret += UUID.randomUUID().toString().replaceAll("-", "");

        JwtTokenStore jwtTokenStore = new JwtTokenStore(secret);
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", "test");
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorityList.add(new SimpleGrantedAuthority("ROLE_TEST"));
        List<String> authorities = authorityList.stream().map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toList());

        claims.put("authorities", authorities);
        String token = jwtTokenStore.createToken(claims);

        System.out.println("token: " + token);

        boolean verifyResult = jwtTokenStore.verifyToken(token);
        System.out.println("verify token: " + verifyResult);

        claims = jwtTokenStore.claimsFromToken(token);

        List<String> authoritiesObj = (List)claims.get("authorities");
        authoritiesObj.stream().forEach(System.out::println);

        System.out.println("claims: " + claims);

    }


}
