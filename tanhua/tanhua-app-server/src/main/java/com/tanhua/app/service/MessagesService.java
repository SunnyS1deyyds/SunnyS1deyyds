package com.tanhua.app.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.model.pojo.User;
import com.itheima.model.pojo.UserInfo;
import com.itheima.model.vo.ContactVo;
import com.itheima.model.vo.PageResult;
import com.itheima.model.vo.UserInfoVo;
import com.tanhua.app.interceptor.UserHolder;
import com.tanhua.commons.utils.Constants;
import com.tanhua.config.template.HuanXinTemplate;
import com.tanhua.dubbo.api.FriendApi;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessagesService {

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private UserApi userApi;

    @DubboReference
    private FriendApi friendApi;


    public UserInfoVo findUserInfoByHuanxin(String huanxinId) {
        //1 根据环信账号查询用户信息
        User user = userApi.findByHanXinId(huanxinId);

        //2 根据用户id查询用户详情
        UserInfo userInfo = userInfoApi.findById(user.getId());

        //封装返回的vo
        //UserInfoVo vo = new UserInfoVo();
        //BeanUtils.copyProperties(userInfo, vo);
        //if (userInfo.getAge() != null) {//age必须转为字符串
        //    vo.setAge(userInfo.getAge().toString());
        //}
        UserInfoVo vo = UserInfoVo.init(userInfo);

        //返回结果
        return vo;
    }

    //添加好友
    public void contacts(Long friendId) {
        //把好友关系注册到环信服务器中
        Boolean aBoolean = huanXinTemplate.addContact(
                Constants.HX_USER_PREFIX + UserHolder.getUserId(),
                Constants.HX_USER_PREFIX + friendId);
        //如果环信中的好友关系注册成功，保存好友关系到MongoDB中
        if (aBoolean) {
            //调用FriendApi实现好友的添加
            friendApi.addContact(UserHolder.getUserId(), friendId);
        }

    }

    public PageResult findFriends(Integer page, Integer pagesize, String keyword) {
        //1 根据当前用户，查询所有好友的用户id
        List<Long> friendIds = friendApi.findFriends(UserHolder.getUserId());

        //2 根据关键词 分页查询用户详情
        Page<UserInfo> pages = userInfoApi.findPage(friendIds, page, pagesize, keyword);

        //3 封装vo
        List<ContactVo> vos = new ArrayList<>();
        for (UserInfo userInfo : pages.getRecords()) {
            ContactVo vo = ContactVo.init(userInfo);
            vos.add(vo);
        }

        //返回结果
        return new PageResult(page, pagesize, (int) pages.getTotal(), vos);
    }
}
