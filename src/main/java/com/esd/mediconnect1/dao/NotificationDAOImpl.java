package com.esd.mediconnect1.dao;


import com.esd.mediconnect1.model.Notification;
import com.esd.mediconnect1.model.NotificationStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class NotificationDAOImpl implements NotificationDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void saveNotification(Notification notification) {
        if (notification.getId() == null) {
            entityManager.persist(notification);
        } else {
            entityManager.merge(notification);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUserId(Long userId) {
        try {
            TypedQuery<Notification> query = entityManager.createQuery(
                "SELECT n FROM Notification n " +
                "WHERE n.user.id = :userId " +
                "ORDER BY n.createdAt DESC",
                Notification.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (NoResultException e) {
            return List.of();
        }
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = entityManager.find(Notification.class, notificationId);
        if (notification != null) {
            notification.setStatus(NotificationStatus.READ);
            entityManager.merge(notification);
        }
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(Long userId) {
        try {
            TypedQuery<Notification> query = entityManager.createQuery(
                "SELECT n FROM Notification n " +
                "WHERE n.user.id = :userId " +
                "AND n.status = :status " +
                "ORDER BY n.createdAt DESC",
                Notification.class);
            query.setParameter("userId", userId);
            query.setParameter("status", NotificationStatus.UNREAD);
            return query.getResultList();
        } catch (NoResultException e) {
            return List.of();
        }
    }

    public void markAllAsRead(Long userId) {
        entityManager.createQuery(
            "UPDATE Notification n " +
            "SET n.status = :status " +
            "WHERE n.user.id = :userId " +
            "AND n.status = :unreadStatus")
            .setParameter("status", NotificationStatus.READ)
            .setParameter("userId", userId)
            .setParameter("unreadStatus", NotificationStatus.UNREAD)
            .executeUpdate();
    }

    public void deleteNotification(Long notificationId) {
        Notification notification = entityManager.find(Notification.class, notificationId);
        if (notification != null) {
            entityManager.remove(notification);
        }
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        try {
            return entityManager.createQuery(
                "SELECT COUNT(n) FROM Notification n " +
                "WHERE n.user.id = :userId " +
                "AND n.status = :status", Long.class)
                .setParameter("userId", userId)
                .setParameter("status", NotificationStatus.UNREAD)
                .getSingleResult();
        } catch (NoResultException e) {
            return 0L;
        }
    }
    public void deleteByUserId(Long userId) {
        entityManager.createQuery("DELETE FROM Notification WHERE user.id = :userId")
                     .setParameter("userId", userId)
                     .executeUpdate();
    }
}
