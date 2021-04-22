package top.codewood.controller.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionController {

    static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionController.class);

    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public Map<String, Object> bindException(BindException e) {
        List<String> errMsgs = e.getBindingResult().getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>();
        map.put("code", 500);
        map.put("message", errMsgs);
        return map;
    }

    @ResponseBody
    @ExceptionHandler(value = ConstraintViolationException.class)
    public Map<String, Object> constraintViolationException(ConstraintViolationException e) {
        List<String> errMsgs = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        map.put("code", 500);
        map.put("message", errMsgs);
        return map;
    }


    @ResponseBody
    @ExceptionHandler(value = IllegalStateException.class)
    public Map<String, Object> IllegalStateException(IllegalStateException e) {
        LOGGER.error("exception: {}", e.getMessage());
        Map<String, Object> map = new HashMap<>();
        map.put("code", 500);
        map.put("message", "请求参数状态异常");
        return map;
    }

    @ResponseBody
    @ExceptionHandler
    public Map<String, Object> exception(Exception e) {
        LOGGER.error("exception: {}", e.getClass());
        Map<String, Object> map = new HashMap<>();
        map.put("code", 500);
        map.put("message", e.getMessage());
        return map;
    }

}
