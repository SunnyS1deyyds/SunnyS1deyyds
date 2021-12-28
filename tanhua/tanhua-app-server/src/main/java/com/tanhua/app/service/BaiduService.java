package com.tanhua.app.service;

import com.itheima.model.vo.ErrorResult;
import com.tanhua.app.exception.BusinessException;
import com.tanhua.app.interceptor.UserHolder;
import com.tanhua.dubbo.api.UserLocationApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class BaiduService {

    @DubboReference
    private UserLocationApi userLocationApi;

    public void updateLocation(Double longitude, Double latitude, String address) {
        //保存用户的地理位置信息
        Boolean flag = userLocationApi.updateLocation(UserHolder.getUserId(), longitude, latitude, address);

        //判断是否保存成功，如果保存失败抛异常
        if (!flag) {
            throw new BusinessException(ErrorResult.error());
        }
    }
}
