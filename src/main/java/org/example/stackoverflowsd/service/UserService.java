package org.example.stackoverflowsd.service;


import org.example.stackoverflowsd.model.User;
import org.example.stackoverflowsd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service

public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getUsers(){
        return (List<User>) userRepository.findAll();
    }

}
