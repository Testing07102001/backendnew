package org.kreyzon.stripe.service;

import org.kreyzon.stripe.dto.ModuleMarksDTO;
import org.kreyzon.stripe.dto.StudentResponseDTO;
import org.kreyzon.stripe.entity.RegistrationAdvance;
import org.kreyzon.stripe.entity.Student;
import org.kreyzon.stripe.repository.ModuleMarkRepository;
import org.kreyzon.stripe.repository.RegistrationAdvanceRepository;
import org.kreyzon.stripe.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public void saveStudent(Student student) {
        studentRepository.save(student);
    }

    @Autowired
    private RegistrationAdvanceRepository registrationAdvanceRepository;
    @Autowired
    private ModuleMarkRepository moduleMarkRepository;


    @Transactional
    public void updateStudentStatusToRemoved(Long studentId) {
        studentRepository.updateStudentStatusToRemoved(studentId);
    }

//    @Transactional
//    public List<Student> getAllRemovedStudents() {
//        List<String> statuses = Arrays.asList("removed", "withdraw","rejected");
//        return studentRepository.findByStatusIn(statuses);
//    }
@Transactional
public List<Student> getAllRemovedStudents() {
    List<String> statuses = Arrays.asList("removed", "withdraw","rejected");
    return studentRepository.findByStatusIn(statuses);
}



    @Transactional
    public void retrieveStudent(Long studentId) {
        // Assuming StudentRepository has a method to update status
        studentRepository.updateStatusToActive(studentId);
    }

    public Long countStudentsByStatus(String status) {
        return studentRepository.countByStatus(status);
    }
    public List<StudentResponseDTO> getStudentsByIntakeId(String intakeId) {
        // Fetch registration advances by intakeId
        List<RegistrationAdvance> registrationAdvances = registrationAdvanceRepository.findByIntakeId(intakeId);

        // Map to DTO
        return registrationAdvances.stream().map(registrationAdvance -> {
            StudentResponseDTO dto = new StudentResponseDTO();
            dto.setStudentName(registrationAdvance.getStudent().getName());
            dto.setId(registrationAdvance.getStudent().getId());
            dto.setNric(registrationAdvance.getStudent().getNric());
            dto.setIntakeId(registrationAdvance.getIntakeId());
            dto.setCourse(registrationAdvance.getBatch().getCourse());

            // Fetch module marks and convert to DTO
            List<ModuleMarksDTO> moduleMarksDTOs = moduleMarkRepository.findByRegistrationAdvanceId(registrationAdvance.getId()).stream().map(moduleMark -> {
                ModuleMarksDTO moduleMarksDTO = new ModuleMarksDTO();
                moduleMarksDTO.setModuleName(moduleMark.getModuleName());
                moduleMarksDTO.setMark1(moduleMark.getMark1());
                moduleMarksDTO.setMark2(moduleMark.getMark2());
                return moduleMarksDTO;
            }).collect(Collectors.toList());

            dto.setModuleMarks(moduleMarksDTOs);

            return dto;
        }).collect(Collectors.toList());
    }

}

