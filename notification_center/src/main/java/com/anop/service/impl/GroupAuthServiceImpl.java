package com.anop.service.impl;

import com.anop.pojo.security.User;
import com.anop.service.GroupAuthService;
import com.anop.service.GroupService;
import com.anop.service.GroupUserService;
import com.anop.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 通知中心权限检查业务逻辑默认实现
 *
 * @author Xue_Feng
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class GroupAuthServiceImpl implements GroupAuthService {
    @Autowired
    GroupUserService groupUserService;
    @Autowired
    GroupService groupService;

    private boolean hasBaseRole(int groupId) {
        Integer currentUserId = SecurityUtils.getLoginUser(User.class).getId();
        return groupService.isGroupCreator(currentUserId, groupId) || groupUserService.hasAdminRole(currentUserId, groupId);
    }

    private boolean hasMasterRole(int groupId) {
        Integer currentUserId = SecurityUtils.getLoginUser(User.class).getId();
        return groupService.isGroupCreator(currentUserId, groupId);
    }

    private boolean hasAdminRole(int groupId) {
        Integer currentUserId = SecurityUtils.getLoginUser(User.class).getId();
        return groupUserService.hasAdminRole(currentUserId, groupId);
    }

    private boolean hasCommonRole(int groupId) {
        Integer currentUserId = SecurityUtils.getLoginUser(User.class).getId();
        return groupUserService.hasCommonRole(currentUserId, groupId);
    }

    @Override
    public boolean canUpdateGroupInfo(int groupId) {
        return hasBaseRole(groupId);
    }

    @Override
    public boolean canDeleteGroup(int groupId) {
        return hasMasterRole(groupId);
    }

    @Override
    public boolean canUpdateGroupUserRole(int groupId) {
        return hasMasterRole(groupId);
    }

    @Override
    public boolean canDeleteGroupUser(int groupId) {
        return hasBaseRole(groupId);
    }

    @Override
    public boolean canHandleUserRequest(int groupId) {
        return hasBaseRole(groupId);
    }

    @Override
    public boolean canAddNotification(int groupId) {
        return hasBaseRole(groupId);
    }

    @Override
    public boolean canUpdateNotification(int groupId) {
        return hasBaseRole(groupId);
    }

    @Override
    public boolean canDeleteNotification(int groupId) {
        return hasBaseRole(groupId);
    }

    @Override
    public boolean canMarkNotification(int groupId) {
        return hasCommonRole(groupId);
    }

    @Override
    public boolean canListReceiver(int groupId) {
        return hasBaseRole(groupId);
    }

    @Override
    public boolean canTurnNotificationIntoTodo(int groupId) {
        return hasCommonRole(groupId);
    }

    @Override
    public boolean canGetReceiverNotification(int groupId) {
        return hasCommonRole(groupId);
    }

    @Override
    public boolean canQuitGroup(int groupId) {
        return hasCommonRole(groupId) || hasAdminRole(groupId);
    }
}
