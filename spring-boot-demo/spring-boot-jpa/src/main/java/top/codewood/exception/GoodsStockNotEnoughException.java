package top.codewood.exception;

public class GoodsStockNotEnoughException extends Exception {

    public GoodsStockNotEnoughException() {
    }

    public GoodsStockNotEnoughException(String msg) {
        super(msg);
    }

}
