package cn.itcast.user.api;


import cn.itcast.dubbo.pojo.User;

public interface UserService {

    User queryById(Long id);
}
