package com.tanhua.admin.service;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.model.mongo.Movement;
import com.itheima.model.mongo.Video;
import com.itheima.model.pojo.UserInfo;
import com.itheima.model.vo.*;
import com.tanhua.admin.exception.BusinessException;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ManagerService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private VideoApi videoApi;

    @DubboReference
    private MovementApi movementApi;

    public PageResult findAllUsers(Integer page, Integer pagesize) {
        //1 分页查询所有的用户
        Page<UserInfo> pages = userInfoApi.findPage(page, pagesize);

        //2 获取用户详情，设置用户状态
        List<UserInfo> list = pages.getRecords();
        for (UserInfo userInfo : list) {
            if (redisTemplate.hasKey(Constants.FREEZE_USER + userInfo.getId())) {
                userInfo.setUserStatus("2");
            }
        }

        //3 返回结果
        return new PageResult(page, pagesize, (int) pages.getTotal(), list);
    }

    //根据id查询用户详情
    public UserInfo findUserById(Long userId) {
        UserInfo userInfo = userInfoApi.findById(userId);
        if (redisTemplate.hasKey(Constants.FREEZE_USER + userId)) {
            userInfo.setUserStatus("2");
        }

        return userInfo;
    }

    //根据用户id，分页查询发布的视频
    public PageResult findAllVideos(Integer page, Integer pagesize, Long uid) {
        //需要查询的是MongoDB，分页查询的结果是Video，但是分页数据中的总记录数

        //1. 分页查询就返回PageResult  但是里面的List集合存放的是Video
        PageResult pageResult = videoApi.findAllVideos(uid, page, pagesize);
        List<Video> items = pageResult.getItems();

        //2. 根据uid查询用户详情信息
        UserInfo userInfo = userInfoApi.findById(uid);

        //3 最终返回的是 VideoVo  封装vo数据
        List<VideoVo> vos = new ArrayList<>();
        for (Video video : items) {
            VideoVo vo = VideoVo.init(userInfo, video);
            vos.add(vo);
        }

        //4 把PageResult里面的  存放着Video的List  替换为存放VideoVo的list
        pageResult.setItems(vos);

        //返回结果
        return pageResult;
    }

    //根据用户id，分页查询指定状态的动态数据
    public PageResult findAllMovements(Integer page, Integer pagesize, Long uid, Integer state) {
        //1 根据用户id和状态分页查询动态数据
        PageResult pageResult = movementApi.findAllMovements(uid, state, page, pagesize);
        List<Movement> movementList = pageResult.getItems();

        //2 根据用户id查询用户详情信息
        UserInfo userInfo = userInfoApi.findById(uid);

        //3 封装vo数据
        List<MovementsVo> vos = new ArrayList<>();
        for (Movement movement : movementList) {
            MovementsVo vo = MovementsVo.init(userInfo, movement);
            vos.add(vo);
        }

        //4 把PageResult里面存放Movement的list  替换为  存放MovementVo的list
        pageResult.setItems(vos);

        //返回结果
        return pageResult;
    }

    //用户冻结
    public Map userFreeze(Map params) {
        //1 获取请求参数   id    冻结时间
        if (params.get("userId") == null || params.get("freezingTime") == null) {
            throw new BusinessException("参数不能为空");
        }

        long id = Long.parseLong(params.get("userId").toString());
        String freezingTime = params.get("freezingTime").toString();

        //2 计算冻结时间(redis数据的存活时间)
        //freezingTime(冻结时间):       1-冻结3天， 2-冻结7天，3-永久冻结
        int day = 0;
        if ("1".equals(freezingTime)) {
            day = 3;
        }
        if ("2".equals(freezingTime)) {
            day = 7;
        }
        if ("3".equals(freezingTime)) {
            day = -1;
        }


        //3 保存数据到redis中
        String value = JSON.toJSONString(params);
        //redisTemplate.opsForValue().set(Constants.FREEZE_USER +id,value,day, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(Constants.FREEZE_USER + id, value, Duration.ofDays(day));

        //封装返回数据
        return MapUtil.builder("message", "用户冻结成功").build();
    }

    //用户解冻
    public Map userUnfreeze(Map params) {
        //1 获取请求参数   id    冻结时间
        if (params.get("userId") == null) {
            throw new BusinessException("参数不能为空");
        }

        long id = Long.parseLong(params.get("userId").toString());

        //2 解冻操作
        redisTemplate.delete(Constants.FREEZE_USER + id);

        //封装返回数据
        return MapUtil.builder("message", "用户解冻成功").build();
    }
}
