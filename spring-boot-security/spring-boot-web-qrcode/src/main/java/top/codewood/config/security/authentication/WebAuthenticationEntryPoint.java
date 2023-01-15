package top.codewood.config.security.authentication;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 此类是在SpringSecurity跳转登录页的时候带上源链接，以便登录成功后跳转回目标链接
 */
public class WebAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    /**
     * @param loginFormUrl URL where the login page can be found. Should either be
     *                     relative to the web-app context path (include a leading {@code /}) or an absolute
     *                     URL.
     */
    public WebAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    protected String buildRedirectUrlToLoginPage(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        String initRedirectUrl = super.buildRedirectUrlToLoginPage(request, response, authException);

        String redirectUri = null;
        try {
            redirectUri = request.getRequestURL().toString();
            if (StringUtils.hasText(request.getQueryString())) {
                redirectUri += "?" + request.getQueryString();
            }
            redirectUri =  URLEncoder.encode(redirectUri, "utf-8");
            return initRedirectUrl + "?redirect_uri=" + redirectUri;
        } catch (UnsupportedEncodingException e) {
            return initRedirectUrl;
        }
    }
}
