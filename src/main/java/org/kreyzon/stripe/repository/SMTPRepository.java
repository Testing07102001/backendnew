package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.SMTP;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SMTPRepository extends JpaRepository<SMTP, Long> {
}
