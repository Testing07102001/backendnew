package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.Batch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface BatchRepository extends JpaRepository<Batch, Long> {
    @Override
    List<Batch> findAll();


//    default List<Batch> findByTrainersContainingAndAssessorsContaining(String trainerId, String assessorId) {
//        return findAll().stream()
//                .filter(batch -> trainerId.map(id -> batch.getTrainers().stream().anyMatch(s -> parseId(s).equals(id))).orElse(true))
//                .filter(batch -> assessorId.map(id -> batch.getAssessors().stream().anyMatch(s -> parseId(s).equals(id))).orElse(true))
//                .collect(Collectors.toList());
//    }

//    List<Batch> findByTrainersContainingOrAssessorsContaining(String trainerId, String assessorId);

    Page<Batch> findByBatchcodeContainingIgnoreCase(String batchcode, Pageable pageable);

    default Long parseId(String nameIdString) {
        return Long.parseLong(nameIdString.substring(nameIdString.indexOf("(") + 1, nameIdString.indexOf(")")));
    }
//
//    List<Batch> findByCourseId(Long courseId);

    Page<Batch> findByType(String type, Pageable pageable);

    Page<Batch> findByBatchcodeContainingIgnoreCaseAndType(String batchcode, String type, Pageable pageable);
    List<Batch> findByCategoryId(Long categoryId);

}

