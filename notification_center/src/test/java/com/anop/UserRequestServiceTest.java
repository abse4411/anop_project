package com.anop;

import com.anop.pojo.UserRequest;
import com.anop.resource.PageParmResource;
import com.anop.resource.UserRequestAddResource;
import com.anop.resource.UserRequestResource;
import com.anop.service.UserRequestService;
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
public class UserRequestServiceTest {
    Logger logger = LoggerFactory.getLogger(UserRequestServiceTest.class);
    @Autowired
    UserRequestService userRequestService;

    @BeforeEach
    void mockLoginUser() {
        MockUtils.mockLoginUser("user");
    }

    @Test
    @Transactional
    @Rollback
    void TestDenyOperation() {
        final int badResult = -1;
        UserRequestAddResource resource = new UserRequestAddResource();
        resource.setGroupId(99);

        int result = userRequestService.addUserRequest(resource);
        Assertions.assertTrue(result <= badResult, "通知群组不存在，却可申请加入群");
        resource.setGroupId(1);
        result = userRequestService.addUserRequest(resource);
        Assertions.assertTrue(result <= badResult, "用户已在群内，却可申请加入群");
        resource.setGroupId(5);
        result = userRequestService.addUserRequest(resource);
        Assertions.assertTrue(result <= badResult, "该群不允许任何人加入，却可申请加入群");

        UserRequest request = new UserRequest();
        request.setId(11);
        request.setUserId(4);
        request.setGroupId(9);
        request.setIsAccepted((byte) 1);
        result = userRequestService.acceptOrDenyUserRequest(request, (byte) 1);
        Assertions.assertTrue(result <= badResult, "申请已被处理，却可以再次处理");
        request.setIsAccepted((byte) 0);
        result = userRequestService.acceptOrDenyUserRequest(request, (byte) 3);
        Assertions.assertTrue(result <= badResult, "无效的isAccepted，却可以再次处理");
        request.setIsAccepted((byte) 0);
        result = userRequestService.acceptOrDenyUserRequest(request, (byte) 1);
        Assertions.assertTrue(result <= badResult, "用户不是该群管理员或者群主，却可以处理申请");

    }

    @Test
    @Transactional
    @Rollback
    void TestAcceptOperation() {
        UserRequest request = userRequestService.getUserRequest(2);
        Assertions.assertNotNull(request, "无法获取加入申请");

        PageInfo<List<UserRequestResource>> listPageInfo = userRequestService.listUserRequest(new PageParmResource());
        Assertions.assertNotNull(listPageInfo, "无法获取用户创建的群组的加入申请");
        Assertions.assertEquals(3, listPageInfo.getList().size(), "无法获取用户创建的群组的加入申请");
        userRequestService.listManageUserRequest(new PageParmResource());
        Assertions.assertNotNull(listPageInfo, "无法用户获取管理的群组的加入申请");
        Assertions.assertEquals(3, listPageInfo.getList().size(), "无法获取用户管理的群组的加入申请");

        UserRequestAddResource resource = new UserRequestAddResource();
        resource.setGroupId(6);
        int result = userRequestService.addUserRequest(resource);
        Assertions.assertTrue(result > 0, "用户无法加入允许任何人加入的群组");

        resource.setGroupId(14);
        result = userRequestService.addUserRequest(resource);
        Assertions.assertTrue(result > 0, "用户发送申请到需要审核的群组");

        resource.setGroupId(10);
        result = userRequestService.addUserRequest(resource);
        Assertions.assertTrue(result > 0, "用户发送申请到需要审核的群组");

        resource.setGroupId(4);
        result = userRequestService.addUserRequest(resource);
        Assertions.assertTrue(result > 0, "用户发送申请到需要审核的群组");

        result = userRequestService.acceptOrDenyUserRequest(request, (byte) 1);
        Assertions.assertTrue(result > 0, "用户作为群管理员或者群组无法审核该群加入申请");
        request = userRequestService.getUserRequest(1);
        result = userRequestService.acceptOrDenyUserRequest(request, (byte) 2);
        Assertions.assertTrue(result > 0, "用户作为群管理员或者群组无法审核该群加入申请");

    }

}
