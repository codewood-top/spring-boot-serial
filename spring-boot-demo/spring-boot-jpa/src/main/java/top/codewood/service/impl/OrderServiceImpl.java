package top.codewood.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.codewood.entity.Goods;
import top.codewood.entity.Order;
import top.codewood.entity.OrderItem;
import top.codewood.exception.GoodsNotExsitsException;
import top.codewood.exception.GoodsStockNotEnoughException;
import top.codewood.service.GoodsService;
import top.codewood.service.OrderService;
import top.codewood.service.bean.order.OrderParam;
import top.codewood.service.repository.OrderItemRepository;
import top.codewood.service.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private GoodsService goodsService;

    @Transactional
    @Override
    public Order create(OrderParam orderParam) throws GoodsStockNotEnoughException {

        Order order = new Order();
        order.setOrderNumber(orderParam.getOrderNumber());
        order.setName(orderParam.getName());
        order.setTelephone(orderParam.getTelephone());
        order.setProvince(orderParam.getProvince());
        order.setCity(orderParam.getCity());
        order.setTown(orderParam.getTown());
        order.setAddress(orderParam.getAddress());
        order.setUserId(orderParam.getUserId());

        order = orderRepository.save(order);

        BigDecimal total = new BigDecimal(0);

        for (OrderParam.ItemParam itemParam : orderParam.getItems()) {
            Goods goods = Optional.ofNullable(goodsService.get(itemParam.getGoodsId())).orElseThrow(() -> new GoodsNotExsitsException("无效商品Id: " + itemParam.getGoodsId()));
            goodsService.stockSold(goods.getId(), itemParam.getQuantity(), order.getId());
            OrderItem orderItem = new OrderItem();
            orderItem.setGoodsId(goods.getId());
            orderItem.setOrderId(order.getId());
            orderItem.setPrice(goods.getPrice());
            orderItem.setQuantity(itemParam.getQuantity());
            orderItem.setName(goods.getName());
            orderItemRepository.save(orderItem);
            total = total.add(goods.getPrice().multiply(new BigDecimal(itemParam.getQuantity())));
        }

        orderRepository.updateTotal(order.getId(), total);

        return order;
    }

    @Override
    public Order get(String orderNumber) {
        return orderRepository.getByOrderNumber(orderNumber);
    }

}
