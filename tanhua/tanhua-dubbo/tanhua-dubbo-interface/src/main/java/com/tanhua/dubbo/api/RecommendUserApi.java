package com.tanhua.dubbo.api;

import com.itheima.model.mongo.RecommendUser;

public interface RecommendUserApi {

    //查询今日佳人数据
    RecommendUser findWithMaxScore(Long toUserId);

}
