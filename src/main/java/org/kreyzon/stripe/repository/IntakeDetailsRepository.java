package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.IntakeDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IntakeDetailsRepository extends JpaRepository<IntakeDetails, Long> {
    Optional<IntakeDetails> findByIntakeId(String intakeId);
    List<IntakeDetails> findAll();
    Optional<IntakeDetails> findById(Long id);
    List<IntakeDetails> findByBatchId(Long batchId);

    List<IntakeDetails> findByIntakeIdAndBatchId(String intakeId, Long batchId);
}
