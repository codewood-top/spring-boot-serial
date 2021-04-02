package top.codewood.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.codewood.entity.User;
import top.codewood.service.UserService;
import top.codewood.service.bean.page.JpaHelper;
import top.codewood.service.bean.page.PageEntity;
import top.codewood.service.bean.page.PageInfo;
import top.codewood.service.repository.UserRepository;

@Service("userService")
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User get(Long id) {
        return userRepository.get(id);
    }

    @Override
    public PageEntity<User> list(PageInfo pageInfo) {
        Page<User> page = userRepository.findAll(JpaHelper.getSpecification(pageInfo), JpaHelper.getPageable(pageInfo));
        return new PageEntity<>(pageInfo, page.getContent(), page.getTotalElements());
    }
}
