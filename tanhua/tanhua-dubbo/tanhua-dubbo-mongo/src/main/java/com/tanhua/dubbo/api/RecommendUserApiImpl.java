package com.tanhua.dubbo.api;

import com.itheima.model.mongo.RecommendUser;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

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

    @Override
    public List<RecommendUser> findRecommendUser(Long toUserId) {
        //封装查询条件
        Query query = new Query(Criteria.where("toUserId").is(toUserId));

        //执行查询
        List<RecommendUser> recommendUserList = mongoTemplate.find(query, RecommendUser.class);

        //返回结果
        return recommendUserList;
    }

    @Override
    public RecommendUser findRecommendUser(Long toUserId, Long userId) {
        //1 封装查询条件
        Query query = new Query(Criteria.where("toUserId").is(toUserId).and("userId").is(userId));

        //2 执行查询
        RecommendUser recommendUser = mongoTemplate.findOne(query, RecommendUser.class);

        //3 如果没有查询到推荐好友(推荐系统没有推荐)，手动设置缘分值95分
        if (recommendUser == null) {
            recommendUser = new RecommendUser();
            recommendUser.setUserId(userId);
            recommendUser.setToUserId(toUserId);
            recommendUser.setScore(95d);
        }

        return recommendUser;
    }
}
