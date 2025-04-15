package com.quiz.Backend.dto;

import java.util.List;


public class QuestionDTO {
    private Long id;
    private String questionText;
    private List<String> answerOptions;

    public QuestionDTO() {}

    public QuestionDTO(Long id, String questionText, List<String> answerOptions) {
        this.id = id;
        this.questionText = questionText;
        this.answerOptions = answerOptions;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public List<String> getAnswerOptions() { return answerOptions; }
    public void setAnswerOptions(List<String> answerOptions) { this.answerOptions = answerOptions; }
}
