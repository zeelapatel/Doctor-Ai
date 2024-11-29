package com.esd.mediconnect1.dao;

import com.esd.mediconnect1.model.Notification;
import java.util.List;

public interface NotificationDAO {
    void saveNotification(Notification notification);
    List<Notification> getNotificationsByUserId(Long userId);
    void markAsRead(Long notificationId);
    List<Notification> getUnreadNotifications(Long userId);
    void markAllAsRead(Long userId);
    void deleteNotification(Long notificationId);
    long getUnreadCount(Long userId);
	void deleteByUserId(Long id);
}
