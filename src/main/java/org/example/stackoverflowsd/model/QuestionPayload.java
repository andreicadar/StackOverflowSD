package org.example.stackoverflowsd.model;

import org.springframework.web.multipart.MultipartFile;

public class QuestionPayload {
    public Question    question;
    public MultipartFile image;

    public QuestionPayload() {
    }

    public QuestionPayload(Question question, MultipartFile image) {
        this.question = question;
        this.image = image;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }



}
