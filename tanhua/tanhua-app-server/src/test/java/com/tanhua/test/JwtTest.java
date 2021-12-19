package com.tanhua.test;

import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    //jwt生成的token是使用base64加密的，是可逆的加密
    //为了保证用户的安全，token当中不要存放用户的隐私数据

    //使用Jwts工具，生成token
    @Test
    public void testCreateToken() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("phone", "13800138000");

        String token = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, "itcast")
                .setClaims(map)
                .setExpiration(new Date(System.currentTimeMillis() + 300000))
                .compact();

        System.out.println(token);
    }


    //使用Jwts工具，解析token
    //token超时过期错误 ExpiredJwtException
    //token校验错误 SignatureException
    @Test
    public void testParseToken() {
        //String token = "eyJhbGciOiJIUzI1NiJ9.eyJwaG9uZSI6IjEzODAwMTM4MDAwIiwiaWQiOjEsImV4cCI6MTYzOTg5OTA0N30.6ZmDl6KlJQwVAWzuV3ScVSLpI-FaI5PsE3FWdJukLLU";
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJwaG9uZSI6IjEzODAwMTM4MDAwIiwiaWQiOjEsImV4cCI6MTYzOTg5OTc1N30.1p4KyR_cjSYDp02j9GV-kghkugt8igGtpy49Ce5xWtQ";

        try {
            //解析token
            Claims claims = Jwts.parser().setSigningKey("itcast")
                    .parseClaimsJws(token)
                    .getBody();

            System.out.println(claims.get("id"));
            System.out.println(claims.get("phone"));
        } catch (ExpiredJwtException e) {
            System.out.println("token超时了");
        } catch (SignatureException e) {
            System.out.println("token校验失败");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }
}
