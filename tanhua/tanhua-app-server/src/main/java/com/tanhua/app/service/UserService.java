package com.tanhua.app.service;

import cn.hutool.core.util.RandomUtil;
import com.itheima.model.pojo.User;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.config.template.SmsTemplate;
import com.tanhua.dubbo.api.UserApi;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private SmsTemplate smsTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @DubboReference
    private UserApi userApi;

    //存放手机验证码的key的前缀
    private String CHECK_CODE = "CHECK_CODE_";


    public void login(String mobile) {
        ////1. 生成6位数字验证码
        //String code = RandomUtil.randomNumbers(6);
        //
        ////2. 调用smsTemplate发短信
        //smsTemplate.sendSms(mobile, code);

        //3. 把验证码存入到Redis中
        String code = "123456";
        redisTemplate.opsForValue().set(CHECK_CODE + mobile, code, Duration.ofMinutes(5));
    }

    public Map loginVerification(String mobile, String code) {

        //1 从redis获取验证码并校验
        String redisCode = redisTemplate.opsForValue().get(CHECK_CODE + mobile);

        //如果校验失败，直接抛异常
        if (redisCode == null || !redisCode.equals(code)) {
            throw new RuntimeException("用户手机校验码，校验错误！");
        }

        //校验成功，删除验证码  ？  根据业务要求处理
        redisTemplate.delete(CHECK_CODE + mobile);

        //2 根据用户名查询用户
        User user = userApi.findByMobile(mobile);

        boolean isNew = false;//默认不是新用户

        //3 判断用户是否存在，若不存在添加新用户
        if (user == null) {
            user = new User();
            user.setMobile(mobile);
            user.setPassword(DigestUtils.md5Hex("123456"));

            Long id = userApi.save(user);

            user.setId(id);

            //如果是新用户，设置为true
            isNew = true;
        }


        //4 使用工具类生成token   token中存放用户的手机号和用户的主键id
        Map map = new HashMap();
        map.put("mobile", mobile);
        map.put("id", user.getId());

        String token = JwtUtils.getToken(map);


        //5 构造返回并响应
        //返回数据   token  jwt-token字符串  |||  isNew	是否新用户
        Map reMap = new HashMap();
        reMap.put("isNew", isNew);
        reMap.put("token", token);

        return reMap;
    }
}
