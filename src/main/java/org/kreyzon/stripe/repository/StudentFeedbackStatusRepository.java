package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.StudentFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentFeedbackStatusRepository  extends JpaRepository<StudentFeedback, Long> {
    List<StudentFeedback> findByBatchId(Long batchId);

    List<StudentFeedback> findByBatchIdAndStudentId(Long batchId, Long studentId);

    StudentFeedback findByIdAndBatchIdAndStudentId(Long id, Long batchId, Long studentId); // Method to find by ID, batch ID, and student ID
}
