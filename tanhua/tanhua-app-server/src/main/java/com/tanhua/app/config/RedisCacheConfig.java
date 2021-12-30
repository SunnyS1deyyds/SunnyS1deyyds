package com.tanhua.app.config;

import com.google.common.collect.ImmutableMap;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.time.Duration;
import java.util.Map;

@Configuration
public class RedisCacheConfig {

    private static final Map<String, Duration> cacheMap;

    // 设置失效时间
    // put 方法第一个参数为缓存的名称空间，就是 @Cacheable注解的value属性
    // put 方法第二个参数就是过期时间
    static {
        cacheMap = ImmutableMap.<String, Duration>builder().put("videos", Duration.ofSeconds(30L)).build();
    }

    //配置RedisCacheManagerBuilderCustomizer对象
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> {
            //根据不同的cachename设置不同的失效时间
            for (Map.Entry<String, Duration> entry : cacheMap.entrySet()) {
                builder.withCacheConfiguration(entry.getKey(),
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(entry.getValue()));
            }
        };
    }
}
