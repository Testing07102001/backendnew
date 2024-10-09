package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {

    Optional<TimeTable> findByIntakeIdAndBatchId(String intakeId, Long batchId);

}
