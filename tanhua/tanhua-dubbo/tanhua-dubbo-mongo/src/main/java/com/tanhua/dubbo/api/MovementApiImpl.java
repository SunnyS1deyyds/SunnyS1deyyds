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
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<Movement> findFriendMovements(Long friendId, Integer page, Integer pagesize) {
        //1 查询时间线表，找到自己的好友时间线数据
        List<MovementTimeLine> movementTimeLineList = mongoTemplate.find(
                new Query(
                        Criteria.where("friendId").is(friendId)
                ).skip((page - 1) * pagesize)
                        .limit(pagesize)
                        .with(Sort.by(Sort.Order.desc("created")))  //按发布时间倒序排序
                , MovementTimeLine.class);

        //2 获取时间线中的 多个动态id
        List<ObjectId> ids = movementTimeLineList.stream()
                .map(MovementTimeLine::getMovementId).collect(Collectors.toList());

        //3 根据多个动态id，查询动态
        return mongoTemplate.find(new Query(Criteria.where("id").in(ids)), Movement.class);
    }

    @Override
    public List<Movement> randomMovement() {
        //1. 创建统计对象   设置获取10个样本
        TypedAggregation<?> aggregation = Aggregation
                .newAggregation(Movement.class, Aggregation.sample(10));

        //2 使用MongoTemplate进行统计，获取结果
        AggregationResults<Movement> result = mongoTemplate.aggregate(aggregation, Movement.class);

        //3 从统计结果中，获取需要的动态数据
        return result.getMappedResults();
    }

    @Override
    public List<Movement> findByPids(List<Long> pids) {
        return mongoTemplate.find(new Query(
                Criteria.where("pid").in(pids)
        ), Movement.class);
    }

    @Override
    public Movement findById(String movementId) {
        //获取到的id是String   查询的时候，需要把String 转为 ObjectID
        //new ObjectId(movementId)
        return mongoTemplate.findById(new ObjectId(movementId), Movement.class);
    }
}
