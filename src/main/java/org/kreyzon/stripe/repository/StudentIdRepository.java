package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.StudentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentIdRepository extends JpaRepository<StudentId, Long> {

    Optional<StudentId> findByBatchIdAndBatchIntakeId(Long batchId, String batchIntakeId);
}
