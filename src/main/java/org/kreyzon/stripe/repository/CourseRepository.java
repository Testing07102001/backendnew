package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT COUNT(c) FROM Course c WHERE c.type = :type")
    Long countByType(@Param("type") String type);


    @Query("SELECT c.type, COUNT(c) FROM Course c GROUP BY c.type")
    List<Object[]> countCoursesByType();
}