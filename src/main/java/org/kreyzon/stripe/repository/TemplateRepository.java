package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemplateRepository extends JpaRepository<Template, Long> {

    List<Template> findByCourseId(String courseId);

}
