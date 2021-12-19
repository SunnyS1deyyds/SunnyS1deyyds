package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.model.pojo.User;
import com.tanhua.dubbo.mapper.UserMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class UserApiImpl implements UserApi {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByMobile(String mobile) {
        //return userMapper.selectOne(
        //        Wrappers.lambdaQuery(User.class)
        //                .eq(User::getMobile, mobile)
        //);

        //LambdaQueryWrapper wrapper = new LambdaQueryWrapper();
        //wrapper.eq("mobile", mobile);
        //User user = userMapper.selectOne(wrapper);
        //return user;

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>();
        wrapper.eq(User::getMobile, mobile);
        User user = userMapper.selectOne(wrapper);
        return user;
    }

    @Override
    public Long save(User user) {
        userMapper.insert(user);
        return user.getId();
    }
}
