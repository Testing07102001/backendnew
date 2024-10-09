package org.kreyzon.stripe.entity;

import javax.persistence.*;

@Entity
public class SFReceive {
    @Id
    @GeneratedValue

    private Long id;
    private long feedbackId;
    private long studentId;
    private long batchId;
    private String question;
    private int type;
    private String answerQ;
    private String answerT;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(long feedbackId) {
        this.feedbackId = feedbackId;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public long getBatchId() {
        return batchId;
    }

    public void setBatchId(long batchId) {
        this.batchId = batchId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAnswerQ() {
        return answerQ;
    }

    public void setAnswerQ(String answerQ) {
        this.answerQ = answerQ;
    }

    public String getAnswerT() {
        return answerT;
    }

    public void setAnswerT(String answerT) {
        this.answerT = answerT;
    }

}
