package top.codewood.service;

import top.codewood.entity.Order;
import top.codewood.exception.GoodsStockNotEnoughException;
import top.codewood.service.bean.order.OrderParam;

public interface OrderService {

    Order create(OrderParam orderParam) throws GoodsStockNotEnoughException;

    Order get(String orderNumber);

}
