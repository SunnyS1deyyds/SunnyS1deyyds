package com.tanhua.app.controller;

import com.itheima.model.mongo.Movement;
import com.itheima.model.vo.MovementsVo;
import com.itheima.model.vo.PageResult;
import com.itheima.model.vo.VisitorsVo;
import com.tanhua.app.service.CommentsService;
import com.tanhua.app.service.MovementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/movements")
public class MovementsController {

    @Autowired
    private MovementsService movementsService;

    @Autowired
    private CommentsService commentsService;

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

    //好友动态
    //GET/movements
    @GetMapping
    public ResponseEntity findFriendMovements(@RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "10") Integer pagesize) {
        //调用Service执行查询
        PageResult pageResult = movementsService.findFriendMovements(page, pagesize);

        //返回结果
        return ResponseEntity.ok(pageResult);
    }


    /**
     * 查询推荐动态列表
     */
    @GetMapping("/recommend")
    public ResponseEntity recommend(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pr = movementsService.findRecommendMovements(page, pagesize);
        return ResponseEntity.ok(pr);
    }

    /**
     * 查询单条动态
     */
    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable("id") String movementId) {
        if ("visitors".equals(movementId)) {
            return ResponseEntity.ok(null);
        }

        MovementsVo vo = movementsService.findById(movementId);
        return ResponseEntity.ok(vo);
    }


    /**
     * 点赞
     */
    @GetMapping("/{id}/like")
    public ResponseEntity like(@PathVariable("id") String movementId) {
        Integer likeCount = commentsService.likeComment(movementId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * 评论取消点赞
     */
    @GetMapping("/{id}/dislike")
    public ResponseEntity dislike(@PathVariable("id") String movementId) {
        Integer likeCount = commentsService.dislikeComment(movementId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * 喜欢动态
     */
    @GetMapping("/{id}/love")
    public ResponseEntity love(@PathVariable("id") String movementId) {
        Integer likeCount = commentsService.loveComment(movementId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * 取消动态喜欢
     */
    @GetMapping("/{id}/unlove")
    public ResponseEntity unlove(@PathVariable("id") String movementId) {
        Integer likeCount = commentsService.unlove(movementId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * 谁看过我
     */
    @GetMapping("visitors")
    public ResponseEntity queryVisitorsList(){
        List<VisitorsVo> list = movementsService.queryVisitorsList();
        return ResponseEntity.ok(list);
    }

}
