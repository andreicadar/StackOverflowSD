package org.example.stackoverflowsd.model;

import java.util.ArrayList;
import java.util.List;

public class QuestionAnswers {
    public Question question;
    public List<Answer> answers;

    public QuestionAnswers(Question question, List<Answer> answers) {
        this.question = question;
        this.answers = answers;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }
}
