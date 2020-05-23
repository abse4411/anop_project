package com.anop.mapper.security;

import com.anop.pojo.security.Role;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


//@Mapper
public interface RoleMapper {

    @Select("SELECT A.id,A.name FROM sys_role A LEFT JOIN sys_user_role B ON A.id=B.role_id WHERE B.user_id=${userId}")
    List<Role> getRolesByUserId(@Param("userId") Integer userId);

}
