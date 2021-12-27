package com.tanhua.config;

import com.tanhua.config.properties.AipProperties;
import com.tanhua.config.properties.HuanXinProperties;
import com.tanhua.config.properties.OssProperties;
import com.tanhua.config.properties.SmsProperties;
import com.tanhua.config.template.AipTemplate;
import com.tanhua.config.template.HuanXinTemplate;
import com.tanhua.config.template.OssTemplate;
import com.tanhua.config.template.SmsTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({
        SmsProperties.class,
        OssProperties.class,
        AipProperties.class,
        HuanXinProperties.class
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

}
