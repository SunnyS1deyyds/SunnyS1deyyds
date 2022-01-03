package com.tanhua.recommend.listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itheima.model.mongo.Movement;
import com.itheima.model.mongo.MovementScore;
import com.itheima.model.mongo.Video;
import com.itheima.model.mongo.VideoScore;
import org.bson.types.ObjectId;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class RecommendListener {

    @Autowired
    private MongoTemplate mongoTemplate;

    //监听消息，采集动态的日志数据
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "tanhua.movement.recommend",
                    durable = "true"
            ),
            exchange = @Exchange(
                    name = "tanhua.log.exchange",
                    type = ExchangeTypes.TOPIC
            ),
            key = "log.movement"
    ))
    public void listenerMovement(String message) {
        System.out.println("采集动态的日志数据");

        //1 解析消息数据
        JSONObject jsonObject = JSON.parseObject(message);
        Long userId = jsonObject.getLong("userId");
        String type = jsonObject.getString("type");
        String logTime = jsonObject.getString("logTime");
        String busId = jsonObject.getString("busId");

        //2 保存日志数据
        Movement movement = mongoTemplate.findById(new ObjectId(busId), Movement.class);

        //判断动态是否存在
        if (movement != null) {
            //如果动态不为空，就保存推荐使用的基础数据
            MovementScore movementScore = new MovementScore();
            movementScore.setUserId(userId);
            movementScore.setMovementId(movement.getPid());//填写推荐系统需要的唯一标识符
            movementScore.setDate(System.currentTimeMillis());
            movementScore.setScore(getMovementScore(type, movement));

            mongoTemplate.save(movementScore);
        }
    }

    //计算动态的评分
    private Double getMovementScore(String type, Movement movement) {
        //0201 发布动态 基础 5分 文字长度：50以内1分，50~100之间2分，100以上3分 图片个数：每个图片一分
        //0202  浏览 +1
        //0203  点赞 +5
        //0204  喜欢 +8
        //0205  评论 + 10
        //0206  取消点赞  -5
        //0207  取消喜欢 -8
        Double score = 0d;
        switch (type) {
            case ("0201"):
                score = 5d;
                int length = StrUtil.length(movement.getTextContent());//获取文本的长度
                if (length < 50) {
                    score += 1d;
                } else if (length < 100) {
                    score += 2d;
                } else {
                    score += 3d;
                }
                score += movement.getMedias().size();
                break;
            case ("0202"):
                score = 1d;
                break;
            case ("0203"):
                score = 5d;
                break;
            case ("0204"):
                score = 8d;
                break;
            case ("0205"):
                score = 10d;
                break;
            case ("0206"):
                score = -5d;
                break;
            case ("0207"):
                score = -8d;
                break;
        }

        return score;
    }


    //监听消息，采集小视频的日志数据
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "tanhua.video.recommend",
                    durable = "true"
            ),
            exchange = @Exchange(
                    name = "tanhua.log.exchange",
                    type = ExchangeTypes.TOPIC
            ),
            key = "log.video"
    ))
    public void listenerVideo(String message) {
        System.out.println("采集小视频日志数据");

        //1 解析消息
        JSONObject jsonObject = JSON.parseObject(message);
        Long userId = jsonObject.getLong("userId");
        String type = jsonObject.getString("type");
        String logTime = jsonObject.getString("logTime");
        String busId = jsonObject.getString("busId");

        //2 存储小视频日志
        Video video = mongoTemplate.findById(new ObjectId(busId), Video.class);
        if (video != null) {
            VideoScore videoScore = new VideoScore();
            videoScore.setUserId(userId);
            videoScore.setVideoId(video.getVid());//填写推荐系统需要的唯一标识符
            videoScore.setDate(System.currentTimeMillis());
            videoScore.setScore(getVidemoScore(type));

            mongoTemplate.save(videoScore);
        }
    }

    //分析小视频操作的得分
    private Double getVidemoScore(String type) {
        //0301 发小视频     +2
        //0302 小视频点赞    +5
        //0303 小视频取消点赞 -5
        //0304 小视频评论    +10
        Double score = 0d;
        switch (type) {
            case ("0301"):
                score = 2d;
                break;
            case ("0302"):
                score = 5d;
                break;
            case ("0303"):
                score = -5d;
                break;
            case ("0304"):
                score = 10d;
                break;
        }
        return score;
    }
}
