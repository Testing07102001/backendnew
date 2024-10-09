package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.GeneralFeedbackCreate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneralFeedbackCreateRepository extends JpaRepository<GeneralFeedbackCreate, Long> {

    List<GeneralFeedbackCreate> findByBatchId(Long batchId);

    long countByCreateStatus(boolean createStatus);

}
