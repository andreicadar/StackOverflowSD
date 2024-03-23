package org.example.stackoverflowsd.service;

import org.example.stackoverflowsd.model.Question;
import org.example.stackoverflowsd.model.User;
import org.example.stackoverflowsd.model.UserInfoDetails;
import org.example.stackoverflowsd.repository.QuestionRepository;
import org.example.stackoverflowsd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> userDetail = userRepository.findByUsername(username);

        // Converting userDetail to UserDetails
        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
    }

    public int checkIfUserExists(String username) {
        Optional<User> userDetail = userRepository.findByUsername(username);
        if(userDetail.isPresent()) {
            return 1;
        }
        else {
            return 0;
        }
    }

    public String addUser(User userInfo) {
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        userRepository.save(userInfo);
        return "User Added Successfully";
    }

    public int postQuestion(Question question, MultipartFile image) {
        return questionRepository.postQuestion(question, image);

    }

    public List<Question> getQuestionsOfUser(String username) {
        return questionRepository.getQuestionsOfUser(username);
    }


    public int deleteQuestion(String username, Long questionID) {
        return questionRepository.deleteQuestion(username, questionID);
    }

    public int updateQuestion(String author, int id, String title, String text, String tags, MultipartFile image) throws IOException {
        return questionRepository.updateQuestion(author, id, title, text, tags, image);
    }

}