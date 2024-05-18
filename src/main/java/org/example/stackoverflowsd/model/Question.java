package org.example.stackoverflowsd.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String author;

    @Column(name = "title")
    private String title;

    @Column(name = "text")
    private String text;

    //creation date
    @Column(name = "creationTime")
    LocalDateTime creationTime;

    @Column(name = "picturePath")
    private String picturePath;

    @Column(name = "tags")
    private String tags;

    @Column(name = "score")
    private int score;

    @JsonIgnore
    @Column(name = "userID")
    private int userID;

    private String pictureBase64;


    public Question() {

    }

    public Question(int userID, String title, String text, String tags) {
        this.userID = userID;
        this.title = title;
        this.text = text;
        this.tags = tags;
    }




    public void setId(Long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
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

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTags() {
        return tags;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getUserID() {
        return userID;
    }

    public String getPictureBase64() {
        return pictureBase64;
    }

    public void setPictureBase64(String pictureBase64) {
        this.pictureBase64 = pictureBase64;
    }

    public Question(String author, String title, String text, String tags) {
        this.author = author;
        this.title = title;
        this.text = text;
        this.tags = tags;
    }

    public Question(Long id, String author, String title, String text, LocalDateTime creationTime, String picturePath, String tags, int score) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.text = text;
        this.creationTime = creationTime;
        this.picturePath = picturePath;
        this.tags = tags;
        this.score = score;
    }

    public Question(Long id, int userID, String title, String text, LocalDateTime creationTime, String picturePath, String tags, int score) {
        this.id = id;
        this.userID = userID;
        this.title = title;
        this.text = text;
        this.creationTime = creationTime;
        this.picturePath = picturePath;
        this.tags = tags;
        this.score = score;
    }

}
