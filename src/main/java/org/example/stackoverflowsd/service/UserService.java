package org.example.stackoverflowsd.service;


import org.example.stackoverflowsd.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends UserDetailsService {
    UserDetails loadUserByUsername(String username);
    void saveUser(User user);
}

