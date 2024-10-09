package org.kreyzon.stripe.repository.wsq;

import org.kreyzon.stripe.entity.wsq.WSQCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WSQCourseRepository extends JpaRepository<WSQCourse, Long> {
    // Method to count how many courses are associated with a specific category
    int countByCategoryId(Long categoryId);
}
