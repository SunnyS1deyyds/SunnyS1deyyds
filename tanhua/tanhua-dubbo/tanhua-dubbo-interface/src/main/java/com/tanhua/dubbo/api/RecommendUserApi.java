package com.tanhua.dubbo.api;

import com.itheima.model.mongo.RecommendUser;

import java.util.List;

public interface RecommendUserApi {

    //查询今日佳人数据
    RecommendUser findWithMaxScore(Long toUserId);

    //查询用户的推荐好友数据
    List<RecommendUser> findRecommendUser(Long toUserId);
}
