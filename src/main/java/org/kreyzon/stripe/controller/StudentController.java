package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.dto.StudentResponseDTO;
import org.kreyzon.stripe.entity.Student;
import org.kreyzon.stripe.entity.User;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.StudentRepository;
import org.kreyzon.stripe.service.StudentService;
import org.kreyzon.stripe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class StudentController {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private StudentService studentService;

    public StudentController() {
    }

    @PostMapping("/student")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        // Set status to "active"
        student.setStatus("active");

        // Save the student first to get the ID
        Student savedStudent = studentRepository.save(student);

        // Create a corresponding User
        User user = new User();
        user.setName(savedStudent.getName());
        user.setEmail(savedStudent.getEmail());
        // Ensure that the password is hashed before storing
        user.setPassword(savedStudent.getPassword());
        user.setUsertype(1);
        user.setStatus("active");
        // Set the sfid to the saved student's ID
        user.setSfid(savedStudent.getId());

        // Save the user
        userService.createUser(user);

        return ResponseEntity.ok(savedStudent);
    }

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/student/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentRepository.findById(id).orElseThrow(() -> new VenueNotFoundException(id));
    }

    @PutMapping("/student/{id}")
    public Student updateUser(@RequestBody Student newStudent, @PathVariable Long id) {
        return studentRepository.findById(id).map(student -> {
            student.setFullname(newStudent.getFullname());
            student.setNric(newStudent.getNric());
            student.setEmail(newStudent.getEmail());
            student.setPhone(newStudent.getPhone());
            student.setGender(newStudent.getGender());
            student.setDob(newStudent.getDob());
            student.setPassword(newStudent.getPassword());
//            student.setAddress(newStudent.getAddress());
//            student.setPostalcode(newStudent.getPostalcode());
//            student.setRace(newStudent.getRace());
//            student.setEduction(newStudent.getEduction());
//            student.setEmploymentstatus(newStudent.getEmploymentstatus());
//            student.setSalary(newStudent.getSalary());
//            student.setCompanyname(newStudent.getCompanyname());
//            student.setCorporatecompanysponsored(newStudent.getCorporatecompanysponsored());
            student.setDesignation(newStudent.getDesignation());
            student.setProfileUrl(newStudent.getProfileUrl());
//            student.setMode(newStudent.getMode());
//            student.setStudentId(newStudent.getStudentId());
            return studentRepository.save(student);
        }).orElseThrow(() -> new VenueNotFoundException(id));
    }

    @DeleteMapping("/student/{id}")
    public String deleteStudent(@PathVariable Long id) {
        if (!studentRepository.existsById(id)) {
            throw new VenueNotFoundException(id);
        } else {
            studentRepository.deleteById(id);
            return "Student with id " + id + " has been deleted successfully.";
        }
    }

    @GetMapping("/student/count")
    public Long getStudentCount() {
        return studentRepository.count();
    }

    @PutMapping("/student/{id}/activate")
    public ResponseEntity<Student> activateStudent(@PathVariable Long id) {
        // Find the student by ID
        Optional<Student> optionalStudent = studentRepository.findById(id);

        // Check if the student exists
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();

            // Update the status to "active"
            student.setStatus("active");

            // Save the updated student
            Student updatedStudent = studentRepository.save(student);

            return ResponseEntity.ok(updatedStudent);
        } else {
            throw new VenueNotFoundException(id);
        }
    }

    @PutMapping("/student/{id}/register")
    public ResponseEntity<Student> registerStudent(@PathVariable Long id) {
        // Find the student by ID
        Optional<Student> optionalStudent = studentRepository.findById(id);

        // Check if the student exists
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();

            // Update the status to "active"
            student.setStatus("register");

            // Save the updated student
            Student updatedStudent = studentRepository.save(student);

            return ResponseEntity.ok(updatedStudent);
        } else {
            throw new VenueNotFoundException(id);
        }
    }
    // New API endpoint to update remarks and status
    @PutMapping("/student/{id}/updateRemarksAndStatus")
    public ResponseEntity<Student> updateRemarksAndStatus(@PathVariable Long id, @RequestBody UpdateRemarksAndStatusRequest request) {
        // Find the student by ID
        Optional<Student> optionalStudent = studentRepository.findById(id);

        // Check if the student exists
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();

            // Update the status and remarks
            student.setStatus(request.getStatus());
//            student.setRemarks(request.getRemarks());

            // Save the updated student
            Student updatedStudent = studentRepository.save(student);

            return ResponseEntity.ok(updatedStudent);
        } else {
            throw new VenueNotFoundException(id);
        }
    }
    @GetMapping("/students/active")
    public ResponseEntity<List<Map<String, Object>>> getActiveStudents() {
        List<Student> activeStudents = studentRepository.findByStatus("active");

        List<Map<String, Object>> result = activeStudents.stream().map(student -> {
            Map<String, Object> studentMap = new HashMap<>();
            studentMap.put("id", student.getId());
            studentMap.put("name", student.getName());
            studentMap.put("fullname", student.getFullname());
            studentMap.put("nric", student.getNric());
//            studentMap.put("studentId", student.getStudentId());
            studentMap.put("email", student.getEmail());
            studentMap.put("phone", student.getPhone());
            studentMap.put("gender", student.getGender());
            studentMap.put("dob", student.getDob());
            studentMap.put("password", student.getPassword());
            studentMap.put("designation", student.getDesignation());
            studentMap.put("status", student.getStatus());
//            studentMap.put("mode", student.getMode());
            studentMap.put("type", student.getType());
            studentMap.put("profileUrl", student.getProfileUrl());
//

            return studentMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
    @GetMapping("/students/deferred")
    public ResponseEntity<List<Student>> getDeferredStudents() {
        List<Student> deferredStudents = studentRepository.findByStatus("deferred");
        return ResponseEntity.ok(deferredStudents);
    }


    @GetMapping("/students/status")
    public ResponseEntity<List<Student>> getStudentsByStatus() {
        List<Student> students = studentRepository.findByStatus("new", "withdraw");
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{studentId}/remove")
    public ResponseEntity<Void> removeStudentStatusByStudentId(@PathVariable Long studentId) {
        studentService.updateStudentStatusToRemoved(studentId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/getAllRemovedStudents")
    public ResponseEntity<List<Student>> getAllStudentsRemovedPreviously() {
        List<Student> removedStudents = studentService.getAllRemovedStudents();
        return ResponseEntity.ok(removedStudents);
    }
    @PutMapping("/students/{studentId}/retrieve")
    public ResponseEntity<Void> retrieveStudent(@PathVariable Long studentId) {
        studentService.retrieveStudent(studentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/student/count/active")
    public ResponseEntity<Long> getActiveStudentCount() {
        Long activeStudentCount = studentRepository.countByStatus("active");
        return ResponseEntity.ok(activeStudentCount);
    }
    @GetMapping("/students/pending")
    public ResponseEntity<List<Student>> getStudentsByPendingStatus() {
        List<Student> pendingStudents = studentRepository.findStudentsByPendingStatus();
        return ResponseEntity.ok(pendingStudents);
    }
    @GetMapping("/intake/{intakeId}")
    public ResponseEntity<List<StudentResponseDTO>> getStudentsByIntakeId(@PathVariable String intakeId) {
        List<StudentResponseDTO> students = studentService.getStudentsByIntakeId(intakeId);

        if (students.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if no students found
        }

        return ResponseEntity.ok(students); // Return 200 OK with the list of students
    }

}


// DTO for updating remarks and status
class UpdateRemarksAndStatusRequest {
    private String status;
    private String remarks;

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }



}
