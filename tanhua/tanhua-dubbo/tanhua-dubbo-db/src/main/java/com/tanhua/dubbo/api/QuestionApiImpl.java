package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.model.pojo.Question;
import com.tanhua.dubbo.mapper.QuestionMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class QuestionApiImpl implements QuestionApi {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Question findByUserId(Long userId) {
        return questionMapper.selectOne(
                Wrappers.lambdaQuery(Question.class)
                        .eq(Question::getUserId, userId)
        );
    }

    @Override
    public void save(Question question) {
        questionMapper.insert(question);
    }

    @Override
    public void update(Question question) {
        questionMapper.updateById(question);
    }
}
