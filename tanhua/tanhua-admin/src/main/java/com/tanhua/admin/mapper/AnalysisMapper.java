package com.tanhua.admin.mapper;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.model.pojo.Analysis;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AnalysisMapper extends BaseMapper<Analysis> {

    //根据日期查询统计数据
    @Select("SELECT * FROM tb_analysis WHERE record_date = #{today};")
    Analysis selectByRecordDate(@Param("today") String today);

    //累计用户数
    @Select("SELECT sum(num_registered) FROM tb_analysis")
    Long cumulativeUsers();
}
