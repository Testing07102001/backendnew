package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.ARTemplate;
import org.kreyzon.stripe.repository.ARTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "https://impulz-lms.com")
@RestController
public class ARTemplateController {
    private final ARTemplateRepository arTemplateRepository;

    @Autowired
    public ARTemplateController(ARTemplateRepository arTemplateRepository) {
        this.arTemplateRepository = arTemplateRepository;
    }

    @PostMapping("/artemplate")
    public ARTemplate newARTemplate(@RequestBody ARTemplate newARTemplate) {
        return arTemplateRepository.save(newARTemplate);
    }

    @GetMapping("/artemplates")
    public List<ARTemplate> getallARTemplate() {
        return arTemplateRepository.findAll();
    }


}
