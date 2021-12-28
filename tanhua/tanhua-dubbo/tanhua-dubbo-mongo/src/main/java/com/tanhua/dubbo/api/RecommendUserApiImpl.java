package com.tanhua.dubbo.api;

import com.itheima.model.mongo.RecommendUser;
import com.itheima.model.mongo.UserLike;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.stream.Collectors;

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

    //根据用户id查询推荐用户，排除喜欢/不喜欢 的用户   最多查询10个用户
    @Override
    public List<RecommendUser> queryCardsList(Long userId, int count) {
        //1 获取当前用户的  喜欢/不喜欢的数据
        List<UserLike> userLikeList = mongoTemplate.find(new Query(
                Criteria.where("userId").is(userId)
        ), UserLike.class);

        //2 喜欢/不喜欢的数据转为   ids  用户的ids
        List<Long> userLikeIds = userLikeList.stream()
                .map(UserLike::getLikeUserId).collect(Collectors.toList());

        //3 设置统计的查询条件  查询当前用户的推荐好友  排除喜欢/不喜欢的用户
        Criteria criteria = Criteria.where("toUserId").is(userId).and("userId").nin(userLikeIds);

        //4 统计条件是   排除喜欢/不喜欢的用户    最多查询10个用户
        TypedAggregation<?> aggregation = Aggregation.newAggregation(
                RecommendUser.class,
                Aggregation.match(criteria),       //统计的条件
                Aggregation.sample(10)  //统计返回的数据条数,必须设置在最后
        );

        //5 使用mongoTemplate的统计方法执行查询
        AggregationResults<RecommendUser> results = mongoTemplate.aggregate(aggregation, RecommendUser.class);

        //返回结果
        return results.getMappedResults();
    }
}
