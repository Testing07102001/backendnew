package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.ARWritten;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ARWrittenRepository extends JpaRepository<ARWritten, Long> {
    ARWritten findByStudentIdAndBatchIdAndCourseIdAndTemplateId(
            Long studentId, Long batchId, Long courseId, Long templateId);
}