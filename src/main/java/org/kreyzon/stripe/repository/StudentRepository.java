package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByStatus(String status);

//    @Transactional
//    @Query("SELECT s FROM Student s WHERE s.status IN :statuses OR s.approve = 'rejected'")
//    List<Student> findByStatusIn(@Param("statuses") List<String> statuses);


    @Transactional
    @Query("SELECT s FROM Student s WHERE s.status IN :statuses ")
    List<Student> findByStatusIn(@Param("statuses") List<String> statuses);


    @Query("SELECT s FROM Student s WHERE s.status = :status1 OR s.status = :status2")
    List<Student> findByStatus(@Param("status1") String status1, @Param("status2") String status2);



    @Modifying
    @Transactional
    @Query("UPDATE Student s SET s.status = 'removed' WHERE s.id = :studentId")
    void updateStudentStatusToRemoved(@Param("studentId") Long studentId);

    @Transactional
    @Query("SELECT s FROM Student s WHERE s.status = 'removed'")
    List<Student> getAllRemovedStudents();


    @Modifying
    @Transactional
    @Query("UPDATE Student s SET s.status = 'active' WHERE s.id = :studentId")
    void updateStatusToActive(Long studentId);

    Optional<Student> findByNric(String nric);

    @Query("SELECT count(s) FROM Student s WHERE s.status = :status")
    Long countByStatus(@Param("status") String status);
    @Query("SELECT s FROM Student s WHERE s.status = 'Pending'")
    List<Student> findStudentsByPendingStatus();

}


