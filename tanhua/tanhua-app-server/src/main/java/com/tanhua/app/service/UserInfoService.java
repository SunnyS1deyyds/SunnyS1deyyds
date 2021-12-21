package com.tanhua.app.service;

import com.itheima.model.pojo.UserInfo;
import com.itheima.model.vo.UserInfoVo;
import com.tanhua.config.template.AipTemplate;
import com.tanhua.config.template.OssTemplate;
import com.tanhua.dubbo.api.UserInfoApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserInfoService {

    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private AipTemplate aipTemplate;

    public void save(UserInfo userInfo) {
        userInfoApi.save(userInfo);
    }

    public void upload(MultipartFile headPhoto, Long id) throws IOException {

        //1. 调用OSS工具类，实现图片上传
        final String imageUrl = ossTemplate.upload(headPhoto.getOriginalFilename(), headPhoto.getInputStream());

        //2. 使用AIP工具类，校验图片是否包括人脸
        boolean detect = aipTemplate.detect(imageUrl);

        //2.1 如果不包括人脸  抛异常提示图片不包括人脸
        if (!detect) {
            throw new RuntimeException("图片不包括人脸");
        }

        //2.2 如果包括人脸，使用Dubbo调用API，更新UserInfo
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setAvatar(imageUrl);

        userInfoApi.update(userInfo);
    }

    public UserInfoVo findById(Long userID) {
        UserInfo userInfo = userInfoApi.findById(userID);

        //把 UserInfo 的值复制到  UserInfoVo中
        UserInfoVo vo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo, vo);

        //设置年龄  把年龄改为String类型
        if (userInfo.getAge() != null) {
            vo.setAge(userInfo.getAge().toString());
        }

        return vo;
    }

    public UserInfo findById2(Long userID) {
        UserInfo userInfo = userInfoApi.findById(userID);

        return userInfo;
    }

    public void updateById(UserInfo userInfo) {
        userInfoApi.update(userInfo);
    }
}
