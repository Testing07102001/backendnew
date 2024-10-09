package org.kreyzon.stripe.service;

import org.kreyzon.stripe.entity.ARWritten;
import org.kreyzon.stripe.repository.ARWrittenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ARWrittenService {
    private final ARWrittenRepository aRWrittenRepository;

    @Autowired
    public ARWrittenService(ARWrittenRepository aRWrittenRepository){
        this.aRWrittenRepository = aRWrittenRepository;
    }

    public ARWritten saveARWritten(ARWritten aRWritten){
        return aRWrittenRepository.save(aRWritten);
    }
    public List<ARWritten> getAllARWritten() {
        return aRWrittenRepository.findAll();
    }

    public ARWritten getARWrittenById(Long id) {
        Optional<ARWritten> optionalARWritten = aRWrittenRepository.findById(id);
        return optionalARWritten.orElse(null); // Or throw an exception if not found
    }


    public ARWritten updateARWritten(Long id, ARWritten updatedARWritten) {
        // Check if ARWritten with given id exists
        Optional<ARWritten> optionalARWritten = aRWrittenRepository.findById(id);
        if (optionalARWritten.isPresent()) {
            ARWritten existingARWritten = optionalARWritten.get();
            existingARWritten.setBatchId(updatedARWritten.getBatchId());
            existingARWritten.setCourseId(updatedARWritten.getCourseId());
            existingARWritten.setTemplateId(updatedARWritten.getTemplateId());
            existingARWritten.setStudentId(updatedARWritten.getStudentId());

            existingARWritten.setUrl(updatedARWritten.getUrl());
            return aRWrittenRepository.save(existingARWritten);
        } else {
            return null; // Or throw an exception if not found
        }
    }

    public void deleteARWritten(Long id) {
        aRWrittenRepository.deleteById(id);
    }




    public String findUrlByParameters(Long studentId, Long batchId, Long courseId, Long templateId) {
        ARWritten arWritten = aRWrittenRepository.findByStudentIdAndBatchIdAndCourseIdAndTemplateId(
                studentId, batchId, courseId, templateId);
        return arWritten != null ? arWritten.getUrl() : null;
    }

}
