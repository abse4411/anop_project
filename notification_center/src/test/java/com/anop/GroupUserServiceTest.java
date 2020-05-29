package com.anop;

import com.anop.pojo.GroupUser;
import com.anop.resource.AutoTodoResource;
import com.anop.resource.GroupUserResource;
import com.anop.resource.GroupUserUpdateResource;
import com.anop.resource.PageParmResource;
import com.anop.service.GroupUserService;
import com.anop.util.test.MockUtils;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
public class GroupUserServiceTest {
    Logger logger = LoggerFactory.getLogger(GroupUserServiceTest.class);
    @Autowired
    GroupUserService groupUserService;

    @BeforeEach
    void mockLoginUser() {
        MockUtils.mockLoginUser("user");
    }

    @Test
    @Transactional
    @Rollback
    void TestDenyOperation() {
        final int badResult = -1;

        GroupUserResource groupUserInfo = groupUserService.getGroupUserInfo(3, 14);
        Assertions.assertNull(groupUserInfo, "用户不是该群成员,却可以获取该群成员信息");

        PageInfo<List<GroupUserResource>> listPageInfo = groupUserService.listGroupUser(new PageParmResource(), 14);
        Assertions.assertNull(listPageInfo, "用户不是该群成员,却可以获取该群成员信息");

        GroupUser groupUser = new GroupUser();
        groupUser.setUserId(3);
        groupUser.setGroupId(14);
        int result = groupUserService.deleteGroupUser(groupUser);
        Assertions.assertEquals(badResult, result, "用户不是该群管理员或者群主，却可以删除该群成员");
        groupUser.setUserId(2);
        groupUser.setGroupId(9);
        result = groupUserService.deleteGroupUser(groupUser);
        Assertions.assertEquals(badResult, result, "用户不是该群管理员或者群主，却可以删除该群成员");

        groupUser.setUserId(2);
        groupUser.setGroupId(9);
        GroupUserUpdateResource resource = new GroupUserUpdateResource();
        resource.setIsAdmin((byte) 1);
        result = groupUserService.updateGroupUserRole(groupUser, resource);
        Assertions.assertEquals(badResult, result, "用户不是该群群主，却可以更新该群成员");

        AutoTodoResource todoOption = groupUserService.getAutoTodoOption(14);
        Assertions.assertNull(todoOption, "用户不是该群成员，却可以获取该群自动代办事项选项信息");
        todoOption = new AutoTodoResource();
        result = groupUserService.updateAutoTodoOption(10, todoOption);
        Assertions.assertEquals(badResult, result, "用户不是该群成员，却可以更新该群自动代办事项选项信息");
    }

    @Test
    @Transactional
    @Rollback
    void TestAcceptOperation() {
        GroupUser groupUser = groupUserService.getGroupUser(3, 1);
        GroupUserResource groupUserInfo = groupUserService.getGroupUserInfo(groupUser.getUserId(), groupUser.getGroupId());
        Assertions.assertNotNull(groupUserInfo, "用户是该群成员，却不可以获取该群成员信息");

        PageInfo<List<GroupUserResource>> listPageInfo = groupUserService.listGroupUser(new PageParmResource(), 8);
        Assertions.assertNotNull(listPageInfo, "用户是该群成员,却不可以获取该群成员信息");
        Assertions.assertEquals(3, listPageInfo.getList().size());

        groupUser.setUserId(3);
        groupUser.setGroupId(1);
        int result = groupUserService.deleteGroupUser(groupUser);
        Assertions.assertTrue(result > 0, "用户是该群管理员或者群主，却不可以删除该群成员");
        groupUser.setId(5);
        groupUser.setUserId(3);
        groupUser.setGroupId(8);
        result = groupUserService.deleteGroupUser(groupUser);
        Assertions.assertTrue(result > 0, "用户是该群管理员或者群主，却不可以删除该群成员");

        groupUser.setId(10);
        groupUser.setUserId(1);
        groupUser.setGroupId(8);
        GroupUserUpdateResource resource = new GroupUserUpdateResource();
        resource.setIsAdmin((byte) 1);
        result = groupUserService.updateGroupUserRole(groupUser, resource);
        Assertions.assertTrue(result > 0, "用户是该群群主，却不可以更新该群成员");

        AutoTodoResource todoOption = groupUserService.getAutoTodoOption(1);
        Assertions.assertNotNull(todoOption, "用户是该群成员，却不可以获取该群自动代办事项选项信息");
        todoOption = new AutoTodoResource();
        result = groupUserService.updateAutoTodoOption(1, todoOption);
        Assertions.assertTrue(result > 0, "用户是该群成员，却不可以更新该群自动代办事项选项信息");
    }

}
