package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.GeneralSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneralSettingsRepository extends JpaRepository<GeneralSettings, Long> {
        }