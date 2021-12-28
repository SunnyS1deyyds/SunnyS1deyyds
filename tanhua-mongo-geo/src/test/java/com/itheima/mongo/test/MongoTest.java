package com.itheima.mongo.test;

import com.itheima.mongo.domain.Places;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testNear() {
        //构造坐标点
        GeoJsonPoint point = new GeoJsonPoint(121.49, 31.23);
        //构造半径
        Distance distanceObj = new Distance(10, Metrics.KILOMETERS);
        //画了一个圆圈
        Circle circle = new Circle(point, distanceObj);
        //构造query对象
        Query query = Query.query(Criteria.where("location").withinSphere(circle));
        //省略其他内容
        List<Places> list = mongoTemplate.find(query, Places.class);
        list.forEach(System.out::println);
    }


    //查询附近且获取间距
    @Test
    public void testNear1() {
        //1、构造中心点(圆点)
        GeoJsonPoint point = new GeoJsonPoint(121.49, 31.23);
        //2、构建NearQuery对象
        NearQuery query = NearQuery.near(point, Metrics.KILOMETERS).maxDistance(5000, Metrics.KILOMETERS);
        //3、调用mongoTemplate的geoNear方法查询
        GeoResults<Places> results = mongoTemplate.geoNear(query, Places.class);
        //4、解析GeoResult对象，获取距离和数据
        for (GeoResult<Places> result : results) {
            Places places = result.getContent();
            double value = result.getDistance().getValue();
            System.out.println(places+"---距离："+value + "km");
        }
    }


}
