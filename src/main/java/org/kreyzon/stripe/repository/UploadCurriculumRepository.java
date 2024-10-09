package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.UploadCurriculum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadCurriculumRepository extends JpaRepository<UploadCurriculum, Long> {

    long countBySeen(boolean seen);
}

