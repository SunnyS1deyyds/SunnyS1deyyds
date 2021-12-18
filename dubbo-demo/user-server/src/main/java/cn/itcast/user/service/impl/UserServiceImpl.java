package cn.itcast.user.service.impl;


import cn.itcast.user.mapper.UserMapper;
import cn.itcast.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    public String queryUsernameById(Long id) {
        return userMapper.queryUsernameById(id);
    }
}