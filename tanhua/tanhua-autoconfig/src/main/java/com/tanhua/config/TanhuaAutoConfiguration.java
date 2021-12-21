package com.tanhua.config;

import com.tanhua.config.properties.AipProperties;
import com.tanhua.config.properties.OssProperties;
import com.tanhua.config.properties.SmsProperties;
import com.tanhua.config.template.AipTemplate;
import com.tanhua.config.template.OssTemplate;
import com.tanhua.config.template.SmsTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({
        SmsProperties.class,
        OssProperties.class,
        AipProperties.class
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

}
