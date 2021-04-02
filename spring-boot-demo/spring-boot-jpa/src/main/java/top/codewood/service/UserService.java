package top.codewood.service;

import top.codewood.entity.User;
import top.codewood.service.bean.page.PageEntity;
import top.codewood.service.bean.page.PageInfo;

public interface UserService {

    User save(User user);

    User get(Long id);

    PageEntity<User> list(PageInfo pageInfo);
}
