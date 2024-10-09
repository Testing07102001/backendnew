package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.ModuleMark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleMarkRepository extends JpaRepository<ModuleMark, Long> {


    List<ModuleMark> findByRegistrationAdvanceId(Long id);
}