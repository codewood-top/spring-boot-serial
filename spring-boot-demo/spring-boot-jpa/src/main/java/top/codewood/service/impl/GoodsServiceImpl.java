package top.codewood.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.codewood.entity.Goods;
import top.codewood.entity.GoodsStockLog;
import top.codewood.exception.GoodsStockNotEnoughException;
import top.codewood.service.GoodsService;
import top.codewood.service.repository.GoodsRepository;
import top.codewood.service.repository.GoodsStockLogRepository;

import java.security.InvalidParameterException;

@Service("goodsService")
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private GoodsStockLogRepository goodsStockLogRepository;

    @Transactional
    @Override
    public Goods save(Goods goods) {
        return goodsRepository.save(goods);
    }

    @Override
    public Goods get(Long id) {
        return goodsRepository.get(id);
    }

    @Transactional
    @Override
    public void stockIn(Long id, int quantity, String content) {
        checkAndGet(id, quantity);
        goodsRepository.stockIn(id, quantity);
        goodsStockLogRepository.save(new GoodsStockLog(GoodsStockLog.OperationType.IN, quantity, null));
    }

    @Transactional
    @Override
    public void stockOut(Long id, int quantity, String content) throws GoodsStockNotEnoughException {
        Goods goods = checkAndGet(id, quantity);
        int effectedCount = goodsRepository.stockOut(id, quantity);
        if (effectedCount == 0) throw new GoodsStockNotEnoughException(String.format("%s 库存不足！", goods.getName()));
        goodsStockLogRepository.save(new GoodsStockLog(GoodsStockLog.OperationType.OUT, -quantity, null));
    }

    @Transactional
    @Override
    public void stockSold(Long id, int quantity, Long orderId) throws GoodsStockNotEnoughException {
        Goods goods = checkAndGet(id, quantity);
        int effectedCount = goodsRepository.stockSold(id, quantity);
        if (effectedCount == 0) throw new GoodsStockNotEnoughException(String.format("%s 库存不足！", goods.getName()));
        goodsStockLogRepository.save(new GoodsStockLog(GoodsStockLog.OperationType.OUT, -quantity, orderId));
    }

    @Transactional
    @Override
    public void stockReturn(Long id, int quantity, Long orderId) {
        checkAndGet(id, quantity);
        goodsRepository.stockReturn(id, quantity);
        goodsStockLogRepository.save(new GoodsStockLog(GoodsStockLog.OperationType.IN, -quantity, orderId));
    }

    private Goods checkAndGet(Long id, int quantity) {
        if (quantity <= 0) throw new InvalidParameterException(String.format("quantity 不能小于等于 0！"));
        Goods goods = goodsRepository.get(id);
        if (goods == null) throw new RuntimeException("无效的商品ID：" + id);
        return goods;
    }

}
