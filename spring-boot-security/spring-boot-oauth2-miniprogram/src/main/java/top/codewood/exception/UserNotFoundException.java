package top.codewood.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("用户不存在！");
    }

    public UserNotFoundException(String msg) {
        super(msg);
    }

}
