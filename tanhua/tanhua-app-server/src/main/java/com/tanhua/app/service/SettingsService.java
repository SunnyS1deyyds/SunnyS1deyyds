package com.tanhua.app.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.model.pojo.Question;
import com.itheima.model.pojo.Settings;
import com.itheima.model.vo.PageResult;
import com.itheima.model.vo.SettingsVo;
import com.tanhua.app.interceptor.UserHolder;
import com.tanhua.dubbo.api.BlackListApi;
import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.api.SettingsApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SettingsService {

    @DubboReference
    private QuestionApi questionApi;

    @DubboReference
    private SettingsApi settingsApi;

    @DubboReference
    private BlackListApi blackListApi;


    public SettingsVo findSettings() {
        //1. 获取用户的id
        Long userId = UserHolder.getUserId();

        //创建VO对象
        SettingsVo vo = new SettingsVo();

        vo.setId(userId);
        vo.setPhone(UserHolder.getUserMobile());

        //2. 根据用户id查询陌生人问题
        Question question = questionApi.findByUserId(userId);
        String txt = question == null ? "你喜欢java吗？" : question.getTxt();
        vo.setStrangerQuestion(txt);

        //3. 查询通知设置
        Settings settings = settingsApi.findByUserId(userId);
        //如果查询结果不为空，设置值，如果为空，默认值已经有了，都是true
        if (settings != null) {
            vo.setGonggaoNotification(settings.getGonggaoNotification());
            vo.setLikeNotification(settings.getLikeNotification());
            vo.setPinglunNotification(settings.getPinglunNotification());
        }

        //返回结果
        return vo;
    }

    public void saveQuestions(String content) {
        //1. 获取用户id
        Long userId = UserHolder.getUserId();

        //2. 根据用户id查询 陌生人问题
        Question question = questionApi.findByUserId(userId);

        //3. 判断查询结果是否为空
        if (question == null) {
            //3.1 如果为空，新增
            question.setUserId(userId);
            question.setTxt(content);

            questionApi.save(question);

        } else {
            //3.2 如果不为空，修改
            question.setTxt(content);

            questionApi.update(question);
        }

    }

    public void saveNotifications(Map<String, Boolean> params) {
        //解析参数
        Boolean likeNotification = params.get("likeNotification");
        Boolean pinglunNotification = params.get("pinglunNotification");
        Boolean gonggaoNotification = params.get("gonggaoNotification");

        //1. 获取用户id
        Long userId = UserHolder.getUserId();

        //2. 根据用户id查询通知设置
        Settings settings = settingsApi.findByUserId(userId);

        //3. 判断查询结果是否为可控
        if (settings == null) {
            //3.1 为空 新增
            settings = new Settings();
            settings.setUserId(userId);
            settings.setLikeNotification(likeNotification);
            settings.setGonggaoNotification(gonggaoNotification);
            settings.setPinglunNotification(pinglunNotification);

            settingsApi.save(settings);

        } else {
            //3.2 不为空 修改
            settings.setLikeNotification(likeNotification);
            settings.setGonggaoNotification(gonggaoNotification);
            settings.setPinglunNotification(pinglunNotification);

            settingsApi.update(settings);
        }


    }

    public PageResult findBlacklist(int page, int pagesize) {
        //1。 获取用户id
        Long userId = UserHolder.getUserId();

        //2. 调用Api执行分页查询
        Page pages = blackListApi.findByUserId(userId, page, pagesize);

        //3. 封装数据到VO中
        PageResult pageResult = new PageResult(page, pagesize, (int) pages.getTotal(), pages.getRecords());

        //返回vo
        return pageResult;
    }

    public void removeBlacklist(Long uid) {
        //获取登录用户的id
        Long userId = UserHolder.getUserId();

        //调用api执行删除
        blackListApi.removeBlacklist(userId, uid);

    }
}
