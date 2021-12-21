package com.tanhua.test;

import cn.hutool.core.date.DateUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

public class OssTest {

    private String endpoint = "oss-cn-beijing.aliyuncs.com";
    private String accessKeyId = "LTAI5tFz3fQcYuD3SQbsUY1G";
    private String accessKeySecret = "1OMqoyjTEKaeMawX88f1ErJ8doI5I4";
    private String bucket = "xuzy-tanhua-test";
    private String url = "https://xuzy-tanhua-test.oss-cn-beijing.aliyuncs.com/";

    public String upload(String fileName, InputStream inputStream) {

        // 生成 2021/12/21/uuid.jpg
        String path = DateUtil.format(new Date(), "yyyy/MM/dd/")
                + UUID.randomUUID() + fileName.substring(fileName.lastIndexOf("."));

        try {

            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 依次填写Bucket名称（例如examplebucket）和Object完整路径（例如exampledir/exampleobject.txt）。Object完整路径中不能包含Bucket名称。
            ossClient.putObject(bucket, path, inputStream);

            // 关闭OSSClient。
            ossClient.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return url + path;
    }

    public static void main(String[] args) throws Exception {
        String uploadFileName = "tree.jpg";
        String path = "C:\\0_itcast\\project10_tanhu\\课程资料\\软件包\\测试使用的图片和视频\\";

        FileInputStream inputStream = new FileInputStream(path + uploadFileName);

        OssTest ossTest = new OssTest();
        String uploadUrl = ossTest.upload(uploadFileName, inputStream);
        System.out.println(uploadUrl);
    }
}
