package com.tanhua.test;

import com.aliyun.tea.*;
import com.aliyun.dysmsapi20170525.*;
import com.aliyun.dysmsapi20170525.models.*;
import com.aliyun.teaopenapi.*;
import com.aliyun.teaopenapi.models.*;

public class SmsTest {

    private String accessKeyId = "LTAI5tFz3fQcYuD3SQbsUY1G";
    private String accessKeySecret = "1OMqoyjTEKaeMawX88f1ErJ8doI5I4";
    private String signName = "Tree";
    private String templateCode = "SMS_137670376";

    public void sendSms(String phone, String code) throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        com.aliyun.dysmsapi20170525.Client client = new com.aliyun.dysmsapi20170525.Client(config);

        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName(signName)
                .setTemplateCode(templateCode)
                .setPhoneNumbers(phone)
                .setTemplateParam("{\"code\":\"" + code + "\"}");
        // 复制代码运行请自行打印 API 的返回值
        client.sendSms(sendSmsRequest);
    }


    public static void main(String[] args) throws Exception {
        SmsTest smsTest = new SmsTest();
        smsTest.sendSms("17671886163","111111");
    }

}
