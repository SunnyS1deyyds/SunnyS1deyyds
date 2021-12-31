package com.tanhua.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.model.mongo.Movement;
import com.itheima.model.mongo.Video;
import com.itheima.model.pojo.UserInfo;
import com.itheima.model.vo.MovementsVo;
import com.itheima.model.vo.PageResult;
import com.itheima.model.vo.UserInfoVo;
import com.itheima.model.vo.VideoVo;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ManagerService {

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private VideoApi videoApi;

    @DubboReference
    private MovementApi movementApi;

    public PageResult findAllUsers(Integer page, Integer pagesize) {
        //1 分页查询所有的用户
        Page<UserInfo> pages = userInfoApi.findPage(page, pagesize);

        //2 构建vos
        List<UserInfoVo> vos = new ArrayList<>();
        for (UserInfo userInfo : pages.getRecords()) {
            UserInfoVo vo = UserInfoVo.init(userInfo);
            vos.add(vo);
        }

        //3 返回结果
        return new PageResult(page, pagesize, (int) pages.getTotal(), vos);
    }

    //根据id查询用户详情
    public UserInfo findUserById(Long userId) {
        return userInfoApi.findById(userId);
    }

    //根据用户id，分页查询发布的视频
    public PageResult findAllVideos(Integer page, Integer pagesize, Long uid) {
        //需要查询的是MongoDB，分页查询的结果是Video，但是分页数据中的总记录数

        //1. 分页查询就返回PageResult  但是里面的List集合存放的是Video
        PageResult pageResult = videoApi.findAllVideos(uid, page, pagesize);
        List<Video> items = pageResult.getItems();

        //2. 根据uid查询用户详情信息
        UserInfo userInfo = userInfoApi.findById(uid);

        //3 最终返回的是 VideoVo  封装vo数据
        List<VideoVo> vos = new ArrayList<>();
        for (Video video : items) {
            VideoVo vo = VideoVo.init(userInfo, video);
            vos.add(vo);
        }

        //4 把PageResult里面的  存放着Video的List  替换为存放VideoVo的list
        pageResult.setItems(vos);

        //返回结果
        return pageResult;
    }

    //根据用户id，分页查询指定状态的动态数据
    public PageResult findAllMovements(Integer page, Integer pagesize, Long uid, Integer state) {
        //1 根据用户id和状态分页查询动态数据
        PageResult pageResult = movementApi.findAllMovements(uid, state, page, pagesize);
        List<Movement> movementList = pageResult.getItems();

        //2 根据用户id查询用户详情信息
        UserInfo userInfo = userInfoApi.findById(uid);

        //3 封装vo数据
        List<MovementsVo> vos = new ArrayList<>();
        for (Movement movement : movementList) {
            MovementsVo vo = MovementsVo.init(userInfo, movement);
            vos.add(vo);
        }

        //4 把PageResult里面存放Movement的list  替换为  存放MovementVo的list
        pageResult.setItems(vos);

        //返回结果
        return pageResult;
    }
}
