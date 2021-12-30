package com.tanhua.test;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.app.AppServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class FastDfsTest {

    @Autowired //先请求Tracker服务 ，获取存储文件的Storage服务信息，返回某个Storage的服务器
    private FastFileStorageClient client;

    @Autowired
    private FdfsWebServer webServer;//获取服务的请求地址

    @Test
    public void fastDfsTest() throws FileNotFoundException {
        //声明文件地址
        String filePath = "C:\\0_itcast\\project10_tanhu\\课程资料\\软件包\\测试使用的图片和视频\\古诗-竹石.mp4";
        File file = new File(filePath);

        //上传文件
        StorePath path = client.uploadFile(new FileInputStream(file)
                , file.length(), "mp4", null);

        //上传的同时生成缩略图
        //StorePath path = client.uploadImageAndCrtThumbImage(new FileInputStream(file)
        //        , file.length(), "jpg", null);

        System.out.println("FullPath:" + path.getFullPath());
        System.out.println("Path:" + path.getPath());

        //拼接文件的访问地址
        String url = webServer.getWebServerUrl() + path.getFullPath();

        System.out.println(url);
    }
}
