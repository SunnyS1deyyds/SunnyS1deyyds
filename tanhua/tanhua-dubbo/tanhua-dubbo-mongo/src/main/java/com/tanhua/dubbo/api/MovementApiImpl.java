package com.tanhua.dubbo.api;

import com.itheima.model.mongo.Friend;
import com.itheima.model.mongo.Movement;
import com.itheima.model.mongo.MovementTimeLine;
import com.itheima.model.vo.PageResult;
import com.tanhua.dubbo.service.MovementTimeLineService;
import com.tanhua.dubbo.utils.IdWorker;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List;

@DubboService
public class MovementApiImpl implements MovementApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MovementTimeLineService movementTimeLineService;

    @Autowired
    private IdWorker idWorker;

    @Override
    public void pushlishMovement(Movement movement) {
        //保存动态数据
        movement.setPid(idWorker.getNextId("movement"));
        mongoTemplate.save(movement);

        ////查询好友数据
        //Query query = new Query(Criteria.where("userId").is(movement.getUserId()));
        //List<Friend> friendList = mongoTemplate.find(query, Friend.class);
        //
        ////保存时间线数据
        //for (Friend friend : friendList) {
        //    MovementTimeLine timeLine = new MovementTimeLine();
        //    timeLine.setMovementId(movement.getId());
        //    timeLine.setUserId(movement.getUserId());
        //    timeLine.setFriendId(friend.getFriendId());
        //    timeLine.setCreated(new Date().getTime());
        //
        //    mongoTemplate.save(timeLine);
        //}
        movementTimeLineService.saveMovementTimeLine(movement.getUserId(), movement.getId());
    }

    //根据用户id分页查询用户动态   根据发布时间倒序排序
    @Override
    public PageResult findPageByUserId(Long userId, Integer page, Integer pagesize) {
        //1 封装查询条件 根据用户id查询
        Query query = new Query(Criteria.where("userId").is(userId));

        //2 查询数据总条数
        long count = mongoTemplate.count(query, Movement.class);

        //3 设置分页条件和排序
        query.skip((page - 1) * pagesize)
                .limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created")));

        //4 查询当前页的记录，根据分页查询
        List<Movement> movementList = mongoTemplate.find(query, Movement.class);

        //5 返回结果
        return new PageResult(page, pagesize, (int) count, movementList);
    }
}
