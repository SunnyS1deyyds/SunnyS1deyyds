package com.tanhua.app.service;

import com.itheima.model.mongo.RecommendUser;
import com.itheima.model.pojo.UserInfo;
import com.itheima.model.vo.TodayBest;
import com.tanhua.app.interceptor.UserHolder;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class TanhuaService {

    @DubboReference
    private RecommendUserApi recommendUserApi;

    @DubboReference
    private UserInfoApi userInfoApi;


    public TodayBest todayBest() {

        //获取当前用户的id
        Long userId = UserHolder.getUserId();

        //查询用户 的缘分值最高的推荐用户
        RecommendUser recommendUser = recommendUserApi.findWithMaxScore(userId);

        //根据推荐用户的id查询用户详情信息
        UserInfo userInfo = userInfoApi.findById(recommendUser.getUserId());

        //构建vo对象
        TodayBest vo = TodayBest.init(userInfo, recommendUser);

        return vo;
    }
}
