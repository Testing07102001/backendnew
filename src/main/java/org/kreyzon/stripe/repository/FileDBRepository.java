package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.FileDB;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDBRepository extends JpaRepository<FileDB, String> {
}
