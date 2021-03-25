package top.codewood.config.security.jwt.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import top.codewood.config.security.jwt.token.JwtTokenStore;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter  extends OncePerRequestFilter {

    static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private JwtTokenStore jwtTokenStore;

    public JwtAuthenticationFilter(JwtTokenStore jwtTokenStore) {
        this.jwtTokenStore = jwtTokenStore;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token == null) {
            token = request.getParameter("token");
        }

        if (StringUtils.hasText(token) && jwtTokenStore.verifyToken(token)) {
            Authentication authentication = jwtTokenStore.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            LOGGER.debug("current uri: invalid  token: {}", request.getRequestURI(), token);
        }

        filterChain.doFilter(request, response);

    }
}
