package top.codewood.service;

import top.codewood.entity.Order;
import top.codewood.service.bean.order.OrderParam;

public interface OrderService {

    /**
     * 如库存不足，应跑 {@link top.codewood.exception.GoodsStockNotEnoughException}
     * @param orderParam
     * @return
     */
    Order create(OrderParam orderParam);

    Order get(String orderNumber);

}
