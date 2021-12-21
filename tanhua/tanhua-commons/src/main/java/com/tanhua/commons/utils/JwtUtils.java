package com.tanhua.commons.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * @author Administrator
 */
public class JwtUtils {

    /**
     * TOKEN的有效期1小时（S）为了测试方便，设置token过期时间为7天
     */
    private static final int TOKEN_TIME_OUT = 7 * 24 * 3600;

    /**
     * 加密KEY,尽量复杂，不要有意义
     */
    private static final String TOKEN_SECRET = "itcast";
    //private static final String TOKEN_SECRET = "QhH^b81j7KjUYN@clCECuvE^U!HL0j";


    /**
     * 生成Token
     *
     * @param params
     */
    public static String getToken(Map params) {
        long currentTime = System.currentTimeMillis();
        try {
            return Jwts.builder()
                    //加密方式与秘钥
                    .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encode(TOKEN_SECRET.getBytes("UTF-8")))
                    //过期时间戳
                    .setExpiration(new Date(currentTime + TOKEN_TIME_OUT * 1000))
                    .addClaims(params)
                    .compact();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取Token中的claims信息
     */
    public static Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(Base64.getEncoder().encode(TOKEN_SECRET.getBytes("UTF-8")))
                    .parseClaimsJws(token).getBody();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 是否有效 true-有效，false-失效
     */
    public static boolean verifyToken(String token) {

        if (StringUtils.isEmpty(token)) {
            return false;
        }

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(Base64.getEncoder().encode(TOKEN_SECRET.getBytes("UTF-8")))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}