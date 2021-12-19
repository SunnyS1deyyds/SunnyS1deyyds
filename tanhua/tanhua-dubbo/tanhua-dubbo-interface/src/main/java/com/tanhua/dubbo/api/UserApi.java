package com.tanhua.dubbo.api;

import com.itheima.model.pojo.User;

public interface UserApi {

    //根据手机号查询用户
    User findByMobile(String mobile);

    //保存用户，返回用户的主键ID
    Long save(User user);
}
