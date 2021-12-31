package com.tanhua.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanhua.commons.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    //获取不需要鉴权的请求路径
    @Value(("${gateway.excludedUrls}"))
    private List<String> excludedUrls;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1. 获取用户的请求路径
        String url = exchange.getRequest().getURI().getPath();
        System.out.println(url);

        //2. 判断该路径url是否需要鉴权
        if (excludedUrls.contains(url)) {
            //不需要鉴权，放行
            return chain.filter(exchange);
        }

        //3. 获取请求头中的token
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        //如果token不为空，需要把Bearer 也给去掉，有可能token传递的时候，会在token前面添加Bearer
        if (StringUtils.isNotBlank(token)) {
            token = token.replace("Bearer ", "");
        }

        //4. 进行用户鉴权操作
        boolean verifyToken = JwtUtils.verifyToken(token);

        //5. 判断鉴权是否失败，如果失败，直接返回401提示信息
        if (!verifyToken) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("errCode", "401");
            responseData.put("errMessage", "用户未登录");

            return responseError(exchange.getResponse(), responseData);
        }

        //如果成功，直接放行
        return chain.filter(exchange);
    }

    //响应错误数据
    private Mono<Void> responseError(ServerHttpResponse response, Map<String, Object> responseData) {
        // 将信息转换为 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] data = new byte[0];
        try {
            data = objectMapper.writeValueAsBytes(responseData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // 输出错误信息到页面
        DataBuffer buffer = response.bufferFactory().wrap(data);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
