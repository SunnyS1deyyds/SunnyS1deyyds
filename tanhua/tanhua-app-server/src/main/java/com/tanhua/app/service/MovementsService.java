package com.tanhua.app.service;

import com.itheima.model.mongo.Movement;
import com.itheima.model.pojo.UserInfo;
import com.itheima.model.vo.ErrorResult;
import com.itheima.model.vo.MovementsVo;
import com.itheima.model.vo.PageResult;
import com.tanhua.app.exception.BusinessException;
import com.tanhua.app.interceptor.UserHolder;
import com.tanhua.config.template.OssTemplate;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MovementsService {

    @Autowired
    private OssTemplate ossTemplate;

    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    public void pushlishMovement(Movement movement, MultipartFile[] imageContent) throws IOException {

        //1. 判断动态内容是否为空，如果为空，直接抛异常，全局异常处理器处理
        if (StringUtils.isBlank(movement.getTextContent())) {
            throw new BusinessException(ErrorResult.contentError());
        }

        //2. 获取当前用户的id
        Long userId = UserHolder.getUserId();


        //3. oss图片上传
        List<String> medias = new ArrayList<>();
        for (MultipartFile file : imageContent) {
            String imageUrl = ossTemplate.upload(file.getOriginalFilename(), file.getInputStream());
            medias.add(imageUrl);
        }

        //4. 调用API实现动态发布
        movement.setMedias(medias);
        movement.setCreated(new Date().getTime());
        movement.setUserId(userId);

        movementApi.pushlishMovement(movement);

    }

    public PageResult all(Long userId, Integer page, Integer pagesize) {
        //1 判断userId是否为空，如果为空，使用当前登录的用户id
        if (userId == null) {
            userId = UserHolder.getUserId();
        }

        //2 根据用户id查询动态数据
        PageResult pageResult = movementApi.findPageByUserId(userId, page, pagesize);

        //判断动态数据是否为空，如果为空，直接返回
        if (pageResult.getItems() == null || pageResult.getItems().isEmpty()) {
            return pageResult;
        }

        //3 根据用户id查询用户详情
        UserInfo userInfo = userInfoApi.findById(userId);

        List<Movement> movementList = pageResult.getItems();

        //4 遍历多个动态，封装vo数据
        List<MovementsVo> vos = new ArrayList<>();
        for (Movement movement : movementList) {
            MovementsVo vo = MovementsVo.init(userInfo, movement);
            vos.add(vo);
        }

        //5 封装返回结果对象，返回数据
        //重新设置结果接Items，因为要返回的是vo对象  分页数据是一样的
        pageResult.setItems(vos);
        return pageResult;
    }
}
