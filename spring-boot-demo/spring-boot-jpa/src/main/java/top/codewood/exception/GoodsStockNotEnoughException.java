package top.codewood.exception;

public class GoodsStockNotEnoughException extends RuntimeException {

    public GoodsStockNotEnoughException() {
    }

    public GoodsStockNotEnoughException(String msg) {
        super(msg);
    }

}
