package top.codewood.config.security.authentication;

import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;

public class HttpServletUtils {

    public static boolean isFromAjax(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        if (accept == null) request.getHeader("accept");
        if (accept != null && accept.startsWith(MediaType.APPLICATION_JSON_VALUE)) return true;

        String xrw = request.getHeader("X-Requested-With");
        if (xrw != null && xrw.equals("XMLHttpRequest")) return true;

        return false;
    }

}
