package top.codewood.config.security.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 主要拦截/oauth/token请求中的header
 * 可做默认添加，即客户端获取access_token是时候不用带Authorization请求头
 * 也可做校验，如果客户端获取access_token没带合法的client-id&client-secret，则异常返回
 */
public class ClientDetailsAuthenticationFilter extends OncePerRequestFilter {

    private ClientDetailsService clientDetailsService;
    private PasswordEncoder passwordEncoder;

    public ClientDetailsAuthenticationFilter(ClientDetailsService clientDetailsService, PasswordEncoder passwordEncoder) {
        this.clientDetailsService = clientDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!request.getRequestURI().equals("/oauth/token")) {
            filterChain.doFilter(request, response);
            return;
        }

        ClientDetails clientDetails = extractClientDetails(request);
        if (clientDetails == null) {
            //throw new AppOAuth2Exception("未授权应用！");

            //clientDetails = clientDetailsService.loadClientByClientId("first-client");
            //UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(clientDetails.getClientId(), clientDetails.getClientSecret(), clientDetails.getAuthorities());
            //SecurityContextHolder.getContext().setAuthentication(token);
        }
        filterChain.doFilter(request, response);
    }

    /**
     *
     * @param request
     * @return
     */
    private ClientDetails extractClientDetails(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null) return null;

        String basicStr = authorization.substring(0, 5);
        if (!"basic".equalsIgnoreCase(basicStr)) return null;

        authorization = authorization.substring(6);
        authorization = new String(Base64.getDecoder().decode(authorization.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        String[] clientInfos = authorization.split(":");
        if (clientInfos.length != 2) return null;

        String clientId = clientInfos[0], clientSecret = clientInfos[1];

        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);

        if (clientDetails != null && passwordEncoder.matches(clientSecret, clientDetails.getClientSecret())) {
            return clientDetails;
        }
        return null;
    }


}
