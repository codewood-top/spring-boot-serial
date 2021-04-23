package top.codewood.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.codewood.entity.WxId;

public interface WxIdRepository extends JpaRepository<WxId, String>, JpaSpecificationExecutor<WxId> {

    default WxId get(String openid) {return findById(openid).orElseGet(() -> null); }

    WxId getByAppidAndUserId(String appid, Long userId);
}
