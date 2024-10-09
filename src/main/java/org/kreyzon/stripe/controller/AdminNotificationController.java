package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.AdminNotification;
import org.kreyzon.stripe.service.AdminNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin-notifications")
public class AdminNotificationController {
    @Autowired
    private AdminNotificationService adminNotificationService;

    @GetMapping
    public ResponseEntity<List<AdminNotification>> getAllNotifications() {
        List<AdminNotification> notifications = adminNotificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminNotification> getNotificationById(@PathVariable Long id) {
        return adminNotificationService.getNotificationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/seen")
    public ResponseEntity<AdminNotification> markNotificationAsSeen(@PathVariable Long id) {
        AdminNotification updatedNotification = adminNotificationService.markNotificationAsSeen(id);
        return ResponseEntity.ok(updatedNotification);
    }

    @PostMapping("/mark-all-seen")
    public ResponseEntity<Void> markAllNotificationsAsSeen() {
        adminNotificationService.markAllAsSeen();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unseen-count")
    public long getUnseenNotificationsCount() {
        return adminNotificationService.getUnseenNotificationsCount();
    }

    @GetMapping("/unseen")
    public ResponseEntity<List<AdminNotification>> getUnseenNotifications() {
        List<AdminNotification> unseenNotifications = adminNotificationService.getUnseenNotifications();
        return ResponseEntity.ok(unseenNotifications);
    }
}
