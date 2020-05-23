package com.anop.service.impl;

import com.anop.mapper.CustomReceiverMapper;
import com.anop.mapper.ReceiverMapper;
import com.anop.pojo.Receiver;
import com.anop.pojo.example.ReceiverExample;
import com.anop.resource.ReceiverAddResource;
import com.anop.resource.ReceiverResource;
import com.anop.pojo.security.User;
import com.anop.service.GroupAuthService;
import com.anop.service.GroupUserService;
import com.anop.service.NotificationService;
import com.anop.service.ReceiverService;
import com.anop.util.PropertyMapperUtils;
import com.anop.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 通知接收者业务逻辑默认实现
 *
 * @author Xue_Feng
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class ReceiverServiceImpl implements ReceiverService {
    private static final byte READ = 1;
    private static final byte UNREAD = 0;
    @Autowired
    ReceiverMapper receiverMapper;
    @Autowired
    CustomReceiverMapper customReceiverMapper;
    @Autowired
    GroupAuthService authService;
    @Autowired
    GroupUserService groupUserService;
    @Autowired
    NotificationService notificationService;

    private Receiver selectReceiver(int notificationId, int userId) {
        ReceiverExample example = new ReceiverExample();
        ReceiverExample.Criteria criteria = example.createCriteria();
        criteria.andNotificationIdEqualTo(notificationId);
        criteria.andUserIdEqualTo(userId);
        List<Receiver> receivers = receiverMapper.selectByExample(example);
        if (receivers.size() > 0) {
            return receivers.get(0);
        }
        return null;
    }

    @Override
    public int addReceiver(ReceiverAddResource resource) {
        int currentUserId = SecurityUtils.getLoginUser(User.class).getId();
        if (!notificationService.isInGroup(resource.getNotificationId(), resource.getGroupId())) {
            return -1;
        }
        if (!authService.canMarkNotification(resource.getGroupId())) {
            return -1;
        }
        boolean isNew = false;
        Receiver receiver = selectReceiver(resource.getNotificationId(), currentUserId);
        if (receiver == null) {
            isNew = true;
            receiver = PropertyMapperUtils.map(resource, Receiver.class);
            receiver.setUserId(currentUserId);
        } else if (receiver.getIsRead() == READ) {
            return 0;
        }
        receiver.setIsRead(READ);
        if (isNew) {
            return receiverMapper.insert(receiver);
        }
        return receiverMapper.updateByPrimaryKey(receiver);
    }

    @Override
    public List<ReceiverResource> listReceiver(int notificationId, int groupId) {
        if (!notificationService.isInGroup(notificationId, groupId)) {
            return null;
        }
        if (!authService.canListReceiver(groupId)) {
            return null;
        }
        List<ReceiverResource> resources = customReceiverMapper.listReceiver(notificationId, groupId);
        for (ReceiverResource resource : resources) {
            if (resource.getIsRead() == null) {
                resource.setIsRead(UNREAD);
            }
        }
        return resources;
    }
}
