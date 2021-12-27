package com.tanhua.app.service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.model.dto.RecommendUserDto;
import com.itheima.model.mongo.RecommendUser;
import com.itheima.model.pojo.Question;
import com.itheima.model.pojo.User;
import com.itheima.model.pojo.UserInfo;
import com.itheima.model.vo.PageResult;
import com.itheima.model.vo.TodayBest;
import com.tanhua.app.interceptor.UserHolder;
import com.tanhua.commons.utils.Constants;
import com.tanhua.config.template.HuanXinTemplate;
import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TanhuaService {

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @DubboReference
    private RecommendUserApi recommendUserApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private QuestionApi questionApi;

    @DubboReference
    private UserApi userApi;

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

    public TodayBest personalInfo(Long userId) {
        //1 根据佳人的id查询用户详情信息
        UserInfo userInfo = userInfoApi.findById(userId);

        //2 根据用户id和推荐好友(佳人)id查询推荐用户信息
        RecommendUser user = recommendUserApi.findRecommendUser(UserHolder.getUserId(), userId);

        //3 封装vo,并返回结果
        return TodayBest.init(userInfo, user);
    }

    public String strangerQuestions(Long userId) {
        //1 查询陌生人问题
        Question question = questionApi.findByUserId(userId);

        //2 判断陌生人的问题是否存在，如果有就是用，没有就设置默认问题
        String txt = question == null ? "你喜欢java吗" : question.getTxt();

        //3 返回默认人的问题
        return txt;
    }

    public void replyQuestions(Long userId, String reply) {
        //1. 根据用户id查询环信id
        String hxUser = userApi.findByUserId(userId).getHxUser();

        //查询当前操作用户的详情信息
        UserInfo userInfo = userInfoApi.findById(UserHolder.getUserId());


        //2. 封装消息内容，使用Map封装
        Map<String, Object> map = new HashMap<>();
        map.put("userId", UserHolder.getUserId());
        map.put("huanXinId", Constants.HX_USER_PREFIX + UserHolder.getUserId());
        map.put("nickname", userInfo.getNickname());
        map.put("strangerQuestion", strangerQuestions(userId));
        map.put("reply", reply);

        //3. 把Map消息对象转为Json的字符串
        String msg = JSON.toJSONString(map);

        //4. 发送消息
        huanXinTemplate.sendMsg(hxUser, msg);
    }
}
