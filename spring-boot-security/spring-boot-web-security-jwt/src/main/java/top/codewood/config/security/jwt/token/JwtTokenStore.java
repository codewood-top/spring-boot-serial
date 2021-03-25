package top.codewood.config.security.jwt.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.Assert;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

public class JwtTokenStore implements InitializingBean {

    static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenStore.class);

    private String secret;
    private Key key;

    public JwtTokenStore(String secret) {
        this.secret = secret;
        try {
            this.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Map<String, Object> claims) {

        Assert.notNull(claims, "claims must not be null!");

        JwtBuilder jwtBuilder = Jwts.builder();
        claims.entrySet().forEach(kv -> jwtBuilder.claim(kv.getKey(), kv.getValue()));

        return jwtBuilder
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + (60 * 1000)))
                .compressWith(CompressionCodecs.DEFLATE)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    public Authentication getAuthentication(String token) {
        Claims claims = claimsFromToken(token);
        List<String> authorities = (List)claims.get("authorities");
        List<GrantedAuthority> authorityList = authorities.stream().map(authority -> new SimpleGrantedAuthority(authority)).collect(Collectors.toList());
        User user = new User((String) claims.get("username"), "", authorityList);
        return new UsernamePasswordAuthenticationToken(user, token, authorityList);
    }

    public Date getExpirationDateFromToken(String token) {
        final Claims claims = claimsFromToken(token);
        return claims.getExpiration();
    }

    public Claims claimsFromToken(String toekn) {
        return (Claims) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parse(toekn).getBody();
    }

    public boolean verifyToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            LOGGER.error("Invalid JWT signature, token: {}", token);
        } catch (ExpiredJwtException e) {
            LOGGER.debug("JWT token expired");
        } catch (UnsupportedJwtException e) {
            LOGGER.error("Unsupported JWT token: {}", token);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Illegal JWT token: {}", token);
        }
        return false;
    }

}
