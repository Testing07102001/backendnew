package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesRepository extends JpaRepository<Categories, Long> {
}
