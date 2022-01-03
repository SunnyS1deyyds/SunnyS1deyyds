package com.tanhua.aop.service;

import com.tanhua.aop.util.LogRec;
import com.tanhua.aop.util.Type;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @LogRec(type = Type.LOGIN, key = "user", note = "执行登录",abc = "测试属性")
    public String login() {
        System.out.println("执行service方法");

        //返回主键ID
        return "666";
    }
}
