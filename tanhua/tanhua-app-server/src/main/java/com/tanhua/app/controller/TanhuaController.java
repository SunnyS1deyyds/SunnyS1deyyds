package com.tanhua.app.controller;

import com.itheima.model.dto.RecommendUserDto;
import com.itheima.model.vo.PageResult;
import com.itheima.model.vo.TodayBest;
import com.tanhua.app.service.TanhuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.GET;

@RestController
@RequestMapping("/tanhua")
public class TanhuaController {

    @Autowired
    private TanhuaService tanhuaService;

    //今日佳人
    //GET/tanhua/todayBest
    @GetMapping("/todayBest")
    public ResponseEntity todayBest() {
        //调用Service查询今日佳人
        TodayBest vo = tanhuaService.todayBest();

        //返回数据
        return ResponseEntity.ok(vo);
    }

    //推荐好友列表(分页查询)
    //GET/tanhua/recommendation
    @GetMapping("/recommendation")
    public ResponseEntity<PageResult<TodayBest>> recommendation(RecommendUserDto dto) {
        //调用Service执行查询
        PageResult<TodayBest> pageResult = tanhuaService.findRecommendUser(dto);

        //返回结果
        return ResponseEntity.ok(pageResult);
    }
}
