package com.tanhua.dubbo.service;

import com.itheima.model.mongo.Friend;
import com.itheima.model.mongo.MovementTimeLine;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MovementTimeLineService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Async
    public void saveMovementTimeLine(Long userId, ObjectId movementId) {
        //查询好友数据
        Query query = new Query(Criteria.where("userId").is(userId));
        List<Friend> friendList = mongoTemplate.find(query, Friend.class);

        try {
            //模拟时间线保存的时间很长
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
        }

        //保存时间线数据
        for (Friend friend : friendList) {
            MovementTimeLine timeLine = new MovementTimeLine();
            timeLine.setMovementId(movementId);
            timeLine.setUserId(userId);
            timeLine.setFriendId(friend.getFriendId());
            timeLine.setCreated(new Date().getTime());

            mongoTemplate.save(timeLine);
        }
    }
}
