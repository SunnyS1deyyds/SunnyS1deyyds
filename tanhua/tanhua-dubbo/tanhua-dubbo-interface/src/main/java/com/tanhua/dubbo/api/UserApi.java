package com.tanhua.dubbo.api;

import com.itheima.model.pojo.User;

import java.util.List;

public interface UserApi {

    //根据手机号查询用户
    User findByMobile(String mobile);

    //保存用户，返回用户的主键ID
    Long save(User user);

    //根据id更新用户
    void update(User user);

    //查询所有用户
    List<User> findAll();

    //根据用户id查询用户信息
    User findByUserId(Long userId);

    //根据环信id查询用户信息
    User findByHanXinId(String huanxinId);
}
