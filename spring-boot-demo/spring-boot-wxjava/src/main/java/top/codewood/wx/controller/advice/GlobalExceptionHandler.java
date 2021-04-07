package top.codewood.wx.controller.advice;

import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import top.codewood.wx.common.bean.error.WxErrorException;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler({WxErrorException.class})
    public Object wxError(WxErrorException exception) {
        return errorMap(500, exception.getMessage());
    }

    @ExceptionHandler({RuntimeException.class})
    public Map<String, Object> runtimeException(RuntimeException e) {
        return errorMap(500, e.getMessage());
    }

    @ExceptionHandler({Exception.class})
    public Map<String, Object> exception(Exception e) {
        if (e instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException ne = (MissingServletRequestParameterException) e;
            return errorMap(400, "缺少参数：" + ne.getParameterName());
        }
        return errorMap(500, e.getMessage());
    }

    protected Map<String, Object> errorMap(int code, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("message", message);
        return map;
    }

}
