package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.Template;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class TemplateController {
    private final TemplateRepository templateRepository;

    @Autowired
    public TemplateController(TemplateRepository templateRepository){
        this.templateRepository=templateRepository;
    }

    @PostMapping("/template")
    public Template newTemplate(@RequestBody Template newTemplate){
        return templateRepository.save(newTemplate);
    }
    @GetMapping("/templates")
    public List<Template> getAllTemlate(){
        return templateRepository.findAll();
    }
    @GetMapping("/template/{id}")
    public Template getTemplate(@PathVariable Long id){
        return templateRepository.findById(id).orElseThrow(()->new VenueNotFoundException(id));
    }
    @PutMapping("/template/{id}")
    public Template updateTemplate(@RequestBody Template newTemplate, @PathVariable Long id){
        return templateRepository.findById(id).map((template)->{
            template.setCourseId(newTemplate.getCourseId());
            template.setCriteria(newTemplate.getCriteria());
            template.setType(newTemplate.getType());
            template.setQuestLabels(newTemplate.getQuestLabels());
            template.setLearningOutcome(newTemplate.getLearningOutcome());
            return templateRepository.save(template);
        }).orElseThrow(()->new VenueNotFoundException(id));
    }

    @DeleteMapping("/template/{id}")
    public String deleteTemplate(@PathVariable Long id){
        if(!templateRepository.existsById(id)){
            throw new VenueNotFoundException(id);
        }else {
            templateRepository.deleteById(id);
            return "Template with id"+ id+ "has been deleted successfully.";
        }
    }

    @GetMapping("/templates/course/{courseId}")
    public List<Template> getTemplatesByCourseIdAndType(@PathVariable String courseId, @RequestParam(required = false) String type) {
        List<Template> templatesByCourseId = templateRepository.findByCourseId(courseId);

        if (type != null) {
            // Filter templates by type
            return templatesByCourseId.stream()
                    .filter(template -> template.getType().equals(type))
                    .collect(Collectors.toList());
        } else {
            // If type is not provided, return all templates for the courseId
            return templatesByCourseId;
        }
    }



}
