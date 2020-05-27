package com.anop.mapper;

import com.anop.pojo.Todo;
import com.anop.resource.MailTodoResource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自定义待办事项Mapper，用于提醒邮件的信息获取
 *
 * @author ZYF
 */
public interface CustomTodoMapper {
    /**
     * 获取用户需要提醒的待办事项列表
     *
     * @param userId 用户id
     * @return 待办事项列表
     */
    List<MailTodoResource> selectByUserId(@Param(value = "userId") int userId);

    /**
     * 批量添加待办事项
     *
     * @param userIds 用户id列表
     * @param record 待办事项
     * @return
     */
    int insertBatch(@Param(value = "userIds") List<Integer> userIds, @Param("record") Todo record);
}
