package com.tanhua.app.service;

import cn.hutool.core.util.PageUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.itheima.model.mongo.Video;
import com.itheima.model.pojo.UserInfo;
import com.itheima.model.vo.PageResult;
import com.itheima.model.vo.VideoVo;
import com.itheima.model.vo.VisitorsVo;
import com.tanhua.app.interceptor.UserHolder;
import com.tanhua.commons.utils.Constants;
import com.tanhua.config.template.OssTemplate;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SmallVideosService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private FastFileStorageClient client;

    @Autowired
    private FdfsWebServer webServer;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private MqMessageService mqMessageService;


    @DubboReference
    private VideoApi videoApi;

    @DubboReference
    private UserInfoApi userInfoApi;


    public void saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        //1 上传视频文件  自己的FastDFS
        //获取视频的名字
        String oriName = videoFile.getOriginalFilename();
        //通过视频的名字，获取视频的文件后缀  注意，需要+1操作，因为不需要 .
        String extName = oriName.substring(oriName.lastIndexOf(".") + 1);
        StorePath path = client.uploadFile(videoFile.getInputStream(), videoFile.getSize(), extName, null);

        //获取视频访问url
        String videoUrl = webServer.getWebServerUrl() + path.getFullPath();

        //2 上传视频封面图片 阿里OSS
        String picUrl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());

        //3 封装video视频数据
        Video video = new Video();
        video.setCreated(System.currentTimeMillis());
        video.setUserId(UserHolder.getUserId());
        video.setVideoUrl(videoUrl);
        video.setPicUrl(picUrl);

        //4 调用api进行保存
        String id = videoApi.save(video);

        //发送用户的操作日志消息
        mqMessageService.sendLogMessage(UserHolder.getUserId(), "0301", "video", id);
    }

    //分页 查询小视频列表
    //缓存的key： 用户id_page_pagesize   例如： 106_1_2
    //key可以使用SpringEL  "T(com.tanhua.app.interceptor.UserHolder).getUserId()" 执行UserHolder的getUserId()静态方法
    @Cacheable(value = "videos", key =
            "T(com.tanhua.app.interceptor.UserHolder).getUserId()+'_'+#page+'_'+#pagesize")
    public PageResult queryVideoList(Integer page, Integer pagesize) {
        //1 有推荐系统，进行小视频的推荐，数据存在redis中
        //需要先查询redis中的推荐数据,推荐数据  100001,100004,100005
        String key = Constants.VIDEOS_RECOMMEND + UserHolder.getUserId();
        String value = redisTemplate.opsForValue().get(key);//100001,100004,100005

        //2 判断是否存在推荐数据
        List<Video> videoList = new ArrayList<>();
        int redisPage = 0;
        if (StringUtils.isNotBlank(value)) {
            //3 如果存在推荐数据，对推荐数据进行分页查询
            String[] arr = value.split(",");//100001,100004,100005
            if (arr.length > (page - 1) * pagesize) {//判断推荐数据是否足够进行分页查询
                List<Long> vids = Arrays.stream(arr)
                        .skip((page - 1) * pagesize)
                        .limit(pagesize)
                        .map(vid -> Long.parseLong(vid))
                        .collect(Collectors.toList());

                videoList = videoApi.queryVideoList(vids);
            }
            //获取redis的总页数
            redisPage = PageUtil.totalPage(arr.length, pagesize);

        }

        if (page > redisPage) {
            //假设推荐小视频(redis中的数据)，总也是是3页，我们希望前3页显示的是推荐视频，从第4开始，显示从MongoDB中直接查询的数据
            //redis（总页数）: 3页       MongoDB有其他小视频数据
            //第1页     redis：1页
            //第2页     redis：2页
            //第3页     redis：3页
            //第4页     MongoDB：1页
            //第5页     MongoDB：2页
            //........
            // 用户需要查询的页码数(page) -  redis的页码数(redisPage)

            //4 如果不存在推进数据，直接使用API查询MongoDB数据库，小视频数据  需要按照创建时间倒序排序
            videoList = videoApi.queryVideoList(page - redisPage, pagesize);
        }

        //5 根据小视频数据，获取发布小视频的人的ids
        List<Long> ids = videoList.stream().map(Video::getUserId).collect(Collectors.toList());

        //6 根据用户的ids 查询用户详情  转为Map方便使用
        Map<Long, UserInfo> map = userInfoApi.findByUserIds(ids).stream()
                .collect(Collectors.toMap(UserInfo::getId, Function.identity()));

        //7 根据小视频的数据和用户详情，构建vo对象
        List<VideoVo> vos = new ArrayList<>();
        for (Video video : videoList) {
            UserInfo userInfo = map.get(video.getUserId());
            if (userInfo != null) {
                VideoVo vo = VideoVo.init(userInfo, video);
                vos.add(vo);
            }
        }

        //构建分页对象，返回   前端不需要显示总记录数，总记录数设置为0
        return new PageResult(page, pagesize, 0, vos);

    }
}
