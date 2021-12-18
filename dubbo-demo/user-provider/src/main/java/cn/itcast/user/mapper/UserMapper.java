package cn.itcast.user.mapper;


import cn.itcast.user.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {

    @Select("select username from tb_user where id = #{id}")
    String queryUsernameById(@Param("id") Long id);


    @Select("select * from tb_user where id = #{id}")
    User queryById(@Param("id") Long id);
}