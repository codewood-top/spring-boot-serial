package top.codewood.service;

import top.codewood.entity.Goods;
import top.codewood.exception.GoodsStockNotEnoughException;

public interface GoodsService {

    Goods save(Goods goods);

    Goods get(Long id);

    void stockIn(Long id, int quantity, String content);

    void stockOut(Long id, int quantity, String content) throws GoodsStockNotEnoughException;

    void stockSold(Long id, int quantity, Long orderId) throws GoodsStockNotEnoughException;

    void stockReturn(Long id, int quantity, Long orderId);

}
