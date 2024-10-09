package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.FeedbackStudent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentFeedbackRepository extends JpaRepository<FeedbackStudent, Long> {
}
