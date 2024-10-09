package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.AdminNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdminNotificationRepository extends JpaRepository<AdminNotification, Long> {
    @Query("SELECT COUNT(n) FROM AdminNotification n WHERE n.seen = false")
    long countBySeenFalse();

    List<AdminNotification> findBySeenFalse();
}
