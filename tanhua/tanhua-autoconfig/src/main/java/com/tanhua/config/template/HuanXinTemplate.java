package com.tanhua.config.template;

import cn.hutool.core.collection.CollUtil;
import com.easemob.im.server.EMProperties;
import com.easemob.im.server.EMService;
import com.easemob.im.server.model.EMTextMessage;
import com.tanhua.config.properties.HuanXinProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class HuanXinTemplate {

    private EMService emService;

    public HuanXinTemplate(HuanXinProperties properties) {
        EMProperties emProperties = EMProperties.builder()
                .setAppkey(properties.getAppkey())
                .setClientId(properties.getClientId())
                .setClientSecret(properties.getClientSecret())
                .build();
        emService = new EMService(emProperties);
    }

    //创建环信用户
    public Boolean createUser(String username, String password) {
        try {
            //创建环信用户
            emService.user().create(username.toLowerCase(), password)
                    .block();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("创建环信用户失败~");
        }
        return false;
    }

    //添加联系人
    public Boolean addContact(String user, String contact) {
        try {
            //创建环信用户
            emService.contact().add(user, contact)
                    .block();
            return true;
        } catch (Exception e) {
            log.error("添加联系人失败~");
        }
        return false;
    }

    //删除联系人
    public Boolean deleteContact(String username1, String username2) {
        try {
            //创建环信用户
            emService.contact().remove(username1, username2)
                    .block();
            return true;
        } catch (Exception e) {
            log.error("删除联系人失败~");
        }
        return false;
    }

    //发送消息
    public Boolean sendMsg(String username, String content) {
        try {
            //接收人用户列表
            Set<String> set = CollUtil.newHashSet(username);
            //文本消息
            EMTextMessage message = new EMTextMessage().text(content);
            //发送消息  from：admin是管理员发送
            emService.message().send(
                    "admin",
                    "users",
                    set, message,
                    null
            ).block();
            return true;
        } catch (Exception e) {
            log.error("删除联系人失败~");
        }
        return false;
    }
}