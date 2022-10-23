package top.codewood.service.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.codewood.entity.User;

import java.util.List;

@Mapper
public interface UserRepository {

    @Select("select * from t_user")
    List<User> findAll();

    @Select("select * from t_user where id=#{id}")
    User get(Long id);

}
