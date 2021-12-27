package com.tanhua.test;

import com.itheima.model.pojo.User;
import com.tanhua.app.AppServerApplication;
import com.tanhua.commons.utils.Constants;
import com.tanhua.config.template.HuanXinTemplate;
import com.tanhua.dubbo.api.UserApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class HuanXinTemplateTest {

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @DubboReference
    private UserApi userApi;

    @Test
    public void test() {
        //Boolean flag = huanXinTemplate.createUser("user04", "123456");
        //System.out.println(flag);

        //1. 查询所有的用户
        List<User> list = userApi.findAll();

        //2. 遍历所有的用户，设置环信账号密码
        for (User user : list) {
            //注册环信账号
            Boolean aBoolean = huanXinTemplate.createUser(
                    Constants.HX_USER_PREFIX + user.getId(), Constants.INIT_PASSWORD
            );

            //判断是否注册成功
            if (aBoolean) {
                //更新用户信息
                user.setHxUser(Constants.HX_USER_PREFIX + user.getId());
                user.setHxPassword(Constants.INIT_PASSWORD);

                userApi.update(user);
            }

        }
    }
}
