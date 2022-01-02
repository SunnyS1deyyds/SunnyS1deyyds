package com.tanhua.app.service;

import com.alibaba.fastjson.JSON;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class MqMessageService {

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 发送日志消息
     *
     * @param userId
     * @param type   操作类型,
     *               0101为登录，  0102为注册，
     *               0201为发动态，
     *               0202为浏览动态，
     *               0203为动态点赞，
     *               0204为动态喜欢，
     *               0205为评论，
     *               0206为动态取消点赞，
     *               0207为动态取消喜欢，
     *               0301为发小视频，
     *               0302为小视频点赞，
     *               0303为小视频取消点赞，
     *               0304为小视频评论
     * @param key    用户相关user , 动态相关movement , 小视频相关 video
     * @param busId  业务id  动态id或者视频id
     */
    public void sendLogMessage(Long userId, String type, String key, String busId) {
        try {
            Map map = new HashMap();
            map.put("userId", userId);
            map.put("type", type);
            map.put("logTime", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            map.put("busId", busId);
            String message = JSON.toJSONString(map);
            amqpTemplate.convertAndSend("tanhua.log.exchange", "log." + key, message);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }


    //发送动态审核消息
    public void sendAudiMessage(String movementId) {
        try {
            amqpTemplate.convertAndSend("tanhua.green.exchange", "green.movement", movementId);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }
}