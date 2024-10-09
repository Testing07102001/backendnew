package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog,Long> {
}

