package top.codewood.service.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import top.codewood.entity.Order;

import java.math.BigDecimal;

public interface OrderRepository extends BaseRepository<Order> {

    Order getByOrderNumber(String orderNumber);

    @Modifying
    @Query("update Order o set o.total=:total where o.id=:id")
    int updateTotal(@Param("id") Long id, @Param("total") BigDecimal total);

}
