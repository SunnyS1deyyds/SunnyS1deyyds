package com.tanhua.dubbo.api;

import com.itheima.model.mongo.Movement;
import com.itheima.model.vo.PageResult;

public interface MovementApi {

    //发布动态
    void pushlishMovement(Movement movement);

    //根据用户id分页查询用户动态   根据发布时间倒序排序
    PageResult findPageByUserId(Long userId, Integer page, Integer pagesize);
}
