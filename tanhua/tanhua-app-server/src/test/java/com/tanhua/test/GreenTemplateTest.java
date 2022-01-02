package com.tanhua.test;

import cn.hutool.core.collection.ListUtil;
import com.tanhua.app.AppServerApplication;
import com.tanhua.config.template.GreenTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class GreenTemplateTest {

    @Autowired
    private GreenTemplate greenTemplate;

    @Test
    public void testGreenTemplate() throws Exception {
        //文本内容检测
        Map<String, String> textMap1 = greenTemplate.greenTextScan("今天天气很好，阳光明媚，适合敲代码");
        textMap1.forEach((k, v) -> System.out.println(k + ":::" + v));
        System.out.println("==========================================================");

        Map<String, String> textMap2 = greenTemplate.greenTextScan("本店大量出售枪支弹药，欲购从速");
        textMap2.forEach((k, v) -> System.out.println(k + ":::" + v));
        System.out.println("==========================================================");

        //图片内容检测
        Map<String, String> imageMap1 = greenTemplate.imageScan(ListUtil
                .toList("https://pic.ntimg.cn/20130510/3822951_151908214000_2.jpg",
                        "https://pic.ntimg.cn/20130604/3822951_103146840000_2.jpg"));
        imageMap1.forEach((k, v) -> System.out.println(k + ":::" + v));
        System.out.println("==========================================================");

        Map<String, String> imageMap2 = greenTemplate.imageScan(ListUtil
                .toList("http://pic.enorth.com.cn/0/08/51/34/8513473_372964.jpg",
                        "https://img.redocn.com/sheying/20160922/xiaohelvshulinfengjing_7151745.jpg"));
        imageMap2.forEach((k, v) -> System.out.println(k + ":::" + v));
        System.out.println("==========================================================");
    }
}
