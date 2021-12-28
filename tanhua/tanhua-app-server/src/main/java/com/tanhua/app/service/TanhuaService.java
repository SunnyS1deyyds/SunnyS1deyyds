package com.tanhua.app.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.model.dto.RecommendUserDto;
import com.itheima.model.mongo.RecommendUser;
import com.itheima.model.mongo.UserLocation;
import com.itheima.model.pojo.Question;
import com.itheima.model.pojo.User;
import com.itheima.model.pojo.UserInfo;
import com.itheima.model.vo.ErrorResult;
import com.itheima.model.vo.NearUserVo;
import com.itheima.model.vo.PageResult;
import com.itheima.model.vo.TodayBest;
import com.tanhua.app.exception.BusinessException;
import com.tanhua.app.interceptor.UserHolder;
import com.tanhua.commons.utils.Constants;
import com.tanhua.config.template.HuanXinTemplate;
import com.tanhua.dubbo.api.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MessagesService messagesService;

    @DubboReference
    private RecommendUserApi recommendUserApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private QuestionApi questionApi;

    @DubboReference
    private UserApi userApi;

    @DubboReference
    private UserLikeApi userLikeApi;

    @DubboReference
    private UserLocationApi userLocationApi;

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

    @Value("${tanhua.default.recommend.users}")
    private String recommendUserStr;

    public List<TodayBest> queryCardsList() {
        //1. 调用Api查询推荐好友信息   根据用户id查询推荐用户，排除喜欢/不喜欢 的用户   最多查询10个用户
        List<RecommendUser> recommendUserList = recommendUserApi.queryCardsList(UserHolder.getUserId(), 10);

        //2. 判断推荐好友信息是否存在
        if (CollUtil.isEmpty(recommendUserList)) {
            recommendUserList = new ArrayList<>();

            //如果不存在，构建默认的推荐好友信息
            String[] arr = recommendUserStr.split(",");
            for (String rUserId : arr) {
                RecommendUser recommendUser = new RecommendUser();
                recommendUser.setUserId(Long.parseLong(rUserId));
                recommendUser.setToUserId(UserHolder.getUserId());
                recommendUser.setScore(RandomUtil.randomDouble(60, 90));

                recommendUserList.add(recommendUser);
            }
        }

        //把推荐好友按照 缘分值排序   ，设置最多10条数据   老师不完成，自己搞定
        //真正开发，不要瞎搞业务需求不是俺们后端开发定滴

        //3. 把推荐好友的id获取出来
        List<Long> rUserIds = recommendUserList.stream()
                .map(RecommendUser::getUserId).collect(Collectors.toList());


        //4. 根据推荐好友的用户id把推荐好友的用户详情全部查出来
        List<UserInfo> userInfoList = userInfoApi.findByUserIds(rUserIds);
        //转为map，方便封装vo
        Map<Long, UserInfo> map = userInfoList.stream()
                .collect(Collectors.toMap(UserInfo::getId, Function.identity()));

        //5. 构建返回的vo对象
        List<TodayBest> vos = new ArrayList<>();
        for (RecommendUser recommendUser : recommendUserList) {
            UserInfo userInfo = map.get(recommendUser.getUserId());
            if (userInfo != null) {
                TodayBest vo = TodayBest.init(userInfo, recommendUser);
                vos.add(vo);
            }
        }

        //返回结果
        return vos;
    }

    //用户进行喜欢的操作
    public void likeUser(Long likeUserId) {
        //1 调用API保存喜欢数据
        boolean flag = userLikeApi.saveLikeUser(UserHolder.getUserId(), likeUserId, true);

        //如果保存失败，喜欢不成功，抛异常
        if (!flag) {
            throw new BusinessException(ErrorResult.error());
        }

        //2 把喜欢的数据，保存到redis中
        //把喜欢的用户从不喜欢的set集合中删掉
        redisTemplate.opsForSet().remove(Constants.USER_NOT_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());
        //把喜欢的用户放到喜欢的set集合中
        redisTemplate.opsForSet().add(Constants.USER_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());

        //3 判断两者是否相互喜欢 查看在对方的喜欢set集合中，是否存在自己的用户id
        Boolean isLike = redisTemplate.opsForSet().isMember(Constants.USER_LIKE_KEY + likeUserId, UserHolder.getUserId().toString());
        if (isLike) {
            //如果是相互喜欢，保存好有数据，互为好友
            messagesService.contacts(likeUserId);
        }

    }

    //用户进行不喜欢的操作
    public void notLikeUser(Long likeUserId) {
        //1 调用API保存不喜欢数据
        boolean flag = userLikeApi.saveLikeUser(UserHolder.getUserId(), likeUserId, false);

        //如果保存失败，不喜欢不成功，抛异常
        if (!flag) {
            throw new BusinessException(ErrorResult.error());
        }

        //2 把不喜欢的数据，保存到redis中
        //把不喜欢的用户从喜欢的set集合中删掉
        redisTemplate.opsForSet().remove(Constants.USER_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());
        //把不喜欢的用户放到不喜欢的set集合中
        redisTemplate.opsForSet().add(Constants.USER_NOT_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());

        //3 判断两者是否相互不喜欢 查看在对方的不喜欢set集合中，是否存在自己的用户id
        Boolean isLike = redisTemplate.opsForSet().isMember(Constants.USER_NOT_LIKE_KEY + likeUserId, UserHolder.getUserId().toString());
        if (isLike) {
            //如果是相互不喜欢，删除好有数据，互为陌生人

            //TODO 自己搞定
        }

    }

    //实现搜索附近的人
    public List<NearUserVo> queryNearUser(String gender, String distance) {
        //1 根据当前用户id查询附近的人的信息  返回ids即可
        List<Long> ids = userLocationApi.queryNearUser(UserHolder.getUserId(), distance);

        //判断查询结果是否为空，如果为空，返回空lis
        if (CollUtil.isEmpty(ids)) {
            return new ArrayList<>();
        }

        //2 根据用户的ids和性别进行用户详情的查询
        List<UserInfo> userInfoList = userInfoApi.findByUserIds(ids, gender);

        //3 封装返回vo对象
        List<NearUserVo> vos = new ArrayList<>();
        for (UserInfo userInfo : userInfoList) {
            if (userInfo.getId().longValue() == UserHolder.getUserId().longValue()) {
                continue;
            }
            NearUserVo vo = NearUserVo.init(userInfo);
            vos.add(vo);
        }

        //返回结果
        return vos;
    }

    public static void main(String[] args) {
        //Integer a = 128;
        //Integer b = 128;
        //
        //Long aa = 128l;
        //Long bb = 128l;
        //
        //System.out.println(a == b);
        //System.out.println(aa == bb);


        long a = 10000l;
        long b = 10000l;

        System.out.println(a*b*10000l);

        long c = 10000*10000*10000;
        System.out.println(c);

    }
}
