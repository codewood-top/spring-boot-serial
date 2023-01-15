package top.codewood.config.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WebAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    static final Logger LOGGER = LoggerFactory.getLogger(WebAuthenticationFailureHandler.class);

    private ObjectMapper objectMapper;

    {
        objectMapper = new ObjectMapper();
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        LOGGER.info("authentication failure: {}", exception.getMessage());

        if (HttpServletUtils.isFromAjax(request)) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", 404);
            map.put("message", exception.getMessage());
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(map));
            response.getWriter().flush();
            response.getWriter().close();
            return;
        }

        super.onAuthenticationFailure(request, response, exception);

    }
}
