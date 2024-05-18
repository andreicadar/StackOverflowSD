package org.example.stackoverflowsd.service;

import org.example.stackoverflowsd.model.*;
import org.example.stackoverflowsd.repository.AnswerRepository;
import org.example.stackoverflowsd.repository.QuestionRepository;
import org.example.stackoverflowsd.repository.UserInterface;
import org.example.stackoverflowsd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private UserInterface userInterface;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> userDetail = userInterface.findByUsername(username);

        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
    }

    public int checkIfUserExists(String username) {
        Optional<User> userDetail = userInterface.findByUsername(username);
        if(userDetail.isPresent()) {
            return 1;
        }
        else {
            return 0;
        }
    }

    public String addUser(User userInfo) {
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        userInterface.save(userInfo);
        return "User Added Successfully";
    }

    public int postQuestion(Question question, MultipartFile image) {
        return questionRepository.postQuestion(question, image);

    }

    public List<Question> getQuestionsOfUser(String username) {
        List<Question> questions = questionRepository.getQuestionsOfUser(username);
        for (Question question : questions) {
            try {
                byte[] fileContent = Files.readAllBytes(Paths.get(question.getPicturePath()));
                String encodedString = Base64.getEncoder().encodeToString(fileContent);
                question.setPictureBase64(encodedString);
            } catch (IOException e) {
                e.printStackTrace();
                // Handle the error appropriately, maybe set a default value or log the error
                question.setPictureBase64(null);
            }
        }
        return questions;
    }

    public int deleteQuestion(String username, Long questionID) {
        return questionRepository.deleteQuestion(username, questionID);
    }

    public int updateQuestion(String author, Long id, String title, String text, String tags, MultipartFile image) throws IOException {
        return questionRepository.updateQuestion(author, id, title, text, tags, image);
    }

    public Object searchQuestions(String title, String text, String author, String tags) {
        List<Question> questions = questionRepository.searchQuestions(title, text, author, tags);
        for (Question question : questions) {
            try {
                byte[] fileContent = Files.readAllBytes(Paths.get(question.getPicturePath()));
                String encodedString = Base64.getEncoder().encodeToString(fileContent);
                question.setPictureBase64(encodedString);
            } catch (IOException e) {
                e.printStackTrace();
                question.setPictureBase64(null);
            }
        }
        return questions;
    }

    public int upvoteQuestion(String username, int questionID) {
        return questionRepository.voteQuestion(username, questionID, 1);
    }

    public int downvoteQuestion(String username, int questionID) {
        return questionRepository.voteQuestion(username, questionID, -1);
    }

    public int answerQuestion(Answer answer, MultipartFile image, Long questionID) throws IOException {
        return answerRepository.answerQuestion(answer, image, questionID);
    }

    public QuestionAnswers getQuestionDetails(Long questionID) {
        return questionRepository.getQuestionDetails(questionID);
    }

    public int deleteAnswer(String username, Long answerID) {
        System.out.println(answerID);
        return answerRepository.deleteAnswer(username, answerID);
    }

    public int upvoteAnswer(String username, int answerID) {
        return answerRepository.voteAnswer(username, answerID, 1);
    }

    public int downvoteAnswer(String username, int answerID) {
        return answerRepository.voteAnswer(username, answerID, -1);
    }

    public int deleteUser(String username) {
        //use already built in crud repository function to delete
        return userRepository.deleteUserByUsername(username);
    }

    public int findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public int updateUser(String username, String newUsername, String password, String email){
        return userRepository.updateUser(username, newUsername, password, email);
    }

    public User getUserByID(String username, int id) {
        return userRepository.getUserByID(username, id);
    }

    public User getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    public Question getQuestionById(Long questionID) {
        return questionRepository.getQuestionById(questionID);
    }

    public Answer getAnswerByID(Long answerID) {
        return answerRepository.getAnswerById(answerID);
    }

    public int updateAnswer(String username, Long answerID, String text, MultipartFile image) {
        return answerRepository.updateAnswer(username, answerID, text, image);
    }

    public int banUser(String username, String userToBan) {
        return userRepository.banUser(username, userToBan);
    }

    public boolean checkIfUserIsBaned(String username) {
        return userRepository.checkIfUserIsBaned(username);
    }

    public int unbanUser(String username, String userToUnban) {
        return userRepository.unbanUser(username, userToUnban);
    }

    public List<Answer> getAnswersOfUser(String username) {
        List<Answer> answers = answerRepository.getAnswersOfUser(username);
        for (Answer answer : answers) {
            try {
                byte[] fileContent = Files.readAllBytes(Paths.get(answer.getPicturePath()));
                String encodedString = Base64.getEncoder().encodeToString(fileContent);
                answer.setPictureBase64(encodedString);
            } catch (IOException e) {
                e.printStackTrace();
                // Handle the error appropriately, maybe set a default value or log the error
                answer.setPictureBase64(null);
            }
        }
        return answers;
    }
}