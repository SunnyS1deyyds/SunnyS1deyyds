package com.tanhua.app.controller;

import cn.hutool.http.HttpStatus;
import com.itheima.model.pojo.UserInfo;
import com.tanhua.app.interceptor.UserHolder;
import com.tanhua.app.service.UserInfoService;
import com.tanhua.app.service.UserService;
import com.tanhua.commons.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.POST;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoService userInfoService;

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

    //03 首次登录---完善资料
    //POST/user/loginReginfo

    @PostMapping("/loginReginfo")
    public ResponseEntity loginReginfo(@RequestBody UserInfo userInfo) {
        //                               @RequestHeader("Authorization") String token) {
        ////1 校验token，验证用户登录是否合法
        //boolean verifyToken = JwtUtils.verifyToken(token);
        //
        ////2 如果token校验失败，直接返回401
        //if (!verifyToken) {
        //    return ResponseEntity.status(HttpStatus.HTTP_UNAUTHORIZED).body(null);
        //}
        //
        ////3 如果token校验成功，从token中获取id，设置到UserInfo中
        //Long id = Long.parseLong(JwtUtils.getClaims(token).get("id").toString());

        Long id = UserHolder.getUserId();
        userInfo.setId(id);

        //4 使用Service进行处理
        userInfoService.save(userInfo);

        //返回结果
        return ResponseEntity.ok(null);
    }

    //04 首次登录---补充头像
    //POST/user/loginReginfo/head
    @PostMapping("/loginReginfo/head")
    public ResponseEntity head(MultipartFile headPhoto) throws IOException {
        //                       @RequestHeader("Authorization") String token) throws IOException {
        //
        ////1 校验token，验证用户登录是否合法
        //boolean verifyToken = JwtUtils.verifyToken(token);
        //
        ////2 如果token校验失败，直接返回401
        //if (!verifyToken) {
        //    return ResponseEntity.status(HttpStatus.HTTP_UNAUTHORIZED).body(null);
        //}
        //
        ////3 如果token校验成功，从token中获取id，设置到UserInfo中
        //Long id = Long.parseLong(JwtUtils.getClaims(token).get("id").toString());

        Long id = UserHolder.getUserId();
        //4 使用UserInfoService 实现图片上传的功能
        userInfoService.upload(headPhoto, id);

        //5 返回结果
        return ResponseEntity.ok(null);
    }


}
