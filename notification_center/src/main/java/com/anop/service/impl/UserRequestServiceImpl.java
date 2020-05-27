package com.anop.service.impl;

import com.anop.mapper.CustomUserRequestMapper;
import com.anop.mapper.GroupUserMapper;
import com.anop.mapper.UserRequestMapper;
import com.anop.pojo.GroupUser;
import com.anop.pojo.UserRequest;
import com.anop.pojo.example.UserRequestExample;
import com.anop.pojo.security.User;
import com.anop.resource.GroupUserAddResource;
import com.anop.resource.PageParmResource;
import com.anop.resource.UserRequestAddResource;
import com.anop.resource.UserRequestResource;
import com.anop.service.GroupAuthService;
import com.anop.service.GroupService;
import com.anop.service.GroupUserService;
import com.anop.service.UserRequestService;
import com.anop.util.PageSortHelper;
import com.anop.util.PropertyMapperUtils;
import com.anop.util.SecurityUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 通知群组加入请求业务逻辑默认实现
 *
 * @author Xue_Feng
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class UserRequestServiceImpl implements UserRequestService {
    private static final byte ACCEPT = 1;
    private static final byte DENY = 2;
    private static final byte PENDING = 0;
    private static final byte COMMON_ROLE = 0;
    private static final byte DISABLE_AUTO_TODO = 0;

    @Autowired
    CustomUserRequestMapper customUserRequestMapper;
    @Autowired
    GroupUserService groupUserService;
    @Autowired
    GroupService groupService;
    @Autowired
    UserRequestMapper userRequestMapper;
    @Autowired
    GroupUserMapper groupUserMapper;
    @Autowired
    GroupAuthService authService;

    private int addGroupUser(int userId, int groupId) {
        GroupUserAddResource resource = new GroupUserAddResource();
        resource.setUserId(userId);
        resource.setGroupId(groupId);
        GroupUser newGroupUser = PropertyMapperUtils.map(resource, GroupUser.class);
        newGroupUser.setIsAdmin(COMMON_ROLE);
        newGroupUser.setIsAuto(DISABLE_AUTO_TODO);
        return groupUserMapper.insert(newGroupUser);
    }

    @Override
    public int addUserRequest(UserRequestAddResource resource) {
        int currentUserId = SecurityUtils.getLoginUser(User.class).getId();
        if (!groupService.hasGroup(resource.getGroupId())) {
            return -1;
        }
        if (groupUserService.isInGroup(currentUserId, resource.getGroupId())) {
            return -2;
        }
        if (groupService.isPrivateGroup(resource.getGroupId())) {
            return -3;
        }
        if (groupService.isPublicGroup(resource.getGroupId())) {
            return addGroupUser(currentUserId, resource.getGroupId());
        }
        UserRequestExample requestExample = new UserRequestExample();
        UserRequestExample.Criteria criteria = requestExample.createCriteria();
        criteria.andUserIdEqualTo(currentUserId);
        criteria.andGroupIdEqualTo(resource.getGroupId());
        List<UserRequest> requests = userRequestMapper.selectByExample(requestExample);
        UserRequest userRequest;
        boolean isNew = false;
        if (requests.size() > 0) {
            userRequest = requests.get(0);
            if (userRequest.getIsAccepted() == PENDING) {
                return 1;
            }
        } else {
            isNew = true;
            userRequest = PropertyMapperUtils.map(resource, UserRequest.class);
            userRequest.setUserId(currentUserId);
        }
        userRequest.setIsAccepted(PENDING);
        userRequest.setRequestTime(new Date());
        if (isNew) {
            return userRequestMapper.insert(userRequest);
        }
        return userRequestMapper.updateByPrimaryKey(userRequest);
    }

    @Override
    public UserRequest getUserRequest(int requestId) {
        UserRequest userRequest = userRequestMapper.selectByPrimaryKey(requestId);
        return userRequest;
    }

    @Override
    public PageInfo<List<UserRequestResource>> listUserRequest(PageParmResource page) {
        PageSortHelper.pageAndSort(page, UserRequestResource.class);
        List<UserRequestResource> resources = customUserRequestMapper.listUserRequest(SecurityUtils.getLoginUser(User.class).getId());
        return new PageInfo(resources);
    }

    @Override
    public PageInfo<List<UserRequestResource>> listManageUserRequest(PageParmResource page) {
        PageSortHelper.pageAndSort(page, UserRequestResource.class);
        List<UserRequestResource> resources = customUserRequestMapper.listManageUserRequest(SecurityUtils.getLoginUser(User.class).getId());
        return new PageInfo(resources);
    }

    @Override
    public int acceptOrDenyUserRequest(UserRequest request, byte isAccepted) {
        if (!authService.canHandleUserRequest(request.getGroupId())) {
            return -2;
        }
        if (request.getIsAccepted() != PENDING) {
            return -1;
        }
        if (groupUserService.isInGroup(request.getUserId(), request.getGroupId())) {
            return -3;
        }
        if (groupService.isPrivateGroup(request.getGroupId())) {
            return -4;
        }
        if (isAccepted == DENY) {
            request.setIsAccepted(DENY);
            return userRequestMapper.updateByPrimaryKey(request);
        }
        if (isAccepted == ACCEPT) {
            addGroupUser(request.getUserId(), request.getGroupId());
            request.setIsAccepted(ACCEPT);
            return userRequestMapper.updateByPrimaryKey(request);
        } else {
            return -5;
        }
    }
}
