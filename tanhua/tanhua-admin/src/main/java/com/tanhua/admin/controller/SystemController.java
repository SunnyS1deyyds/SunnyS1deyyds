package com.tanhua.admin.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.itheima.model.vo.AdminVo;
import com.tanhua.admin.service.AdminService;
import com.tanhua.commons.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/system/users")
public class SystemController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //生成图片验证码
    @GetMapping("/verification")
    public void verification(String uuid, HttpServletResponse response) throws IOException {
        //1 使用工具创建验证码对象
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(300, 100);

        //2 获取验证码并保存到redis当中
        redisTemplate.opsForValue().set(Constants.CAP_CODE + uuid, lineCaptcha.getCode());

        //3 输出验证码图片
        lineCaptcha.write(response.getOutputStream());
    }

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map map) {
        Map retMap = adminService.login(map);
        return ResponseEntity.ok(retMap);
    }

    /**
     * 获取管理员的信息
     */
    @PostMapping("/profile")
    public ResponseEntity profile() {
        AdminVo vo = adminService.profile();
        return ResponseEntity.ok(vo);
    }

}
