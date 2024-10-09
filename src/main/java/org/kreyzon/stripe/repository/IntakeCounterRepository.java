package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.IntakeCounter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IntakeCounterRepository extends JpaRepository<IntakeCounter, Long> {
    Optional<IntakeCounter> findByBatchId(Long batchId);
    List<IntakeCounter> findAll();


}
