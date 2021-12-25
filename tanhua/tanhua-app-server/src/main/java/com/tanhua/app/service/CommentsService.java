package com.tanhua.app.service;

import cn.hutool.core.collection.CollUtil;
import com.itheima.model.enums.CommentType;
import com.itheima.model.mongo.Comment;
import com.itheima.model.mongo.Movement;
import com.itheima.model.pojo.UserInfo;
import com.itheima.model.vo.CommentVo;
import com.itheima.model.vo.ErrorResult;
import com.itheima.model.vo.PageResult;
import com.tanhua.app.exception.BusinessException;
import com.tanhua.app.interceptor.UserHolder;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommentsService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private CommentApi commentApi;

    @DubboReference
    private UserInfoApi userInfoApi;


    public void saveComments(String movementId, String comment) {

        //1 根据动态id，查询动态
        Movement movement = movementApi.findById(movementId);

        //2 判断动态是否存在，不存在就直接返回
        if (movement == null) {
            return;
        }

        //3 创建评论的对象 设置值
        Comment newComment = new Comment();
        newComment.setPublishId(movement.getId());
        newComment.setCommentType(CommentType.COMMENT.getType());
        newComment.setContent(comment);
        newComment.setUserId(UserHolder.getUserId());
        newComment.setPublishUserId(movement.getUserId());
        newComment.setCreated(new Date().getTime());

        //4 保存评论
        commentApi.save(newComment);
    }

    public PageResult findComments(String movementId, Integer page, Integer pagesize) {
        //1 分页查询评论数据
        List<Comment> commentList = commentApi.findByMovementId(movementId, CommentType.COMMENT.getType(), page, pagesize);

        //判断是否有评论
        if (CollUtil.isEmpty(commentList)) {
            return new PageResult(page, pagesize, 0, null);
        }

        //2 查询用户详情
        List<Long> ids = commentList.stream().map(Comment::getUserId).collect(Collectors.toList());
        Map<Long, UserInfo> map = userInfoApi.findByUserIds(ids).stream()
                .collect(Collectors.toMap(UserInfo::getId, Function.identity()));

        //3 封装vo集合
        List<CommentVo> vos = new ArrayList<>();
        for (Comment comment : commentList) {
            UserInfo userInfo = map.get(comment.getUserId());
            if (userInfo != null) {
                CommentVo vo = CommentVo.init(userInfo, comment);
                vos.add(vo);
            }
        }

        //4 创建PageResult返回
        return new PageResult(page, pagesize, 0, vos);
    }

    //点赞 保存点赞数据
    public Integer likeComment(String movementId) {
        //1 根据动态id，查询动态
        Movement movement = movementApi.findById(movementId);

        //2 判断动态是否存在，不存在抛异常
        if (movement == null) {
            throw new BusinessException(ErrorResult.contentError());
        }

        //3 查询当前用户是否已经点赞
        boolean hasLike = commentApi.hasLikeComment(UserHolder.getUserId(), movementId, CommentType.LIKE.getType());
        //如果存在，不进行点赞  抛异常
        if (hasLike) {
            throw new BusinessException(ErrorResult.likeError());
        }


        //4 创建点赞的对象 设置值  保存点赞
        Comment newComment = new Comment();
        newComment.setPublishId(movement.getId());
        newComment.setCommentType(CommentType.LIKE.getType());
        newComment.setUserId(UserHolder.getUserId());
        newComment.setPublishUserId(movement.getUserId());
        newComment.setCreated(new Date().getTime());
        int count = commentApi.save(newComment);


        //5 点赞状态保存到redis中
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId; //redis的hash结构的大key，区分动态
        String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + UserHolder.getUserId();//哪个用户进行的点赞操作
        redisTemplate.opsForHash().put(key, hashKey, "1");

        return count;
    }

    //取消点赞 删除点赞数据
    public Integer dislikeComment(String movementId) {
        //1 根据动态id，查询动态
        Movement movement = movementApi.findById(movementId);

        //2 判断动态是否存在，不存在抛异常
        if (movement == null) {
            throw new BusinessException(ErrorResult.contentError());
        }

        //3 查询当前用户是否已经点赞
        boolean hasLike = commentApi.hasLikeComment(UserHolder.getUserId(), movementId, CommentType.LIKE.getType());
        //如果不存在，不取消点赞   抛异常
        if (!hasLike) {
            throw new BusinessException(ErrorResult.disLikeError());
        }


        //4 创建点赞的对象 设置值  删除点赞
        Comment newComment = new Comment();
        newComment.setPublishId(movement.getId());
        newComment.setCommentType(CommentType.LIKE.getType());
        newComment.setUserId(UserHolder.getUserId());
        newComment.setPublishUserId(movement.getUserId());
        newComment.setCreated(new Date().getTime());
        int count = commentApi.delete(newComment);


        //5 点赞状态从redis中删除
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId; //redis的hash结构的大key，区分动态
        String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + UserHolder.getUserId();//哪个用户进行的点赞操作
        redisTemplate.opsForHash().delete(key, hashKey);

        return count;
    }

    //喜欢  保存喜欢数据
    public Integer loveComment(String movementId) {
        //1 根据动态id，查询动态
        Movement movement = movementApi.findById(movementId);

        //2 判断动态是否存在，不存在抛异常
        if (movement == null) {
            throw new BusinessException(ErrorResult.contentError());
        }

        //3 查询当前用户是否已经喜欢
        boolean hasLike = commentApi.hasLikeComment(UserHolder.getUserId(), movementId, CommentType.LOVE.getType());
        //如果存在，不进行喜欢  抛异常
        if (hasLike) {
            throw new BusinessException(ErrorResult.loveError());
        }


        //4 创建喜欢的对象 设置值  保存喜欢
        Comment newComment = new Comment();
        newComment.setPublishId(movement.getId());
        newComment.setCommentType(CommentType.LOVE.getType());
        newComment.setUserId(UserHolder.getUserId());
        newComment.setPublishUserId(movement.getUserId());
        newComment.setCreated(new Date().getTime());
        int count = commentApi.save(newComment);


        //5 喜欢状态保存到redis中
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId; //redis的hash结构的大key，区分动态
        String hashKey = Constants.MOVEMENT_LOVE_HASHKEY + UserHolder.getUserId();//哪个用户进行的喜欢操作
        redisTemplate.opsForHash().put(key, hashKey, "1");

        return count;
    }

    //取消喜欢  删除喜欢数据
    public Integer unlove(String movementId) {
        //1 根据动态id，查询动态
        Movement movement = movementApi.findById(movementId);

        //2 判断动态是否存在，不存在抛异常
        if (movement == null) {
            throw new BusinessException(ErrorResult.contentError());
        }

        //3 查询当前用户是否已经喜欢
        boolean hasLike = commentApi.hasLikeComment(UserHolder.getUserId(), movementId, CommentType.LOVE.getType());
        //如果不存在，不取消喜欢   抛异常
        if (!hasLike) {
            throw new BusinessException(ErrorResult.disloveError());
        }


        //4 创建喜欢的对象 设置值  删除喜欢
        Comment newComment = new Comment();
        newComment.setPublishId(movement.getId());
        newComment.setCommentType(CommentType.LOVE.getType());
        newComment.setUserId(UserHolder.getUserId());
        newComment.setPublishUserId(movement.getUserId());
        newComment.setCreated(new Date().getTime());
        int count = commentApi.delete(newComment);


        //5 喜欢状态从redis中删除
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId; //redis的hash结构的大key，区分动态
        String hashKey = Constants.MOVEMENT_LOVE_HASHKEY + UserHolder.getUserId();//哪个用户进行的点赞操作
        redisTemplate.opsForHash().delete(key, hashKey);

        return count;
    }
}
