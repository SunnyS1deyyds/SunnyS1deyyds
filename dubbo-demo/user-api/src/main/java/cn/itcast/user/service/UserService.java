package cn.itcast.user.service;


import cn.itcast.user.pojo.User;

public interface UserService {

    String queryUsernameById(Long id);

    User queryById(Long id);
}
