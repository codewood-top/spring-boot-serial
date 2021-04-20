package top.codewood.security;

import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter extends BasicHttpAuthenticationFilter {

    static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);

    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("Authorization");
        return authorization != null;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("Authorization");

        JwtToken token = new JwtToken(authorization);

        // 如果token异常
        getSubject(request, response).login(token);
        return true;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginAttempt(request, response)) {
            try {
                executeLogin(request, response);
            } catch (Exception e) {
                response401(request, response);
            }
        }
        return true;
    }

    /**
     * 跨域支持
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        resp.setHeader("Access-control-Allow-Origin", req.getHeader("Origin"));
        resp.setHeader("Access-Control-Allow-Methods", "GET,POST, OPTIONS,PUT,DELETE");
        resp.setHeader("Access-Control-Allow-Headers", req.getHeader("Access-Control-Request-Headers"));

        //  跨域options请求, 直接返回正常状态
        if (req.getMethod().equals(RequestMethod.OPTIONS.name())) {
            resp.setStatus(HttpStatus.OK.value());
            return false;
        }

        return super.preHandle(request, response);
    }

    private void response401(ServletRequest request, ServletResponse response) {
        try {
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.sendRedirect("/401");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
