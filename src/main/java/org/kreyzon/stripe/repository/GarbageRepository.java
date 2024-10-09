package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.Garbage;
import org.kreyzon.stripe.entity.RegistrationAdvance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GarbageRepository extends JpaRepository<Garbage,Long> {

    List<Garbage> findBynric(String nric);

    String findStatusByNric(String nric);

    List<RegistrationAdvance> findAllDataByNric(String nric);

    List<Garbage> findByIntakeId(String intakeId);
}