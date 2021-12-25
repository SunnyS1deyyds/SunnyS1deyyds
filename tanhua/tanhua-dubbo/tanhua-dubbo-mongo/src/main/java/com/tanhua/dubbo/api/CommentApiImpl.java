package com.tanhua.dubbo.api;

import com.itheima.model.enums.CommentType;
import com.itheima.model.mongo.Comment;
import com.itheima.model.mongo.Movement;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;

import java.util.List;

@DubboService
public class CommentApiImpl implements CommentApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Integer save(Comment newComment) {
        //1 保存Comment数据到数据库中
        mongoTemplate.save(newComment);

        //2 评论数+1  在动态Movement中进行修改  评论、点赞、喜欢都要考虑
        Query query = new Query(Criteria.where("id").is(newComment.getPublishId()));

        Update update = new Update();

        if (newComment.getCommentType() == CommentType.LIKE.getType()) {
            //是否是点赞 点赞数量+1
            update.inc("likeCount", 1);
        } else if (newComment.getCommentType() == CommentType.COMMENT.getType()) {
            //是否是评论  评论数量+1
            update.inc("commentCount", 1);
        } else {
            //是否是喜欢  喜欢数量+1
            update.inc("loveCount", 1);
        }

        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);//设置是否返回修改后的数据  true：返回修改后的数据

        //findAndModify :更新并查询
        Movement movement = mongoTemplate.findAndModify(query, update, options, Movement.class);

        //3 返回操作后的评论总数   评论、点赞、喜欢都要考虑
        return movement.statisCount(newComment.getCommentType());

    }

    @Override
    public List<Comment> findByMovementId(String movementId, int commentType, Integer page, Integer pagesize) {
        return mongoTemplate.find(
                new Query(
                        Criteria.where("publishId").is(new ObjectId(movementId))
                                .and("commentType").is(commentType)
                ).skip((page - 1) * pagesize)
                        .limit(pagesize)
                        .with(Sort.by(Sort.Order.desc("created")))
                , Comment.class);
    }

    @Override
    public boolean hasLikeComment(Long userId, String movementId, int commentType) {
        return mongoTemplate.exists(new Query(
                Criteria.where("userId").is(userId)
                        .and("publishId").is(new ObjectId(movementId))
                        .and("commentType").is(commentType)
        ),Comment.class);
    }

    @Override
    public int delete(Comment newComment) {
        //1 保存Comment数据到数据库中
        mongoTemplate.remove(new Query(
                Criteria.where("publishId").is(newComment.getPublishId())
                        .and("userId").is(newComment.getUserId())
                        .and("commentType").is(newComment.getCommentType())
        ), Comment.class);

        //2 互动数量-1  在动态Movement中进行修改  评论、点赞、喜欢都要考虑
        Query query = new Query(Criteria.where("id").is(newComment.getPublishId()));

        Update update = new Update();

        if (newComment.getCommentType() == CommentType.LIKE.getType()) {
            //是否是点赞 点赞数量-1
            update.inc("likeCount", -1);
        } else if (newComment.getCommentType() == CommentType.COMMENT.getType()) {
            //是否是评论  评论数量-1
            update.inc("commentCount", -1);
        } else {
            //是否是喜欢  喜欢数量-1
            update.inc("loveCount", -1);
        }

        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);//设置是否返回修改后的数据  true：返回修改后的数据

        //findAndModify :更新并查询
        Movement movement = mongoTemplate.findAndModify(query, update, options, Movement.class);

        //3 返回操作后的评论总数   评论、点赞、喜欢都要考虑
        return movement.statisCount(newComment.getCommentType());

    }
}
