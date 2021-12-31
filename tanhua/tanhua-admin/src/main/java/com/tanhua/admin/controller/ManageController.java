package com.tanhua.admin.controller;

import com.itheima.model.pojo.UserInfo;
import com.itheima.model.vo.PageResult;
import com.tanhua.admin.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manage")
public class ManageController {

    @Autowired
    private ManagerService managerService;

    //查询用户列表
    @GetMapping("/users")
    public ResponseEntity users(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult result = managerService.findAllUsers(page, pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据id查询用户详情
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity findUserById(@PathVariable("userId") Long userId) {
        UserInfo userInfo = managerService.findUserById(userId);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 查询指定用户发布的所有视频列表
     */
    @GetMapping("/videos")
    public ResponseEntity videos(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pagesize,
                                 Long uid ) {
        PageResult result = managerService.findAllVideos(page,pagesize,uid);
        return ResponseEntity.ok(result);
    }

    //查询动态
    @GetMapping("/messages")
    public ResponseEntity messages(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pagesize,
                                   Long uid,Integer state) {
        PageResult result = managerService.findAllMovements(page,pagesize,uid,state);
        return ResponseEntity.ok(result);
    }
}