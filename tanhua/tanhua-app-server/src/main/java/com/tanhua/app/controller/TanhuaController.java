package com.tanhua.app.controller;

import com.itheima.model.dto.RecommendUserDto;
import com.itheima.model.vo.PageResult;
import com.itheima.model.vo.TodayBest;
import com.tanhua.app.service.TanhuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.GET;
import java.util.Map;

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

    /**
     * 查询佳人信息
     */
    @GetMapping("/{id}/personalInfo")
    public ResponseEntity personalInfo(@PathVariable("id") Long userId) {
        TodayBest best = tanhuaService.personalInfo(userId);
        return ResponseEntity.ok(best);
    }

    /**
     * 查看陌生人问题
     */
    @GetMapping("/strangerQuestions")
    public ResponseEntity strangerQuestions(Long userId) {
        String questions = tanhuaService.strangerQuestions(userId);
        return ResponseEntity.ok(questions);
    }


    /**
     * 回复陌生人问题
     */
    @PostMapping("/strangerQuestions")
    public ResponseEntity replyQuestions(@RequestBody Map map) {
        //前端传递的userId:是Integer类型的
        String obj = map.get("userId").toString();
        Long userId = Long.valueOf(obj);
        String reply = map.get("reply").toString();
        tanhuaService.replyQuestions(userId,reply);
        return ResponseEntity.ok(null);
    }
}
