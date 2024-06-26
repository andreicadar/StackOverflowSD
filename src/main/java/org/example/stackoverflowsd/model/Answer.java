package org.example.stackoverflowsd.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "answer")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @Column(name = "userID")
    private int userID;

    @Column(name = "text")
    private String text;

    @Column(name = "score")
    private int score;

    @Column(name = "creationTime")
    LocalDateTime creationTime;

    @Column(name = "picturePath")
    private String picturePath;

    @Column(name = "questionID")
    private int questionID;

    private String author;

    private String pictureBase64;

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getUserID() {
        return userID;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public int getQuestionID() {
        return questionID;
    }

    public String getPictureBase64() {
        return pictureBase64;
    }

    public void setPictureBase64(String pictureBase64) {
        this.pictureBase64 = pictureBase64;
    }

    public Answer() {

    }

    public Answer(String author, String text) {
        this.author = author;
        this.text = text;
    }

    public Answer(long id, int userID, String text, LocalDateTime creationTime, String picturePath, int score) {
        this.id = id;
        this.userID = userID;
        this.text = text;
        this.creationTime = creationTime;
        this.picturePath = picturePath;
        this.score = score;
    }

    public Answer(long id, int userID, String text, LocalDateTime creationTime, String picturePath, int score, int questionID) {
        this.id = id;
        this.userID = userID;
        this.text = text;
        this.creationTime = creationTime;
        this.picturePath = picturePath;
        this.score = score;
        this.questionID = questionID;
    }



}
