package com.anop.service.impl;

import com.anop.mapper.CustomNotificationMapper;
import com.anop.mapper.GroupUserMapper;
import com.anop.mapper.NotificationMapper;
import com.anop.mapper.ReceiverMapper;
import com.anop.pojo.GroupUser;
import com.anop.pojo.Notification;
import com.anop.pojo.Receiver;
import com.anop.pojo.example.GroupUserExample;
import com.anop.pojo.example.ReceiverExample;
import com.anop.pojo.security.User;
import com.anop.resource.*;
import com.anop.service.GroupAuthService;
import com.anop.service.GroupUserService;
import com.anop.service.NotificationService;
import com.anop.service.TodoRemoteService;
import com.anop.util.PageSortHelper;
import com.anop.util.PropertyMapperUtils;
import com.anop.util.SecurityUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 通知业务逻辑默认实现
 *
 * @author Xue_Feng
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class NotificationServiceImpl implements NotificationService {
    private static final Byte READ = 1;
    private static final Byte AUTO_TODO = 1;
    private static final Byte UNREAD = 0;
    private static final Byte NOT_FAVORITE = 0;
    private static final Byte NOT_IMPORTANT = 0;
    private static final Byte COMMON_ROLE = 0;
    private static final int MAX_TODO_TITLE_LENGTH = 15;
    @Autowired
    NotificationMapper notificationMapper;
    @Autowired
    CustomNotificationMapper customNotificationMapper;
    @Autowired
    ReceiverMapper receiverMapper;
    @Autowired
    GroupUserMapper groupUserMapper;
    @Autowired
    GroupUserService groupUserService;
    @Autowired
    GroupAuthService authService;
    @Autowired
    TodoRemoteService todoService;

    @Override
    public boolean isInGroup(int notificationId, int groupId) {
        return getNotification(notificationId, groupId) != null;
    }

    @Override
    public NotificationResource getNotificationInfo(int notificationId, int groupId) {
        if (!groupUserService.isInGroup(SecurityUtils.getLoginUser(User.class).getId(), groupId)) {
            return null;
        }
        return customNotificationMapper.selectNotification(notificationId, groupId);
    }

    @Override
    public ReceiverNotificationResource getReceiverNotification(int notificationId, int groupId) {
        int currentUserId = SecurityUtils.getLoginUser(User.class).getId();
        if (!authService.canGetReceiverNotification(groupId)) {
            return null;
        }
        ReceiverNotificationResource resource = customNotificationMapper.selectReceiverNotification(notificationId, currentUserId, groupId);
        if (resource.getIsRead() == null) {
            resource.setIsRead(UNREAD);
        }
        return resource;
    }

    @Override
    public Notification getNotification(int notificationId, int groupId) {
        Notification notification = notificationMapper.selectByPrimaryKey(notificationId);
        if (notification != null && notification.getGroupId() != groupId) {
            return null;
        }
        return notification;
    }

    @Override
    public PageInfo<List<NotificationResource>> listNotificationInfo(PageParmResource page, int groupId) {
        if (!groupUserService.isInGroup(SecurityUtils.getLoginUser(User.class).getId(), groupId)) {
            return null;
        }
        PageSortHelper.pageAndSort(page, NotificationResource.class);
        List<NotificationResource> notificationResources = customNotificationMapper.listNotification(groupId);
        return new PageInfo(notificationResources);
    }

    @Override
    public PageInfo<List<ReceiverNotificationResource>> listReceiverNotification(PageParmResource page, int groupId) {
        int currentUserId = SecurityUtils.getLoginUser(User.class).getId();
        if (!authService.canGetReceiverNotification(groupId)) {
            return null;
        }
        PageSortHelper.pageAndSort(page, ReceiverNotificationResource.class);
        List<ReceiverNotificationResource> notificationResources = customNotificationMapper.listReceiverNotification(currentUserId, groupId);
        for (ReceiverNotificationResource resource : notificationResources) {
            if (resource.getIsRead() == null) {
                resource.setIsRead(UNREAD);
            }
        }
        return new PageInfo(notificationResources);
    }

    @Override
    public GroupUnreadNotificationCountResource countGroupUnreadNotification(int groupId) {
        int currentUserId = SecurityUtils.getLoginUser(User.class).getId();
        if (!authService.canGetReceiverNotification(groupId)) {
            return null;
        }
        Long unreadCount = customNotificationMapper.countGroupUnreadNotification(currentUserId, groupId);
        GroupUnreadNotificationCountResource resource = new GroupUnreadNotificationCountResource();
        resource.setGroupId(groupId);
        resource.setUnreadCount(unreadCount);
        return resource;
    }

    @Override
    public int deleteNotification(Notification notification) {
        if (!authService.canDeleteNotification(notification.getGroupId())) {
            return -1;
        }
        return notificationMapper.deleteByPrimaryKey(notification.getId());
    }

    @Override
    public int updateNotification(Notification oldNotification, NotificationUpdateResource resource) {
        if (!authService.canUpdateNotification(oldNotification.getGroupId())) {
            return -1;
        }
        ReceiverExample example = new ReceiverExample();
        ReceiverExample.Criteria criteria = example.createCriteria();
        criteria.andNotificationIdEqualTo(oldNotification.getId());
        Receiver receiver = new Receiver();
        receiver.setIsRead(READ);
        receiverMapper.updateByExampleSelective(receiver, example);

        Notification newNotification = PropertyMapperUtils.map(resource, Notification.class);
        newNotification.setId(oldNotification.getId());
        newNotification.setCreationDate(new Date());
        return notificationMapper.updateByPrimaryKeySelective(newNotification);
    }

    @Override
    public int addNotification(NotificationAddResource resource, int groupId) {
        if (!authService.canAddNotification(groupId)) {
            return -1;
        }
        Notification notification = PropertyMapperUtils.map(resource, Notification.class);
        notification.setGroupId(groupId);
        notification.setCreationDate(new Date());
        notification.setUserId(SecurityUtils.getLoginUser(User.class).getId());

        GroupUserExample example = new GroupUserExample();
        example.createCriteria()
            .andGroupIdEqualTo(groupId)
            .andIsAutoEqualTo(AUTO_TODO)
            .andIsAdminEqualTo(COMMON_ROLE);
        List<GroupUser> groupUsers = groupUserMapper.selectByExample(example);
        if (groupUsers.size() > 0) {
            List<Integer> userIds = new ArrayList<>(groupUsers.size());
            for (GroupUser user : groupUsers) {
                userIds.add(user.getUserId());
            }
            TodoBatchAddResource addResource = new TodoBatchAddResource();
            addResource.setUserIds(userIds);
            addResource.setTitle(cutString(resource.getTitle(), MAX_TODO_TITLE_LENGTH));
            addResource.setContent(resource.getContent());
            todoService.addTodosBatch(addResource);
        }
        return notificationMapper.insert(notification);
    }

    @Override
    public int asTodo(Notification notification) {
        if (!authService.canTurnNotificationIntoTodo(notification.getGroupId())) {
            return -1;
        }
        TodoAddResource resource = new TodoAddResource();
        resource.setTitle(cutString(notification.getTitle(), MAX_TODO_TITLE_LENGTH));
        resource.setContent(notification.getContent());
        resource.setIsFavorite(NOT_FAVORITE);
        resource.setIsImportant(NOT_IMPORTANT);
        return todoService.addTodo(resource) != null ? 1 : 0;
    }

    private String cutString(String str, int maxLength) {
        if (str != null) {
            if (str.length() > maxLength) {
                str = str.substring(0, maxLength);
            }
        }
        return str;
    }
}
