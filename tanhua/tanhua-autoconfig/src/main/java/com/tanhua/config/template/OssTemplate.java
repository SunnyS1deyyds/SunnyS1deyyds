package com.tanhua.config.template;

import cn.hutool.core.date.DateUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.tanhua.config.properties.OssProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

public class OssTemplate {

    private OssProperties ossProperties;

    public OssTemplate(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    public String upload(String fileName, InputStream inputStream) {

        // 生成 2021/12/21/uuid.jpg
        String path = DateUtil.format(new Date(), "yyyy/MM/dd/")
                + UUID.randomUUID() + fileName.substring(fileName.lastIndexOf("."));

        try {

            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(
                    ossProperties.getEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret());

            // 依次填写Bucket名称（例如examplebucket）和Object完整路径（例如exampledir/exampleobject.txt）。Object完整路径中不能包含Bucket名称。
            ossClient.putObject(ossProperties.getBucket(), path, inputStream);

            // 关闭OSSClient。
            ossClient.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ossProperties.getUrl() + path;
    }
}
