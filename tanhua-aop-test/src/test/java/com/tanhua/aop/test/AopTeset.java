package com.tanhua.aop.test;

import com.tanhua.aop.AopDemoApplication;
import com.tanhua.aop.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AopDemoApplication.class)
public class AopTeset {

    @Autowired
    private UserService userService;

    @Test
    public void test() {
        userService.login();
    }
}
