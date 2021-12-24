package com.tanhua.app.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.model.dto.RecommendUserDto;
import com.itheima.model.mongo.RecommendUser;
import com.itheima.model.pojo.UserInfo;
import com.itheima.model.vo.PageResult;
import com.itheima.model.vo.TodayBest;
import com.tanhua.app.interceptor.UserHolder;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TanhuaService {

    @DubboReference
    private RecommendUserApi recommendUserApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    public TodayBest todayBest() {

        //获取当前用户的id
        Long userId = UserHolder.getUserId();

        //查询用户 的缘分值最高的推荐用户
        RecommendUser recommendUser = recommendUserApi.findWithMaxScore(userId);

        //根据推荐用户的id查询用户详情信息
        UserInfo userInfo = userInfoApi.findById(recommendUser.getUserId());

        //构建vo对象
        TodayBest vo = TodayBest.init(userInfo, recommendUser);

        return vo;
    }

    public PageResult<TodayBest> findRecommendUser(RecommendUserDto dto) {
        //1 获取当前用户的id
        Long userId = UserHolder.getUserId();

        //2 根据用户id查询推荐好友 根据toUserId查询
        List<RecommendUser> recommendUserList = recommendUserApi.findRecommendUser(userId);

        //判断好友数据是否为空
        if (recommendUserList == null || recommendUserList.isEmpty()) {
            //直接返回空对象
            return new PageResult<>(dto.getPage(), dto.getPagesize(), 0, null);
        }

        //3 根据推荐好友id查询好友的详情信息
        //获取查询条件--一堆用户id(推荐的好友id)
        List<Long> ids = recommendUserList.stream()
                .map(RecommendUser::getUserId).collect(Collectors.toList());
        //List<Long> ids = CollUtil.getFieldValues(recommendUserList, "userId", Long.class);

        Page<UserInfo> pages = userInfoApi.findPage(ids, dto);

        //4 封装返回结果
        //把推荐好友List转为Map，封装vo的时候，能够性能更高
        Map<Long, RecommendUser> map = recommendUserList.stream()
                .collect(Collectors.toMap(RecommendUser::getUserId, Function.identity()));

        //声明存放vo的集合
        List<TodayBest> vos = new ArrayList<>();
        for (UserInfo userInfo : pages.getRecords()) {
            //根据用户详情和推荐好友数据，封装成vo对象
            TodayBest vo = TodayBest.init(userInfo, map.get(userInfo.getId()));

            //把vo放到容器中
            vos.add(vo);
        }


        //5 返回数据
        return new PageResult<TodayBest>(dto.getPage(), dto.getPagesize(), (int) pages.getTotal(), vos);
    }
}
