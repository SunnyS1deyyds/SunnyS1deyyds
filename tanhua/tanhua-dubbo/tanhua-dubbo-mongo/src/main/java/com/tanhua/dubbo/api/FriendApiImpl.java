package com.tanhua.dubbo.api;

import com.itheima.model.mongo.Friend;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@DubboService
public class FriendApiImpl implements FriendApi {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public void addContact(Long userId, Long friendId) {
        //1. 把别人加为我的好友
        Query query1 = new Query(Criteria.where("userId").is(userId).and("friendId").is(friendId));
        //判断别人是否已经为我的好友
        if (!mongoTemplate.exists(query1, Friend.class)) {
            //如果不是我的好友，就添加好友
            Friend friend = new Friend();
            friend.setUserId(userId);
            friend.setFriendId(friendId);
            friend.setCreated(new Date().getTime());

            mongoTemplate.save(friend);
        }

        //2. 别人加我为好友
        Query query2 = new Query(Criteria.where("userId").is(friendId).and("friendId").is(userId));
        //判断我是否已经为别人的好友
        if (!mongoTemplate.exists(query2, Friend.class)) {
            //如果不是别人的好友，就添加好友
            Friend friend = new Friend();
            friend.setUserId(friendId);
            friend.setFriendId(userId);
            friend.setCreated(new Date().getTime());

            mongoTemplate.save(friend);
        }
    }

    @Override
    public List<Long> findFriends(Long userId) {
        List<Friend> friendList = mongoTemplate.find(new Query(
                Criteria.where("userId").is(userId)
        ), Friend.class);

        return friendList.stream().map(Friend::getFriendId).collect(Collectors.toList());
    }
}
