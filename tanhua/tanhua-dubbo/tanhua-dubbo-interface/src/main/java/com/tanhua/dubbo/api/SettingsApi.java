package com.tanhua.dubbo.api;

import com.itheima.model.pojo.Settings;

public interface SettingsApi {

    //根据用户id 查询通知开关
    Settings findByUserId(Long userId);

    //新增
    void save(Settings settings);

    //修改
    void update(Settings settings);
}
