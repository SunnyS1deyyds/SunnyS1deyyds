package com.tanhua.config;

import com.tanhua.config.properties.*;
import com.tanhua.config.template.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({
        SmsProperties.class,
        OssProperties.class,
        AipProperties.class,
        HuanXinProperties.class,
        GreenProperties.class
})
public class TanhuaAutoConfiguration {

    @Bean
    public SmsTemplate smsTemplate(SmsProperties smsProperties) {
        return new SmsTemplate(smsProperties);
    }

    @Bean
    public OssTemplate ossTemplate(OssProperties ossProperties) {
        return new OssTemplate(ossProperties);
    }

    @Bean
    public AipTemplate aipTemplate() {
        return new AipTemplate();
    }

    //其他省略
    @Bean
    public HuanXinTemplate huanXinTemplate(HuanXinProperties properties) {
        return new HuanXinTemplate(properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "tanhua.green",value = "enable", havingValue = "true")
    public GreenTemplate aliyunGreenTemplate(GreenProperties properties) {
        return new GreenTemplate(properties);
    }
}
