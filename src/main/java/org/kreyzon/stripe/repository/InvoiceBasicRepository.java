package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.InvoiceBasic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceBasicRepository extends JpaRepository<InvoiceBasic,Long> {
}

