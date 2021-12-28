package com.tanhua.dubbo.api;

import com.itheima.model.mongo.UserLike;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;


@DubboService
public class UserLikeApiImpl implements UserLikeApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    //保存喜欢的数据  isLike 为true就是喜欢   为false就是不喜欢
    @Override
    public boolean saveLikeUser(Long userId, Long likeUserId, boolean isLike) {
        try {
            //1.  封装查询条件
            Query query = new Query(Criteria.where("userId").is(userId)
                    .and("likeUserId").is(likeUserId));

            //2.  执行当前数据条件的查询
            UserLike userLike = mongoTemplate.findOne(query, UserLike.class);

            //3 判断当前的喜欢数据是否存在
            if (userLike == null) {
                //  3.1 如果不存在，就新增
                userLike = new UserLike();
                userLike.setUserId(userId);
                userLike.setLikeUserId(likeUserId);
                userLike.setIsLike(isLike);
                userLike.setCreated(System.currentTimeMillis());
                userLike.setUpdated(System.currentTimeMillis());
                mongoTemplate.save(userLike);

            } else {
                //  3.2 如果存在，就修改
                Update update = Update.update("isLike", isLike)
                        .set("updated", System.currentTimeMillis());

                mongoTemplate.updateFirst(query, update, UserLike.class);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
