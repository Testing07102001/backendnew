package org.kreyzon.stripe.service;

import org.kreyzon.stripe.entity.AdminNotification;
import org.kreyzon.stripe.repository.AdminNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminNotificationService {
    @Autowired
    private AdminNotificationRepository adminNotificationRepository;

    public List<AdminNotification> getAllNotifications() {
        return adminNotificationRepository.findAll();
    }

    public Optional<AdminNotification> getNotificationById(Long id) {
        return adminNotificationRepository.findById(id);
    }

    public AdminNotification markNotificationAsSeen(Long id) {
        Optional<AdminNotification> optionalNotification = adminNotificationRepository.findById(id);
        if (optionalNotification.isPresent()) {
            AdminNotification notification = optionalNotification.get();
            notification.setSeen(true);
            return adminNotificationRepository.save(notification);
        } else {
            throw new RuntimeException("Notification not found with id " + id);
        }
    }

    public void markAllAsSeen() {
        List<AdminNotification> notifications = adminNotificationRepository.findAll();
        for (AdminNotification notification : notifications) {
            if (!notification.isSeen()) {
                notification.setSeen(true);
            }
        }
        adminNotificationRepository.saveAll(notifications);
    }

    public long getUnseenNotificationsCount() {
        return adminNotificationRepository.countBySeenFalse();
    }

    public List<AdminNotification> getUnseenNotifications() {
        return adminNotificationRepository.findBySeenFalse();
    }
}
