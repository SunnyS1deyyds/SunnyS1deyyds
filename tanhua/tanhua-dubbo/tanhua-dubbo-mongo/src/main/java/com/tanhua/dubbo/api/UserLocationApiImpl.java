package com.tanhua.dubbo.api;

import com.itheima.model.mongo.UserLocation;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;

import java.util.List;
import java.util.stream.Collectors;

@DubboService
public class UserLocationApiImpl implements UserLocationApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Boolean updateLocation(Long userId, Double longitude, Double latitude, String address) {
        try {
            //1 封装查询条件
            Query query = Query.query(Criteria.where("userId").is(userId));

            //2 查询用户的地址位置
            UserLocation userLocation = mongoTemplate.findOne(query, UserLocation.class);

            //3 判断查询结果是否为空
            if (userLocation == null) {
                //3.1 如果为空，就新增地理位置
                userLocation = new UserLocation();
                userLocation.setUserId(userId);
                userLocation.setAddress(address);
                userLocation.setLocation(new GeoJsonPoint(longitude, latitude));
                userLocation.setCreated(System.currentTimeMillis());
                userLocation.setUpdated(System.currentTimeMillis());
                userLocation.setLastUpdated(System.currentTimeMillis());
                mongoTemplate.save(userLocation);

            } else {
                //3.2 如果不为空就更新地理位置
                Update update = Update.update("location", new GeoJsonPoint(longitude, latitude))
                        .set("address", address)
                        .set("updated", System.currentTimeMillis())
                        .set("lastUpdated", System.currentTimeMillis());
                mongoTemplate.updateFirst(query, update, UserLocation.class);
            }

            //成功返回true
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Long> queryNearUser(Long userId, String distance) {
        UserLocation userLocation = mongoTemplate.findOne(new Query(
                Criteria.where("userId").is(userId)), UserLocation.class);

        //构造坐标点   查询当前用户的坐标点
        GeoJsonPoint point = userLocation.getLocation();
        //构造半径
        Double distanceNum = Double.valueOf(distance);//数字的转换
        Distance distanceObj = new Distance(distanceNum / 1000, Metrics.KILOMETERS);
        //画了一个圆圈
        Circle circle = new Circle(point, distanceObj);
        //构造query对象
        Query query = Query.query(Criteria.where("location").withinSphere(circle));
        //省略其他内容
        List<UserLocation> list = mongoTemplate.find(query, UserLocation.class);

        //返回附近的人的ids
        return list.stream().map(UserLocation::getUserId).collect(Collectors.toList());
    }
}
