package com.tanhua.dubbo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.tanhua.dubbo.mapper"})
public class DubboDbApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboDbApplication.class, args);
    }
}
