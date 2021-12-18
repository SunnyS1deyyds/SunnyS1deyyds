package cn.itcast.user.mapper;


import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {
    
    @Select("select username from tb_user where id = #{id}")
    String queryUsernameById(@Param("id") Long id);
}