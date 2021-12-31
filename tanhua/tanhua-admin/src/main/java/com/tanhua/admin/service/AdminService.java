package com.tanhua.admin.service;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.model.pojo.Admin;
import com.itheima.model.vo.AdminVo;
import com.tanhua.admin.exception.BusinessException;
import com.tanhua.admin.interceptor.AdminHolder;
import com.tanhua.admin.mapper.AdminMapper;
import com.tanhua.commons.utils.Constants;
import com.tanhua.commons.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    //@Autowired
    @Resource
    private AdminMapper adminMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //管理员登录
    public Map login(Map map) {
        //1 从map中获取请求参数 username,password,verificationCode（验证码）,uuid）
        String username = (String) map.get("username");
        String password = (String) map.get("password");
        String verificationCode = (String) map.get("verificationCode");
        String uuid = (String) map.get("uuid");

        //2 判断账户密码是否为空，如果为空直接抛异常提示
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new BusinessException("账号和密码不能为空");
        }

        //3 判断用户输入的验证码是否为空，如果为空抛异常提示
        if (StringUtils.isBlank(verificationCode)) {
            throw new BusinessException("验证码不能为空");
        }

        //4 从redis中查询验证码，进行验证码的校验
        String redisCode = redisTemplate.opsForValue().get(Constants.CAP_CODE + uuid);
        if (!verificationCode.equals(redisCode)) {
            throw new BusinessException("验证码错误");
        }

        //5 根据用户名查询管理员信息
        Admin admin = adminMapper.selectOne(Wrappers.lambdaQuery(Admin.class).eq(Admin::getUsername, username));

        //6 判断密码是否正确   传入的密码需要加密后再比对
        if (admin == null || !admin.getPassword().equals(SecureUtil.md5(password))) {
            //如果用户未查到，或者密码错误，抛异常
            throw new BusinessException("账号或者密码错误");
        }

        //7 封装需要存到token中的数据   username  id
        Map<String, Object> params = new HashMap<>();
        params.put("id", admin.getId());
        params.put("username", username);

        //8 生成token
        String token = JwtUtils.getToken(params);

        //9 返回结果  封装返回的Map   "token": 生成的token
        //Map rMap = new HashMap();
        //rMap.put("token", token);
        return MapUtil.builder("token", token).build();

    }

    public AdminVo profile() {
        //查询当前用户的id
        Long userId = AdminHolder.getUserId();

        //根据id查询管理员数据
        Admin admin = adminMapper.selectOne(Wrappers.lambdaQuery(Admin.class).eq(Admin::getId, userId));

        //封装为vo  返回数据
        return AdminVo.init(admin);
    }
}
