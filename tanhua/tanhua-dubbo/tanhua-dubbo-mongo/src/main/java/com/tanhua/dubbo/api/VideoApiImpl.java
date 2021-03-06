package com.tanhua.dubbo.api;

import com.itheima.model.mongo.Video;
import com.itheima.model.vo.PageResult;
import com.tanhua.dubbo.utils.IdWorker;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class VideoApiImpl implements VideoApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdWorker idWorker;


    @Override
    public String save(Video video) {
        //设置vid唯一标识
        video.setVid(idWorker.getNextId("video"));

        //保存
        mongoTemplate.save(video);

        //返回视频的主键id
        return video.getId().toHexString();
    }

    //根据vid(推荐系统)查询小视频
    @Override
    public List<Video> queryVideoList(List<Long> vids) {
        return mongoTemplate.find(Query.query(
                Criteria.where("vid").in(vids)
        ), Video.class);
    }

    //分页查询小视频 需要按照创建时间倒序排序
    @Override
    public List<Video> queryVideoList(int page, Integer pagesize) {
        return mongoTemplate.find(new Query()
                .skip((page - 1) * pagesize)
                .limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created"))), Video.class);
    }

    @Override
    public PageResult findAllVideos(Long uid, Integer page, Integer pagesize) {
        //1 封装查询条件
        Query query = Query.query(Criteria.where("userId").is(uid));

        //2 查询数据的总记录数
        long count = mongoTemplate.count(query, Video.class);

        //3 设置分页的条件
        query.skip((page - 1) * pagesize)
                .limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created")));

        //4 查询当前页的List结果集
        List<Video> list = mongoTemplate.find(query, Video.class);

        return new PageResult(page, pagesize, (int) count, list);
    }
}
