package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.GeneralFeedbackStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneralFeedbackStudentRepository extends JpaRepository<GeneralFeedbackStudent, Long> {
    List<GeneralFeedbackStudent> findByBatchId(Long batchId);
    List<GeneralFeedbackStudent> findByStudentId(Long studentId);

    List<GeneralFeedbackStudent> findByFeedbackId(Long feedbackId); // New method


}

