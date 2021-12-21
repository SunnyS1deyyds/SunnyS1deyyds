package com.tanhua.app.interceptor;

import cn.hutool.http.HttpStatus;
import com.itheima.model.pojo.User;
import com.tanhua.commons.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserInterceptor extends HandlerInterceptorAdapter {

    //在执行Controller方法之前，先进行用户的鉴权
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1. 获取token
        String token = request.getHeader("Authorization");

        //2. 校验token
        boolean verifyToken = JwtUtils.verifyToken(token);

        //3. 如果校验失败，设置响应代码为401  返回false
        if (!verifyToken) {
            response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
            return false;
        }

        //4. 解析token，获取用户相关的信息
        Claims claims = JwtUtils.getClaims(token);
        Long id = Long.parseLong(claims.get("id").toString());
        String mobile = claims.get("mobile").toString();

        //5. 创建User对象，设置到ThreadLocal中
        User user = new User();
        user.setId(id);
        user.setMobile(mobile);

        UserHolder.setUser(user);

        //6. 如果校验成功，放行，返回true
        return true;

    }

    //执行Controller之后，从ThreadLocal中移除User
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
