package top.codewood.service.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import top.codewood.entity.Goods;

public interface GoodsRepository extends BaseRepository<Goods> {

    /**
     * 销售出库，会相应变动 sold, stock字段
     * @param id
     * @param quantity
     * @return
     */
    @Modifying
    @Query("update Goods g set g.sold=g.sold+:quantity, g.stock=g.stock-:quantity where g.id=:id and g.stock>=:quantity and :quantity>0")
    int stockSold(@Param("id") Long id, @Param("quantity") int quantity);

    /**
     * 销售入库，会相应变动 sold, stock字段
     * @param id
     * @param quantity
     * @return
     */
    @Modifying
    @Query("update Goods g set g.sold=g.sold-:quantity, g.stock=g.stock+:quantity where g.id=:id and g.sold>=:quantity and :quantity>0")
    int stockReturn(@Param("id") Long id, @Param("quantity") int quantity);

    /**
     * 入库, 只变动 stock 字段
     * @param id
     * @param quantity
     * @return
     */
    @Modifying
    @Query("update Goods g set g.stock=g.stock+:quantity where g.id=:id and :quantity>0")
    int stockIn(@Param("id") Long id, @Param("quantity") int quantity);

    /**
     * 出库, 只变动 stock 字段
     * @param id
     * @param quantity
     * @return
     */
    @Modifying
    @Query("update Goods g set g.stock=g.stock-:quantity where g.id=:id and :quantity>0")
    int stockOut(@Param("id") Long id, @Param("quantity") int quantity);

}
