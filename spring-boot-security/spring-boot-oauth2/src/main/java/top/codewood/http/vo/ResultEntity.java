package top.codewood.http.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.transform.Result;
import java.io.Serializable;
import java.util.StringJoiner;

public class ResultEntity<T> implements Serializable {

    private int code;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private T data;

    public ResultEntity(){}

    public ResultEntity(int code, String message) {
        this.code = code;
        this.message = message;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultEntity{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public static ResultEntity failure(int code, String message) {
        return new ResultEntity(code, message);
    }

    public static ResultEntity success() {
        return new ResultEntity(0, "success");
    }

    public static <T> ResultEntity<T> success(T data) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setData(data);
        return resultEntity;
    }

    public static <T> ResultEntity<T> success(T data, String message) {
        ResultEntity resultEntity = new ResultEntity(0, message);
        resultEntity.setData(data);
        return resultEntity;
    }

}
