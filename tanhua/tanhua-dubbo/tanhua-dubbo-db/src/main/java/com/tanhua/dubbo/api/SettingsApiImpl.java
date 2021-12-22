package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.model.pojo.Settings;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.dubbo.mapper.SettingsMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class SettingsApiImpl implements SettingsApi {

    @Autowired
    private SettingsMapper settingsMapper;

    @Override
    public Settings findByUserId(Long userId) {
        return settingsMapper.selectOne(
                Wrappers.lambdaQuery(Settings.class)
                        .eq(Settings::getUserId, userId)
        );
    }

    @Override
    public void save(Settings settings) {
        settingsMapper.insert(settings);
    }

    @Override
    public void update(Settings settings) {
        settingsMapper.updateById(settings);
    }
}
