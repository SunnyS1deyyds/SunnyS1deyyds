package com.tanhua.app.controller;

import cn.hutool.http.HttpStatus;
import com.itheima.model.pojo.UserInfo;
import com.itheima.model.vo.PageResult;
import com.itheima.model.vo.SettingsVo;
import com.itheima.model.vo.UserInfoVo;
import com.tanhua.app.interceptor.UserHolder;
import com.tanhua.app.service.SettingsService;
import com.tanhua.app.service.UserInfoService;
import com.tanhua.commons.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private SettingsService settingsService;


    //用户资料 - 读取
    //GET/users
    @GetMapping
    public ResponseEntity findById(Long userID) {
        //                           @RequestHeader("Authorization") String token) {
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

        //从ThreadLocal中获取用户信息
        Long id = UserHolder.getUserId();
        if (userID == null) {
            userID = id;//如果没有值传进来，就是用当前登录的用户的id
        }

        //4 使用Service进行查询
        //UserInfo userInfo = userInfoService.findById(userID);//前端要求的数据格式和实体类不一致，需要使用vo
        UserInfoVo vo = userInfoService.findById(userID);
        //UserInfo userInfo = userInfoService.findById2(userID);

        //返回结果
        return ResponseEntity.ok(vo);
    }

    //用户资料 - 修改
    //PUT/users
    @PutMapping
    public ResponseEntity updateById(@RequestBody UserInfo userInfo,
                                     @RequestHeader("Authorization") String token) {
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

        //4 使用Service实现修改功能
        userInfoService.updateById(userInfo);

        //返回结果
        return ResponseEntity.ok(null);
    }

    //用户通用设置 - 读取
    //GET/users/settings
    @GetMapping("/settings")
    public ResponseEntity settings() {
        //调用SettingsService执行查询功能
        SettingsVo vo = settingsService.findSettings();

        //构建返回数据
        return ResponseEntity.ok(vo);
    }

    //设置陌生人问题 - 保存
    //POST/users/questions
    @PostMapping("/questions")
    public ResponseEntity questions(@RequestBody Map<String, String> params) {
        //1. 获取陌生人问题
        String content = params.get("content");

        //2. 调用Service进行处理
        settingsService.saveQuestions(content);

        //3. 构建返回结果数据
        return ResponseEntity.ok(null);
    }

    //通知设置 - 保存
    //POST/users/notifications/setting
    @PostMapping("/notifications/setting")
    public ResponseEntity notifications(@RequestBody Map<String, Boolean> params) {
        // 1. 调用Service进行保存操作
        settingsService.saveNotifications(params);
        // 2. 返回结果
        return ResponseEntity.ok(null);
    }

    //黑名单 - 翻页列表
    //GET/users/blacklist
    @GetMapping("/blacklist")
    public ResponseEntity blacklist(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int pagesize) {
        //1. 使用Service进行查询，返回vo对象 PageResult
        PageResult pageResult = settingsService.findBlacklist(page, pagesize);

        //2. 返回结果
        return ResponseEntity.ok(pageResult);
    }

    //黑名单 - 移除
    //DELETE/users/blacklist/:uid
    @DeleteMapping("/blacklist/{uid}")
    public ResponseEntity removeBlacklist(@PathVariable Long uid) {
        //调用Service实现功能
        settingsService.removeBlacklist(uid);

        //返回结果
        return ResponseEntity.ok(null);
    }

}
