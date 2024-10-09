package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.CurriculumFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurriculumFeedbackRepository extends JpaRepository<CurriculumFeedback,Long> {
    List<CurriculumFeedback> findByCourseId(Long courseId);
    List<CurriculumFeedback> findByCourseIdAndStudentId (long courseId,long studentId);

    CurriculumFeedback findByIdAndCourseIdAndStudentId(Long id, Long courseId, Long studentId);
}
