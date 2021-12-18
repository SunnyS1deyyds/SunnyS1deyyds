package cn.itcast.user.service;


import cn.itcast.dubbo.pojo.User;

public interface UserService {

    User queryById(Long id);
}
