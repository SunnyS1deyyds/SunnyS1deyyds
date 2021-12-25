package com.tanhua.dubbo.api;

import com.itheima.model.mongo.Comment;

import java.util.List;

public interface CommentApi {

    //保存评论数据  也可以保存点赞、喜欢数据
    //返回 评论、点赞、喜欢  的总数
    Integer save(Comment newComment);

    //分页查询评论数据
    List<Comment> findByMovementId(String movementId, int commentType, Integer page, Integer pagesize);

    //判断用户是否已经点过赞
    boolean hasLikeComment(Long userId, String movementId, int commentType);

    //删除点赞数据  也可以删除评论、点赞、喜欢数据
    //返回 评论、点赞、喜欢  的总数
    int delete(Comment newComment);
}
