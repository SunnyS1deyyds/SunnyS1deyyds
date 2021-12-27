package com.tanhua.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("tanhua.huanxin")
public class HuanXinProperties {

    private String appkey;
    private String clientId;
    private String clientSecret;

}
