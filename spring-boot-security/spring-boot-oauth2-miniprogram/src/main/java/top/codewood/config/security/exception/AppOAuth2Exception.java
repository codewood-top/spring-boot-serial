package top.codewood.config.security.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

@JsonSerialize(using = AppOAuth2ExceptionSerializer.class)
public class AppOAuth2Exception extends OAuth2Exception {

    private Integer code;

    public AppOAuth2Exception(String msg) {
        super(msg);
    }

    public AppOAuth2Exception(String msg, Integer code) {
        super(msg);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
