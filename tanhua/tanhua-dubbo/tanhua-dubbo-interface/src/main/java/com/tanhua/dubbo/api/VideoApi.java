package com.tanhua.dubbo.api;

import com.itheima.model.mongo.Video;
import com.itheima.model.vo.PageResult;

import java.util.List;

public interface VideoApi {

    //保存小视频数据
    String save(Video video);

    //根据vid(推荐系统)查询小视频
    List<Video> queryVideoList(List<Long> vids);

    //分页查询小视频 需要按照创建时间倒序排序
    List<Video> queryVideoList(int page, Integer pagesize);

    //根据用户id分页查询视频数据
    PageResult findAllVideos(Long uid, Integer page, Integer pagesize);
}
