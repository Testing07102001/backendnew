package org.kreyzon.stripe.repository.wsq;

import org.kreyzon.stripe.entity.wsq.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Page<Category> findByNameContaining(String name, Pageable pageable);

}
