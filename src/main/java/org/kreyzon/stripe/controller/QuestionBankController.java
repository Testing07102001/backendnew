package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.QuestionBank;
import org.kreyzon.stripe.repository.QuestionBankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class QuestionBankController {
    private final QuestionBankRepository questionBankRepository;

    @Autowired
    public QuestionBankController(QuestionBankRepository questionBankRepository) {
        this.questionBankRepository = questionBankRepository;
    }

    @PostMapping("/questionbank")
    public QuestionBank newQuestionBank(@RequestBody QuestionBank newQuestionBank) {
        return questionBankRepository.save(newQuestionBank);
    }

}
