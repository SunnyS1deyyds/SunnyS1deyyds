package com.tanhua.dubbo.api;

import com.itheima.model.mongo.Visitors;

import java.util.List;

public interface VisitorsApi {

    //保存访客信息  一个用户的访问信息，一天只保存一条
    void save(Visitors visitors);

    //根据用户in系，访问时间和查询条数  查询访客信息
    List<Visitors> queryVisitorsList(Long userId, Long date, int count);
}
