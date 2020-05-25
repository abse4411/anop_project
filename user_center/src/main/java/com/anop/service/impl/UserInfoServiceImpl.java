package com.anop.service.impl;

import com.anop.config.FileHandleConfig;
import com.anop.mapper.CustomUserInfoMapper;
import com.anop.mapper.UserInfoMapper;
import com.anop.pojo.UserInfo;
import com.anop.pojo.example.UserInfoExample;
import com.anop.resource.UserInfoResource;
import com.anop.resource.UserInfoUpdateResource;
import com.anop.service.UserInfoService;
import com.anop.util.PropertyMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * @author SilverBay
 */
@Service
@Transactional
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    CustomUserInfoMapper customUserInfoMapper;

    @Override
    public UserInfo getUserInfoByUserId(int userId) {
        UserInfoExample example = new UserInfoExample();
        UserInfoExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        List<UserInfo> list =  userInfoMapper.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public UserInfoResource getUserInfoResource(int userId) {
        return customUserInfoMapper.selectByUserId(userId);
    }

    @Override
    public int updateUserInfo(UserInfo userInfo, UserInfoUpdateResource resource) {
        UserInfo newUserInfo = PropertyMapperUtils.map(resource, UserInfo.class);
        newUserInfo.setId(userInfo.getId());
        return userInfoMapper.updateByPrimaryKeySelective(newUserInfo);
    }

    @Override
    public String saveAvatarFile(MultipartFile file) throws IOException {

        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String MD5Name = DigestUtils.md5DigestAsHex(file.getBytes());

        File newFile = new File(FileHandleConfig.getUploadPath() + MD5Name + suffix);
        if(newFile.exists() == false) {
            file.transferTo(newFile);
        }

        return FileHandleConfig.AVATAR_SAVE_PATH + newFile.getName();
    }

    @Override
    public int updateAvatarUrl(UserInfo userInfo, String url) {
        userInfo.setAvatarUrl(url);
        return userInfoMapper.updateByPrimaryKey(userInfo);
    }

}
