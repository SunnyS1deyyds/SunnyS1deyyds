package com.tanhua.app.controller;

import cn.hutool.http.HttpStatus;
import com.tanhua.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    //01 登录---获取验证码
    //POST/user/login
    //参数 phone	string
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map<String, String> params) {
        // 1. 获取电话号码
        String mobile = params.get("phone");

        // 2. 调用UserService实现发送短信验证码
        userService.login(mobile);

        // 3. 返回结果数据
        return ResponseEntity.ok(null);
    }

    //02 登录---验证码校验
    //POST/user/loginVerification
    //请求过来的数据：phone	string  ||| verificationCode	string
    //返回数据   token  jwt-token字符串  |||  isNew	是否新用户
    @PostMapping("/loginVerification")
    public ResponseEntity loginVerification(@RequestBody Map<String, String> params) {

        //1. 获取请求参数   手机号和验证码
        String mobile = params.get("phone");
        String code = params.get("verificationCode");

        //2. 使用UserService进行登录校验处理,返回结果数据
        Map reMap = userService.loginVerification(mobile, code);

        //3. 返回结果
        return ResponseEntity.ok(reMap);
    }
}
