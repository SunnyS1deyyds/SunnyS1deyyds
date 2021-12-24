package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.model.dto.RecommendUserDto;
import com.itheima.model.pojo.UserInfo;
import com.tanhua.dubbo.mapper.UserInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService
public class UserInfoApiImpl implements UserInfoApi {

    @Autowired
    private UserInfoMapper userInfoMapper;


    @Override
    public void save(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    @Override
    public void update(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public UserInfo findById(Long userId) {
        return userInfoMapper.selectById(userId);
    }

    @Override
    public Page<UserInfo> findPage(List<Long> ids, RecommendUserDto dto) {
        //1 设置查询条件
        //1.1 设置查询的用户ids
        LambdaQueryWrapper<UserInfo> wrapper = Wrappers.lambdaQuery(UserInfo.class)
                .in(UserInfo::getId, ids);

        //1.2 判断dto是否不为空
        if (dto != null) {
            //不为空，设置查询条件
            //性别  isNotBlank:认为空格是空    isNotEmpty：认为空格不是空
            wrapper.eq(StringUtils.isNotBlank(dto.getGender()), UserInfo::getGender, dto.getGender());
            //年龄
            wrapper.lt(dto.getAge() != null, UserInfo::getAge, dto.getAge());

            //自己编写其他的条件。。
        }

        //2 设置分页对象
        Page<UserInfo> pages = new Page<>(dto.getPage(), dto.getPagesize());

        //3 执行分页查询
        pages = userInfoMapper.selectPage(pages, wrapper);

        //返回结果
        return pages;
    }
}
