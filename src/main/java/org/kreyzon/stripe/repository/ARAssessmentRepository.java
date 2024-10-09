package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.ARAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ARAssessmentRepository extends JpaRepository<ARAssessment, Long> {
    List<ARAssessment> findBySid(Long sid);


}