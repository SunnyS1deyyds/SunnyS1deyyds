package com.tanhua.app.controller;

import com.itheima.model.mongo.Movement;
import com.itheima.model.vo.PageResult;
import com.tanhua.app.service.MovementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.POST;
import java.io.IOException;

@RestController
@RequestMapping("/movements")
public class MovementsController {

    @Autowired
    private MovementsService movementsService;

    //动态-发布
    //POST/movements
    @PostMapping
    public ResponseEntity movements(Movement movement,
                                    MultipartFile[] imageContent) throws IOException {
        //调用Service执行发布动态
        movementsService.pushlishMovement(movement, imageContent);

        //返回数据结果
        return ResponseEntity.ok(null);
    }

    //我的动态
    //GET/movements/all
    @GetMapping("/all")
    public ResponseEntity all(@RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer pagesize,
                              Long userId) {
        //调用Service执行查询
        PageResult pageResult = movementsService.all(userId, page, pagesize);

        //返回结果数据
        return ResponseEntity.ok(pageResult);
    }

}
