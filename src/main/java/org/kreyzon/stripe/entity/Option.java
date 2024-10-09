package org.kreyzon.stripe.entity;

import javax.persistence.*;

@Entity
@Table(name = "options")
public class Option {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_name")
    private String option;

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public boolean isAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

    private boolean answer;

    @ManyToOne
    @JoinColumn(name = "question_bank_id")
    private QuestionBank questionBank;
}
