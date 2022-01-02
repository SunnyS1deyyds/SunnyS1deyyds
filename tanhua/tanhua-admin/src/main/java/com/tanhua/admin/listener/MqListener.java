package com.tanhua.admin.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itheima.model.mongo.Movement;
import com.itheima.model.pojo.Log;
import com.tanhua.admin.mapper.LogMapper;
import com.tanhua.config.template.GreenTemplate;
import com.tanhua.dubbo.api.MovementApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class MqListener {

    //@Autowired
    @Resource
    private LogMapper logMapper;

    @Autowired
    private GreenTemplate greenTemplate;

    @DubboReference
    private MovementApi movementApi;

    //监听日志消息
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "tanhua.log.admin",
                    durable = "true"
            ),
            exchange = @Exchange(
                    name = "tanhua.log.exchange",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"log.*"}
    ))
    public void logListener(String message) {
        //1 解析消息
        JSONObject jsonObject = JSON.parseObject(message);

        //2 保存操作日志
        Log log = new Log();
        log.setUserId(jsonObject.getLong("userId"));
        log.setLogTime(jsonObject.getString("logTime"));
        log.setType(jsonObject.getString("type"));

        logMapper.insert(log);
    }

    //监听审核动态消息
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "tanhua.green.movement",
                    durable = "true"
            ),
            exchange = @Exchange(
                    name = "tanhua.green.exchange",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"green.movement"}
    ))
    public void greenListener(String movementId) {
        //1 根据id查询动态
        Movement movement = movementApi.findById(movementId);

        //如果动态审核过了，就不再审核了，判断动态是否不为空，动态的状态需要为0
        if (movement != null && movement.getState() == 0) {
            try {
                //2 获取动态的文本和图片，使用阿里内容审核进行审核操作
                Map<String, String> textScan = greenTemplate.greenTextScan(movement.getTextContent());
                Map<String, String> imageScan = greenTemplate.imageScan(movement.getMedias());

                //3 判断审核结果，进行审核状态的修改
                if ("pass".equals(textScan.get("suggestion")) && "pass".equals(imageScan.get("suggestion"))) {
                    movement.setState(1);
                } else if ("block".equals(textScan.get("suggestion")) || "block".equals(imageScan.get("suggestion"))) {
                    movement.setState(2);
                } else {
                    movement.setState(3);
                }

                movementApi.updateState(movement);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
