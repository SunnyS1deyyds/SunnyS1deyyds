package com.tanhua.app.service;

import com.itheima.model.pojo.User;
import com.itheima.model.vo.HuanXinUserVo;
import com.tanhua.app.interceptor.UserHolder;
import com.tanhua.dubbo.api.UserApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class HuanXinService {

    @DubboReference
    private UserApi userApi;

    public HuanXinUserVo findHuanXinUser() {
        //根据用户id查询用户信息
        User user = userApi.findByUserId(UserHolder.getUserId());

        //封装vo对象
        HuanXinUserVo vo = new HuanXinUserVo(user.getHxUser(), user.getHxPassword());

        //返回结果
        return vo;

    }
}
