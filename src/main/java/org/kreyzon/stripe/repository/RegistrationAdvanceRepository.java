package org.kreyzon.stripe.repository;

import org.kreyzon.stripe.dto.ScheduleDateWithStudentInfoDTO;
import org.kreyzon.stripe.dto.StudentDTO;
import org.kreyzon.stripe.dto.StudentInfoDTO;
import org.kreyzon.stripe.entity.Batch;
import org.kreyzon.stripe.entity.RegistrationAdvance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationAdvanceRepository extends JpaRepository<RegistrationAdvance, Long> {

    List<RegistrationAdvance> findByApprove(String approve);

//    RegistrationAdvance findBySessionId(String sessionId);

    @Query("SELECT ra FROM RegistrationAdvance ra WHERE ra.endModulus IS NULL")
    List<RegistrationAdvance> findOngoingEnrollments();

    @Query("SELECT COUNT(r) FROM RegistrationAdvance r WHERE r.batch.id = :batchId AND r.status = 'active'")
    long countActiveStudentsByBatchId(@Param("batchId") Long batchId);

    List<RegistrationAdvance> findByStudentId(Long studentId);
  ;

    @Query("SELECT new org.kreyzon.stripe.dto.ScheduleDateWithStudentInfoDTO(ra.id, sd.startModulus, sd.startDate, sd.endDate, s.name, s.email, s.phone) " +
            "FROM RegistrationAdvance ra " +
            "JOIN ra.scheduleDate sd " +
            "JOIN ra.student s " +
            "WHERE ra.batch.id = :batchId")
    List<ScheduleDateWithStudentInfoDTO> findScheduleDatesWithStudentInfoByBatchId(@Param("batchId") Long batchId);


    List<RegistrationAdvance> findByPaymentType(String paymentType);

    List<RegistrationAdvance> findByPaymentTypeAndPaymentStatus(String paymentType, String paymentStatus);

    Optional<RegistrationAdvance> findRegistrationAdvanceById(Long registrationId);


    List<RegistrationAdvance> findByStatus(String status);

    List<RegistrationAdvance> findByApproveAndPaymentTypeAndPaymentStatus(String approve, String paymentType, String paymentStatus);

    List<RegistrationAdvance> findByBatch(Batch batch);

    List<RegistrationAdvance> findByBatchId(Long batchId);

    List<RegistrationAdvance> findByStudent_NricAndStudent_Status(String nric, String status);


    List<RegistrationAdvance> findByNric(String nric);




//    RegistrationAdvance findBySessionId(String sessionId);


//    @Modifying
//    @Transactional
//    @Query("UPDATE RegistrationAdvance r SET r.status = 'removed' WHERE r.student.id = :studentId")
//    void updateDeleteStatusByStudentId(@Param("studentId") Long studentId);

//    @Modifying
//    @Transactional
//    @Query("UPDATE RegistrationAdvance r SET r.status = 'removed' WHERE r.student.id = :studentId")
//    void updateDeleteStatusByStudentId(@Param("studentId") Long studentId);
//
//    @Query("SELECT r FROM RegistrationAdvance r WHERE r.student.id = :studentId AND r.status = 'removed'")
//    List<RegistrationAdvance> findRemovedByStudentId(@Param("studentId") Long studentId);
//
//    @Modifying
//    @Transactional
//    @Query("UPDATE RegistrationAdvance r SET r.status = 'active' WHERE r.student.id = :studentId AND r.status = 'removed'")
//    void updateDeleteStatusToActiveByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT ra FROM RegistrationAdvance ra JOIN FETCH ra.courseFeeDetails WHERE ra.email = :email")
    List<RegistrationAdvance> findByEmail(@Param("email") String email);

    @Query("SELECT ra.email FROM RegistrationAdvance ra")
    List<String> getAllStudentEmails();

    @Query("SELECT ra FROM RegistrationAdvance ra WHERE ra.batch.id = :batchId AND ra.student.status = 'active'")
    List<RegistrationAdvance> findActiveByBatchId(@Param("batchId") Long batchId);

    List<RegistrationAdvance> findByNricAndStatus(String nric, String alumni);

    @Query("SELECT new org.kreyzon.stripe.dto.StudentInfoDTO(r.student.name, r.nric ,r.status) " +
            "FROM RegistrationAdvance r WHERE r.intakeId = :intakeId")
    List<StudentInfoDTO> findStudentsByIntakeId(@Param("intakeId") String intakeId);


    List<RegistrationAdvance> findByIntakeIdAndStatus(String intakeId, String status);

    List<RegistrationAdvance> findByIntakeId(String intakeId);

    @Query("SELECT r FROM RegistrationAdvance r JOIN r.courseFeeDetails c WHERE r.status = 'Alumni' AND c.courseFeeRemaining = 0 AND c.registrationFeeRemaining = 0")
    List<RegistrationAdvance> findByStatusAndFeesRemaining(String status);

    List<RegistrationAdvance> findByBatchIdAndStatus(Long batchId, String status);


    @Query("SELECT new org.kreyzon.stripe.dto.StudentDTO(s.id, s.name, s.nric) " +
            "FROM RegistrationAdvance ra " +
            "JOIN ra.student s " +
            "WHERE ra.intakeId = :intakeId")
    List<StudentDTO> findStudentsByBatchIdAndIntake(@Param("intakeId") String intakeId);

    @Query("SELECT r FROM RegistrationAdvance r WHERE r.status = 'new' OR r.status = 'register'")
    List<RegistrationAdvance> findByStatusNewOrRegister();

    @Query("SELECT ra FROM RegistrationAdvance ra " +
            "LEFT JOIN Garbage g ON ra.id = g.registrationAdvanceId " +
            "WHERE YEAR(ra.enrolledDate) BETWEEN :startYear AND :endYear " +
            "AND ra.status IN ('active', 'Alumni') " +
            "AND g IS NULL")
    List<RegistrationAdvance> findByEnrollmentYearBetweenAndStatusAndNotInGarbage(
            @Param("startYear") int startYear,
            @Param("endYear") int endYear
    );
  @Query("SELECT r FROM RegistrationAdvance r WHERE r.status = 'new' AND (r.approveReference = :approveReference OR r.approveReference IS NULL OR r.approveReference = '')")
  List<RegistrationAdvance> findByStatusAndApproveReferenceOrApproveReferenceIsEmpty(@Param("approveReference") String approveReference);
  @Query("SELECT r FROM RegistrationAdvance r WHERE (r.status = 'new' OR r.status = 'Register') AND (r.approveReference = :approveReference OR r.approveReference IS NULL OR r.approveReference = '')")
  List<RegistrationAdvance> findByStatusNewOrRegisterAndApproveReferenceOrApproveReferenceIsEmpty(@Param("approveReference") String approveReference);

  @Query("SELECT r FROM RegistrationAdvance r WHERE r.status = 'new' AND (r.approveReference IS NULL OR r.approveReference = '')")
    List<RegistrationAdvance> findByStatusWithNullOrEmptyApproveReference(@Param("status") String status);


    @Query("SELECT COUNT(r) FROM RegistrationAdvance r WHERE r.batch.id = :batchId AND r.status = 'active'")
    long countActiveByBatchId(@Param("batchId") Long batchId);

    @Query("SELECT r FROM RegistrationAdvance r WHERE r.batch.id = :batchId AND r.status = 'new'")
    List<RegistrationAdvance> findAllActiveByBatchId(@Param("batchId") Long batchId);


  @Query("SELECT COUNT(r) FROM RegistrationAdvance r WHERE r.status = 'new'")
  long countByStatusNew();
}