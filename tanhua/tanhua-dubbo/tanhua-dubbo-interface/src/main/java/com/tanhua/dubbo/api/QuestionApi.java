package com.tanhua.dubbo.api;

import com.itheima.model.pojo.Question;

public interface QuestionApi {

    //根据用户id查询  陌生人问题
    Question findByUserId(Long userId);

    //新增问题
    void save(Question question);

    //修改问题
    void update(Question question);
}
