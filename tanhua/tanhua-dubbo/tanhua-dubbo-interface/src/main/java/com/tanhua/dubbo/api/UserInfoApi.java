package com.tanhua.dubbo.api;

import com.itheima.model.pojo.UserInfo;

public interface UserInfoApi {
    //新增
    void save(UserInfo userInfo);

    //根据id修改
    void update(UserInfo userInfo);

    //根据id查询
    UserInfo findById(Long userId);
}
