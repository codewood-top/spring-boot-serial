package top.codewood.websocket.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Component
public class RequestListener implements ServletRequestListener {

    static final Logger LOGGER = LoggerFactory.getLogger(RequestListener.class);

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        HttpSession session = ((HttpServletRequest)sre.getServletRequest()).getSession();
        LOGGER.info("将所有的request请求都携带上httpSession： {}", session.getId());
    }
}
