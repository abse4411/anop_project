package com.anop.mapper.security;


import com.anop.pojo.security.RolePermisson;
import org.apache.ibatis.annotations.Select;

import java.util.List;


//@Mapper
public interface PermissionMapper {

    @Select("SELECT A.NAME AS roleName,C.url FROM sys_role AS A JOIN role_permission B ON A.id=B.role_id LEFT JOIN sys_permission AS C ON B.permission_id=C.id")
    List<RolePermisson> getRolePermissions();

}
