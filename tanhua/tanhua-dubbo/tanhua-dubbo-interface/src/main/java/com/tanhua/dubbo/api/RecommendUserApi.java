package com.tanhua.dubbo.api;

import com.itheima.model.mongo.RecommendUser;

import java.util.List;

public interface RecommendUserApi {

    //查询今日佳人数据
    RecommendUser findWithMaxScore(Long toUserId);

    //查询用户的推荐好友数据
    List<RecommendUser> findRecommendUser(Long toUserId);

    //根据自己的用户Id  和   推荐用户的ID  查询推荐用户
    RecommendUser findRecommendUser(Long toUserId, Long userId);

    //根据用户id查询推荐用户，排除喜欢/不喜欢 的用户   最多查询10个用户
    List<RecommendUser> queryCardsList(Long userId, int count);
}
