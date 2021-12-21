package com.tanhua.test;

import com.tanhua.app.AppServerApplication;
import com.tanhua.config.template.OssTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class OssTemplateTest {

    @Autowired
    private OssTemplate ossTemplate;

    @Test
    public void test() throws FileNotFoundException {
        String uploadFileName = "tree.jpg";
        String path = "C:\\0_itcast\\project10_tanhu\\课程资料\\软件包\\测试使用的图片和视频\\";

        FileInputStream inputStream = new FileInputStream(path + uploadFileName);

        ossTemplate.upload(uploadFileName, inputStream);
    }
}
