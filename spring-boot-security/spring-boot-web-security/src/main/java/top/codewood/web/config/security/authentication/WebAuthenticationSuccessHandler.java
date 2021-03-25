package top.codewood.web.config.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import top.codewood.web.config.security.authorization.AuthorizationContext;
import top.codewood.web.entity.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WebAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    static final Logger LOGGER = LoggerFactory.getLogger(WebAuthenticationSuccessHandler.class);

    private ObjectMapper objectMapper;

    {
        objectMapper = new ObjectMapper();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        LOGGER.info("authentication success");

        String username = ((org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        AuthorizationContext.setCurrentAuthenticationUser(User.getUser(username));

        if (HttpServletUtils.isFromAjax(request)) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", 0);
            map.put("message", "ok");
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(map));
            response.getWriter().flush();
            response.getWriter().close();
            return;
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }




}
