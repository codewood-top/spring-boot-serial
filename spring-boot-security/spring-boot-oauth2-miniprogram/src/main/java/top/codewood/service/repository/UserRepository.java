package top.codewood.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.codewood.entity.User;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    default User get(Long id) {return findById(id).orElseGet(() -> null); }

    User getByUsername(String username);

}
