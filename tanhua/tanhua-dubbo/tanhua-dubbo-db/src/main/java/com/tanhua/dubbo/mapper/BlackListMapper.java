package com.tanhua.dubbo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.model.pojo.BlackList;
import com.itheima.model.pojo.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface BlackListMapper extends BaseMapper<BlackList> {


    @Select("SELECT * FROM tb_user_info WHERE id  in(" +
            "SELECT black_user_id FROM tb_black_list WHERE user_id = #{userId}" +
            ")")
    Page<UserInfo> findByUserId(@Param("pages") Page<UserInfo> pages, @Param("userId") Long userId);
}
