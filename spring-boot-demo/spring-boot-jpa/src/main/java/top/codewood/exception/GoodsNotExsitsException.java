package top.codewood.exception;

public class GoodsNotExsitsException extends RuntimeException {

    public GoodsNotExsitsException() {}

    public GoodsNotExsitsException(String msg) {
        super(msg);
    }

}
