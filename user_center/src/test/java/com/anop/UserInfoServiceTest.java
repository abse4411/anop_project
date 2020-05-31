package com.anop;

import com.anop.pojo.UserInfo;
import com.anop.pojo.security.User;
import com.anop.resource.UserInfoUpdateResource;
import com.anop.service.UserInfoService;
import com.anop.util.SecurityUtils;
import com.anop.util.test.MockUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@SpringBootTest
public class UserInfoServiceTest {

    @Autowired
    UserInfoService service;

    @BeforeEach
    public void mockLoginUser() {
        MockUtils.mockLoginUser("zwn");
    }

    @Test
    @Transactional
    @Rollback
    public void getInfoTest() {
        User user = SecurityUtils.getLoginUser(User.class);
        UserInfo userInfo = service.getUserInfoByUserId(user.getId());

        Assertions.assertNotNull(userInfo, "不存在的用户");
        Assertions.assertEquals("zhaoweinan", userInfo.getNickname(), "用户昵称不匹配");
        Assertions.assertEquals("", userInfo.getAvatarUrl(), "用户头像路径不匹配");
        Assertions.assertEquals(user.getId(), userInfo.getUserId(), "用户ID不匹配");

        userInfo = service.getUserInfoByUserId(8);
        Assertions.assertNull(userInfo, "8号用户不存在，却可以获取到");

    }

    @Test
    @Transactional
    @Rollback
    public void updateInfoTest() {
        UserInfoUpdateResource resource = new UserInfoUpdateResource();
        resource.setNickname("Honey");
        resource.setAvatarUrl("url");

        UserInfo userInfo = service.getUserInfoByUserId(7);
        service.updateUserInfo(userInfo, resource);
        userInfo = service.getUserInfoByUserId(7);

        Assertions.assertEquals("Honey", userInfo.getNickname(), "更新用户昵称失败");
        Assertions.assertEquals("url", userInfo.getAvatarUrl(), "更新用户头像url失败");
    }

    @Test
    @Transactional
    @Rollback
    public void updateAvatarTest() {

        MultipartFile file = null;
        try {
            service.saveAvatarFile(file);
            Assertions.fail("保存不存在的文件成功");
        } catch (Exception e) {
            // TODO
        }
    }

}
