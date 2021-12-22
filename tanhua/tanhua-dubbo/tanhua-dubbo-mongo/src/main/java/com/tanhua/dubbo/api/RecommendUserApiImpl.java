package com.tanhua.dubbo.api;

import com.itheima.model.mongo.RecommendUser;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@DubboService
public class RecommendUserApiImpl implements RecommendUserApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public RecommendUser findWithMaxScore(Long toUserId) {

        //创建查询条件
        Criteria criteria = Criteria.where("toUserId").is(toUserId);

        //创建query查询对象  ，根据分值倒序排序，返回1条数据
        Query query = new Query(criteria)
                .limit(1)
                .with(Sort.by(Sort.Order.desc("score")));

        //执行查询
        RecommendUser recommendUser = mongoTemplate.findOne(query, RecommendUser.class);

        //判断是否查询到结果，
        if (recommendUser == null) {
            //如果查询不到结果，获取分值最高的推荐用户
            query = new Query()
                    .limit(1)
                    .with(Sort.by(Sort.Order.desc("score")));
            recommendUser = mongoTemplate.findOne(query, RecommendUser.class);
        }

        return recommendUser;
    }
}
