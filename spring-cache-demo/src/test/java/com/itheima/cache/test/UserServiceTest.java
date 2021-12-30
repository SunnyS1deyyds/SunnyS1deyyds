package com.itheima.cache.test;

import com.itheima.cache.pojo.User;
import com.itheima.cache.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;


    //测试缓存
    @Test
    public void test() {
        for (int i = 0; i < 5; i++) {
            User user = userService.findById(1);
            System.out.println(user);
        }
    }

    //测试缓存
    @Test
    public void test2() {
        userService.update(1);
    }


}
