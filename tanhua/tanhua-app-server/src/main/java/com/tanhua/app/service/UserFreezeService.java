package com.tanhua.app.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itheima.model.vo.ErrorResult;
import com.tanhua.app.exception.BusinessException;
import com.tanhua.commons.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserFreezeService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    //根据用户id判断用户冻结的情况
    //根据state状态判断是要进行什么范围的冻结判断，只有用户id和冻结范围都一样，才会抛异常

    //userid 冻结的用户
    //freezingRange(冻结范围)：     1-冻结登录，2-冻结发言，3-冻结发布动态
    public void checkUserFreeze(Long userId, String state) {
        //1 查询redis的冻结数据
        String json = redisTemplate.opsForValue().get(Constants.FREEZE_USER + userId);

        //2 获取冻结范围
        if (StringUtils.isBlank(json)) {
            return;
        }

        Map map = JSON.parseObject(json, Map.class);
        String freezingRange = map.get("freezingRange").toString();

        //3 根据条件抛出冻结异常
        if (state.equals(freezingRange) && "1".equals(freezingRange)) {
            throw new BusinessException(ErrorResult.freezeLoginError());
        }

        if (state.equals(freezingRange) && "2".equals(freezingRange)) {
            throw new BusinessException(ErrorResult.freezeSpeakError());
        }

        if (state.equals(freezingRange) && "3".equals(freezingRange)) {
            throw new BusinessException(ErrorResult.freezeMovementError());
        }

    }

}
