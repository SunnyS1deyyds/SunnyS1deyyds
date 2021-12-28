package com.tanhua.dubbo.api;

import java.util.List;

public interface UserLocationApi {

    //保存或更新用户的地理位置
    Boolean updateLocation(Long userId, Double longitude, Double latitude, String address);

    //查询指定用户的附近的人
    List<Long> queryNearUser(Long userId, String distance);
}
