package top.codewood.controller.bean;

import java.io.Serializable;

public class ResultEntity<T> implements Serializable {

    private int code;
    private String message;
    private T data;

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

    public static ResultEntity failure(int code, String message) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setCode(code);
        resultEntity.setMessage(message);
        return resultEntity;
    }

    public static ResultEntity success() {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setMessage("ok");
        return resultEntity;
    }

    public static <T> ResultEntity<T> success(T data) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setData(data);
        return resultEntity;
    }

}
