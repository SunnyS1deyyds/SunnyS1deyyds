package com.tanhua.test;

import com.tanhua.app.AppServerApplication;
import com.tanhua.config.template.AipTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class AipTemplateTest {

    @Autowired
    private AipTemplate aipTemplate;

    @Test
    public void testAipTemplate() {
        String imageUrl1 = "https://xuzy-tanhua-test.oss-cn-beijing.aliyuncs.com/2021/12/21/620639bb-f776-47d5-9aa8-1bae6c86bd79.jpg";
        boolean result1 = aipTemplate.detect(imageUrl1);
        String imageUrl2 = "https://xuzy-tanhua-test.oss-cn-beijing.aliyuncs.com/2021/12/21/0b1fade4-5aaf-440f-8033-c433650a83af.jpg";
        boolean result2 = aipTemplate.detect(imageUrl2);

        System.out.println(result1);
        System.out.println(result2);
    }
}
