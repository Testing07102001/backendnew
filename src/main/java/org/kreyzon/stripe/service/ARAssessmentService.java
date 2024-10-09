package org.kreyzon.stripe.service;

import org.kreyzon.stripe.entity.ARAssessment;
import org.kreyzon.stripe.repository.ARAssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class ARAssessmentService {
    private final ARAssessmentRepository assessmentRepository;

    @Autowired
    public ARAssessmentService(ARAssessmentRepository assessmentRepository) {
        this.assessmentRepository = assessmentRepository;
    }

    public ARAssessment saveAssessment(ARAssessment assessment) {
        return assessmentRepository.save(assessment);
    }

    public List<ARAssessment> getAllAssessments() {
        return assessmentRepository.findAll();
    }

    public ARAssessment getAssessmentById(Long id) {
        Optional<ARAssessment> optionalAssessment = assessmentRepository.findById(id);
        return optionalAssessment.orElse(null);
    }

    public ARAssessment updateAssessment(Long id, ARAssessment updatedAssessment) {
        ARAssessment existingAssessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Assessment not found with id: " + id));

        existingAssessment.setcId(updatedAssessment.getcId());
        existingAssessment.setbId(updatedAssessment.getbId());

        existingAssessment.setSkillCode(updatedAssessment.getSkillCode());
        existingAssessment.setSkill(updatedAssessment.getSkill());
        existingAssessment.setsNric(updatedAssessment.getsNric());
        existingAssessment.setAssessors(updatedAssessment.getAssessors());
        existingAssessment.setTrainers(updatedAssessment.getTrainers());
        existingAssessment.setAssessmentDate(updatedAssessment.getAssessmentDate());
        existingAssessment.setPc_nyc(updatedAssessment.getPc_nyc());
        existingAssessment.setFeedback(updatedAssessment.getFeedback());
        existingAssessment.seteSign(updatedAssessment.geteSign());
        existingAssessment.setTemplates(updatedAssessment.getTemplates());
        existingAssessment.setSname(updatedAssessment.getSname());
        existingAssessment.setSid(updatedAssessment.getSid());

        return assessmentRepository.save(existingAssessment);
    }

    public void deleteAssessment(Long id) {
        assessmentRepository.deleteById(id);
    }
    public List<ARAssessment> getAssessmentsBySid(Long sid) {
        return assessmentRepository.findBySid(sid);
    }


}
