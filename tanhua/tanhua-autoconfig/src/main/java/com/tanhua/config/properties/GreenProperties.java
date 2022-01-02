package com.tanhua.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("tanhua.green")
public class GreenProperties {
    //账号
    String accessKeyId;
    //密钥
    String accessKeySecret;
    //场景
    String scenes;
}