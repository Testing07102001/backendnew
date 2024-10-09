package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.InvoiceSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceSettingsRepository extends JpaRepository<InvoiceSettings,Long> {
}
