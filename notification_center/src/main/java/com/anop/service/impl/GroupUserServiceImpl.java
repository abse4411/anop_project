package com.anop.service.impl;

import com.anop.mapper.CustomGroupUserMapper;
import com.anop.mapper.GroupUserMapper;
import com.anop.pojo.GroupUser;
import com.anop.pojo.example.GroupUserExample;
import com.anop.pojo.security.User;
import com.anop.resource.AutoTodoResource;
import com.anop.resource.GroupUserResource;
import com.anop.resource.GroupUserUpdateResource;
import com.anop.resource.PageParmResource;
import com.anop.service.GroupAuthService;
import com.anop.service.GroupService;
import com.anop.service.GroupUserService;
import com.anop.util.PageSortHelper;
import com.anop.util.PropertyMapperUtils;
import com.anop.util.SecurityUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 通知群组业务通知中心权限检查业务逻辑默认实现
 *
 * @author Xue_Feng
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class GroupUserServiceImpl implements GroupUserService {
    private static final byte ADMIN_ROLE = 1;
    private static final byte COMMON_ROLE = 0;
    @Autowired
    GroupUserMapper groupUserMapper;
    @Autowired
    CustomGroupUserMapper customGroupUserMapper;
    @Autowired
    GroupService groupService;
    @Autowired
    GroupAuthService authService;

    @Override
    public boolean hasAdminRole(int userId, int groupId) {
        GroupUser user = getGroupUser(userId, groupId);
        if (user != null && user.getIsAdmin() == ADMIN_ROLE) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasCommonRole(int userId, int groupId) {
        GroupUser user = getGroupUser(userId, groupId);
        if (user != null && user.getIsAdmin() == COMMON_ROLE) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isInGroup(int userId, int groupId) {
        GroupUser user = getGroupUser(userId, groupId);
        return user != null || groupService.isGroupCreator(userId, groupId);
    }

    @Override
    public GroupUserResource getGroupUserInfo(int userId, int groupId) {
        if (!isInGroup(SecurityUtils.getLoginUser(User.class).getId(), groupId)) {
            return null;
        }
        return customGroupUserMapper.selectGroupUser(userId, groupId);
    }

    @Override
    public GroupUser getGroupUser(int userId, int groupId) {
        GroupUserExample example = new GroupUserExample();
        GroupUserExample.Criteria criteria = example.createCriteria();
        criteria.andGroupIdEqualTo(groupId);
        criteria.andUserIdEqualTo(userId);
        List<GroupUser> groupUsers = groupUserMapper.selectByExample(example);
        if (groupUsers.size() > 0) {
            return groupUsers.get(0);
        }
        return null;
    }

    @Override
    public PageInfo<List<GroupUserResource>> listGroupUser(PageParmResource page, int groupId) {
        if (!isInGroup(SecurityUtils.getLoginUser(User.class).getId(), groupId)) {
            return null;
        }
        PageSortHelper.pageAndSort(page, GroupUserResource.class);
        List<GroupUserResource> groupUsers = customGroupUserMapper.listGroupUser(groupId);
        return new PageInfo(groupUsers);
    }


    @Override
    public int deleteGroupUser(GroupUser groupUser) {
        if (!authService.canDeleteGroupUser(groupUser.getGroupId())) {
            return -1;
        }
        return groupUserMapper.deleteByPrimaryKey(groupUser.getId());
    }

    @Override
    public int updateGroupUserRole(GroupUser oldGroupUser, GroupUserUpdateResource resource) {
        if (!authService.canUpdateGroupUserRole(oldGroupUser.getGroupId())) {
            return -1;
        }
        GroupUser newGroupUser = PropertyMapperUtils.map(resource, GroupUser.class);
        newGroupUser.setId(oldGroupUser.getId());
        return groupUserMapper.updateByPrimaryKeySelective(newGroupUser);
    }

    @Override
    public int updateAutoTodoOption(int groupId, AutoTodoResource resource) {
        Integer userId = SecurityUtils.getLoginUser(User.class).getId();
        if (!isInGroup(userId, groupId)) {
            return -1;
        }
        GroupUser groupUser = getGroupUser(userId, groupId);
        GroupUser newGroupUser = new GroupUser();
        newGroupUser.setId(groupUser.getId());
        newGroupUser.setIsAuto(resource.getIsAuto());
        return groupUserMapper.updateByPrimaryKeySelective(newGroupUser);
    }

    @Override
    public AutoTodoResource getAutoTodoOption(int groupId) {
        Integer userId = SecurityUtils.getLoginUser(User.class).getId();
        if (!isInGroup(userId, groupId)) {
            return null;
        }
        GroupUser groupUser = getGroupUser(userId, groupId);
        AutoTodoResource resource = new AutoTodoResource();
        resource.setIsAuto(groupUser.getIsAuto());
        return resource;
    }

}
