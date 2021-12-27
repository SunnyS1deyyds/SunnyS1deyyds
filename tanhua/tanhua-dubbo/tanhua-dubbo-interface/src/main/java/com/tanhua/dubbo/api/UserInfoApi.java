package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.model.dto.RecommendUserDto;
import com.itheima.model.pojo.UserInfo;

import java.util.List;

public interface UserInfoApi {
    //新增
    void save(UserInfo userInfo);

    //根据id修改
    void update(UserInfo userInfo);

    //根据id查询
    UserInfo findById(Long userId);

    //根据用户id和dto条件，分页查询数据
    Page<UserInfo> findPage(List<Long> ids, RecommendUserDto dto);

    //根据用户ids查询用户详情
    List<UserInfo> findByUserIds(List<Long> ids);

    //根据昵称的关键词 和用户ids 分页查询用户详情
    Page<UserInfo> findPage(List<Long> friendIds, Integer page, Integer pagesize, String keyword);
}
