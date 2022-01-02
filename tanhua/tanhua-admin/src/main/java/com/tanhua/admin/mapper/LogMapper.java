package com.tanhua.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.model.pojo.Log;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface LogMapper extends BaseMapper<Log> {
    //每日的 新注册用户数
    //每日的 用户登陆次数
    @Select("SELECT count(DISTINCT user_id) FROM tb_log WHERE type = #{type} AND log_time = #{today}")
    Integer countByTypeAndDate(@Param("type") String type, @Param("today") String today);

    //每日的 活跃用户数
    @Select("SELECT count(DISTINCT user_id) FROM tb_log WHERE log_time = #{today}")
    Integer countActive(@Param("today") String today);

    //次日留存用户数
    @Select("SELECT count(DISTINCT user_id) FROM tb_log WHERE log_time = #{today} AND " +
            "user_id in (SELECT DISTINCT user_id FROM tb_log WHERE type = \"0102\" AND log_time = #{yestoday})")
    Integer countRetention1d(@Param("today")String today, @Param("yestoday")String yestoday);
}
