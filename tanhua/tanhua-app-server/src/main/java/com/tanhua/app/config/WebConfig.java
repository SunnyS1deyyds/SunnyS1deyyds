package com.tanhua.app.config;

import com.tanhua.app.interceptor.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInterceptor())
                .addPathPatterns("/**")//拦截所有的请求   都需要进行用户鉴权
                .excludePathPatterns("/user/login", "/user/loginVerification");//登录接口放行，不鉴权
    }
}
