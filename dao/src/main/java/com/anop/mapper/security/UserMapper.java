package com.anop.mapper.security;

import com.anop.pojo.security.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

//@Mapper
public interface UserMapper {
    @Select("select * from user where username = #{username} or email=#{username}")
    User loadUserByUsername(@Param("username") String username);
}
