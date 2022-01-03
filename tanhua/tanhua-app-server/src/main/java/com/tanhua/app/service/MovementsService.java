package com.tanhua.app.service;

import cn.hutool.core.collection.CollUtil;
import com.itheima.model.mongo.Movement;
import com.itheima.model.mongo.Visitors;
import com.itheima.model.pojo.UserInfo;
import com.itheima.model.vo.ErrorResult;
import com.itheima.model.vo.MovementsVo;
import com.itheima.model.vo.PageResult;
import com.itheima.model.vo.VisitorsVo;
import com.tanhua.app.exception.BusinessException;
import com.tanhua.app.interceptor.UserHolder;
import com.tanhua.commons.utils.Constants;
import com.tanhua.config.template.OssTemplate;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VisitorsApi;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MovementsService {

    @Autowired
    private MqMessageService mqMessageService;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private VisitorsApi visitorsApi;

    public void pushlishMovement(Movement movement, MultipartFile[] imageContent) throws IOException {

        //1. 判断动态内容是否为空，如果为空，直接抛异常，全局异常处理器处理
        if (StringUtils.isBlank(movement.getTextContent())) {
            throw new BusinessException(ErrorResult.contentError());
        }

        //2. 获取当前用户的id
        Long userId = UserHolder.getUserId();


        //3. oss图片上传
        List<String> medias = new ArrayList<>();
        for (MultipartFile file : imageContent) {
            String imageUrl = ossTemplate.upload(file.getOriginalFilename(), file.getInputStream());
            medias.add(imageUrl);
        }

        //4. 调用API实现动态发布
        movement.setMedias(medias);
        movement.setCreated(new Date().getTime());
        movement.setUserId(userId);


        String movementId = movementApi.pushlishMovement(movement);

        //发送发布动态的操作日志
        mqMessageService.sendLogMessage(UserHolder.getUserId(), "0201", "movement", movementId);

        //发送动态审核的消息
        mqMessageService.sendAudiMessage(movementId);
    }

    public PageResult all(Long userId, Integer page, Integer pagesize) {
        //1 判断userId是否为空，如果为空，使用当前登录的用户id
        if (userId == null) {
            userId = UserHolder.getUserId();
        }

        //2 根据用户id查询动态数据
        PageResult pageResult = movementApi.findPageByUserId(userId, page, pagesize);

        //判断动态数据是否为空，如果为空，直接返回
        if (pageResult.getItems() == null || pageResult.getItems().isEmpty()) {
            return pageResult;
        }

        //3 根据用户id查询用户详情
        UserInfo userInfo = userInfoApi.findById(userId);

        List<Movement> movementList = pageResult.getItems();

        //4 遍历多个动态，封装vo数据
        List<MovementsVo> vos = new ArrayList<>();
        for (Movement movement : movementList) {
            MovementsVo vo = MovementsVo.init(userInfo, movement);
            vos.add(vo);
        }

        //5 封装返回结果对象，返回数据
        //重新设置结果接Items，因为要返回的是vo对象  分页数据是一样的
        pageResult.setItems(vos);
        return pageResult;
    }

    public PageResult findFriendMovements(Integer page, Integer pagesize) {
        //1 获取当前用户id
        Long userId = UserHolder.getUserId();

        //2 根据当前用户id查询好友动态  调用API查询
        List<Movement> movementList = movementApi.findFriendMovements(userId, page, pagesize);

        return getPageResult(movementList, page, pagesize);
    }

    private PageResult getPageResult(List<Movement> movementList, int page, int pagesize) {
        //3 判断查询结果是否为空，为空直接返回
        //if (movementList == null || movementList.isEmpty()) {
        if (CollUtil.isEmpty(movementList)) {
            return new PageResult(page, pagesize, 0, null);
        }

        //4 根据好友动态（多个 用户id）查询发布人的详情信息
        List<Long> ids = movementList.stream().map(Movement::getUserId).collect(Collectors.toList());
        List<UserInfo> userInfoList = userInfoApi.findByUserIds(ids);

        //为了方便获取UserInfo，把list转为map
        Map<Long, UserInfo> map = userInfoList.stream()
                .collect(Collectors.toMap(UserInfo::getId, Function.identity()));

        //5 封装返回结果vo
        List<MovementsVo> vos = new ArrayList<>();
        for (Movement movement : movementList) {
            //有可能用户被注销了，  如果用户注销，动态不展示   就需要做判断
            UserInfo userInfo = map.get(movement.getUserId());
            if (userInfo != null) {
                MovementsVo vo = MovementsVo.init(userInfo, movement);


                //修改动态的vo   设置点赞状态
                String keyLike = Constants.MOVEMENTS_INTERACT_KEY + movement.getId().toHexString(); //redis的hash结构的大key，区分动态
                String hashKeyLike = Constants.MOVEMENT_LIKE_HASHKEY + UserHolder.getUserId();//哪个用户进行的点赞操作
                //判断点赞状态是否存在，如果有点赞，设置值
                if (redisTemplate.opsForHash().hasKey(keyLike, hashKeyLike)) {
                    vo.setHasLiked(1);
                } else {
                    vo.setHasLiked(0);
                }


                //修改动态的vo   设置喜欢状态
                String keyLove = Constants.MOVEMENTS_INTERACT_KEY + movement.getId().toHexString(); //redis的hash结构的大key，区分动态
                String hashKeyLove = Constants.MOVEMENT_LOVE_HASHKEY + UserHolder.getUserId();//哪个用户进行的点赞操作
                //判断点赞状态是否存在，如果有点赞，设置值
                if (redisTemplate.opsForHash().hasKey(keyLove, hashKeyLove)) {
                    vo.setHasLoved(1);
                } else {
                    vo.setHasLoved(0);
                }


                vos.add(vo);
            }
        }

        //6 封装pageResult对象，返回数据.不用数据总条数，因为前端不展示，不需要
        return new PageResult(page, pagesize, 0, vos);
    }

    public PageResult findRecommendMovements(Integer page, Integer pagesize) {
        //1. 查询redis中推荐动态  pid
        String key = Constants.MOVEMENTS_RECOMMEND + UserHolder.getUserId();
        String value = redisTemplate.opsForValue().get(key);

        //2. 判断是否有推荐数据
        List<Movement> movementList = new ArrayList<>();
        if (StringUtils.isBlank(value)) {
            //2.1  如果没有推荐数据，随机获取10条动态数据
            movementList = movementApi.randomMovement();
        } else {
            //2.2  如果有推荐数据，进行分页处理  16,17,18,19,20,21,30,31,42,43,54,55,66,68
            String[] pidArr = value.split(",");
            //判断是否有足够的数据
            if (pidArr.length > (page - 1) * pagesize) {
                //分页梳理
                List<Long> pids = Arrays.stream(pidArr)
                        .skip((page - 1) * pagesize)  //分页操作
                        .limit(pagesize) //设置每页显示多少数据
                        .map(pid -> Long.parseLong(pid)) //对流里面的元素进行处理  把String转为字符串
                        .collect(Collectors.toList());//把流转为List

                //根据推荐数据 多个 pid  查询动态数据
                movementList = movementApi.findByPids(pids);
            }
        }

        //返回结果
        return getPageResult(movementList, page, pagesize);
    }

    public MovementsVo findById(String movementId) {
        //1 根据动态id查询动态
        Movement movement = movementApi.findById(movementId);

        if (movement == null) {
            return null;
        }

        //2 获取发布动态人的详情信息
        UserInfo userInfo = userInfoApi.findById(movement.getUserId());

        //3 封装vo  返回结果
        return MovementsVo.init(userInfo, movement);
    }

    //谁看过我   根据查看访客信息的时间  进行查询
    public List<VisitorsVo> queryVisitorsList() {
        //1. 从redis中查询访问时间
        String key = Constants.VISITORS_USER;//hash结构的大key，保存所有的访客查询时间
        String hashKey = UserHolder.getUserId().toString();
        //查询访问时间
        String redisDate = (String) redisTemplate.opsForHash().get(key, hashKey);
        //转换时间格式
        Long date = redisDate == null ? null : Long.parseLong(redisDate);

        //2. 使用API查询访客信息
        List<Visitors> visitorsList = visitorsApi.queryVisitorsList(UserHolder.getUserId(), date, 5);

        //3. 把访客集合中的访客id提取
        List<Long> ids = visitorsList.stream()
                .map(Visitors::getVisitorUserId).collect(Collectors.toList());

        //4. 根据访客的ids   查询访客的用户详情
        Map<Long, UserInfo> map = userInfoApi.findByUserIds(ids).stream()
                .collect(Collectors.toMap(UserInfo::getId, Function.identity()));


        //5. 封装vo
        List<VisitorsVo> vos = new ArrayList<>();
        for (Visitors visitors : visitorsList) {
            UserInfo userInfo = map.get(visitors.getVisitorUserId());
            if (userInfo != null) {
                VisitorsVo vo = VisitorsVo.init(userInfo, visitors);
                vos.add(vo);
            }
        }

        //返回结果
        return vos;
    }
}
