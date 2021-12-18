package cn.itcast.user.service;

import cn.itcast.user.mapper.UserMapper;
import cn.itcast.user.pojo.User;
import cn.itcast.user.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

//@Service
@DubboService(version = "2.0")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public String queryUsernameById(Long id) {
        return userMapper.queryUsernameById(id) + ":重大更新2.1";

    }

    @Override
    public User queryById(Long id) {
        return userMapper.queryById(id);

    }
}
