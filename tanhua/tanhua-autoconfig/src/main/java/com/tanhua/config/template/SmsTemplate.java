package com.tanhua.config.template;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.teaopenapi.models.Config;
import com.tanhua.config.properties.SmsProperties;

public class SmsTemplate {

    private SmsProperties smsProperties;

    public SmsTemplate(SmsProperties smsProperties) {
        this.smsProperties = smsProperties;
    }

    public void sendSms(String phone, String code) {
        try {
            Config config = new Config()
                    // 您的AccessKey ID
                    .setAccessKeyId(smsProperties.getAccessKeyId())
                    // 您的AccessKey Secret
                    .setAccessKeySecret(smsProperties.getAccessKeySecret());
            // 访问的域名
            config.endpoint = "dysmsapi.aliyuncs.com";
            com.aliyun.dysmsapi20170525.Client client = new com.aliyun.dysmsapi20170525.Client(config);

            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setSignName(smsProperties.getSignName())
                    .setTemplateCode(smsProperties.getTemplateCode())
                    .setPhoneNumbers(phone)
                    .setTemplateParam("{\"code\":\"" + code + "\"}");
            // 复制代码运行请自行打印 API 的返回值
            client.sendSms(sendSmsRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
