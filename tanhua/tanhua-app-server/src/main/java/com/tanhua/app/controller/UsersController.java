package com.tanhua.app.controller;

import cn.hutool.http.HttpStatus;
import com.itheima.model.pojo.UserInfo;
import com.itheima.model.vo.UserInfoVo;
import com.tanhua.app.interceptor.UserHolder;
import com.tanhua.app.service.UserInfoService;
import com.tanhua.commons.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.PUT;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UserInfoService userInfoService;


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
}
