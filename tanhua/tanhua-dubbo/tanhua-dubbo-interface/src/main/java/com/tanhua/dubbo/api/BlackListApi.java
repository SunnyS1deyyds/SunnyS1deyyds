package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface BlackListApi {

    //根据用户id分页查询黑名单
    Page findByUserId(Long userId, int page, int pagesize);

    //根据用户id和黑名单用户id删除数据
    void removeBlacklist(Long userId, Long uid);
}
