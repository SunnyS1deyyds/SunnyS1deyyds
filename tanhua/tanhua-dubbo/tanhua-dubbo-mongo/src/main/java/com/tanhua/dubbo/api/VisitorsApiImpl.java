package com.tanhua.dubbo.api;

import com.itheima.model.mongo.Visitors;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class VisitorsApiImpl implements VisitorsApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    //保存访客信息  一个用户的访问信息，一天只保存一条
    @Override
    public void save(Visitors visitors) {
        //封装查询条件   查询用户当日有没有访客信息
        Query query = new Query(Criteria.where("userId").is(visitors.getUserId())
                .and("visitorUserId").is(visitors.getVisitorUserId())
                .and("visitDate").is(visitors.getVisitDate()));

        //判断  当日内有没有这个用户的访问记录
        if (!mongoTemplate.exists(query, Visitors.class)) {
            //没有数据就保存
            mongoTemplate.save(visitors);
        }

    }

    //根据用户in系，访问时间和查询条数  查询最近访客信息
    @Override
    public List<Visitors> queryVisitorsList(Long userId, Long date, int count) {
        Criteria criteria = Criteria.where("userId").is(userId);

        if (date != null) {
            criteria.and("date").gte(date);
        }

        return mongoTemplate.find(new Query(criteria)
                .limit(5)
                .with(Sort.by(Sort.Order.desc("date"))), Visitors.class);
    }
}
