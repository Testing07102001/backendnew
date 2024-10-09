package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
        }

