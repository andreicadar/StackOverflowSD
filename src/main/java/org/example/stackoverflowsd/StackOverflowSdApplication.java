package org.example.stackoverflowsd;


import org.example.stackoverflowsd.controller.UserController;
import org.example.stackoverflowsd.model.User;
import org.example.stackoverflowsd.repository.UserRepository;
import org.example.stackoverflowsd.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;


@SpringBootApplication
@ComponentScan(basePackageClasses = UserController.class)
@ComponentScan(basePackageClasses = UserService.class)
@ComponentScan(basePackageClasses = UserRepository.class)
@ComponentScan(basePackageClasses = User.class)
//@ComponentScan("controller")
//@ComponentScan("service")
//@ComponentScan("model")
//@ComponentScan("repository")
public class StackOverflowSdApplication {

	public static void main(String[] args) {
		SpringApplication.run(StackOverflowSdApplication.class, args);
	}

}
