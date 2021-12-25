package com.tanhua.dubbo.api;

import com.itheima.model.mongo.Movement;
import com.itheima.model.vo.PageResult;

import java.util.List;

public interface MovementApi {

    //发布动态
    void pushlishMovement(Movement movement);

    //根据用户id分页查询用户动态   根据发布时间倒序排序
    PageResult findPageByUserId(Long userId, Integer page, Integer pagesize);

    //根据用户id  查询好友动态数据
    List<Movement> findFriendMovements(Long friendId, Integer page, Integer pagesize);

    //获取10条随机动态数据
    List<Movement> randomMovement();

    //根据多个  pid  查询动态数据
    List<Movement> findByPids(List<Long> pids);

    //根据动态id  查询动态数据
    Movement findById(String movementId);
}
