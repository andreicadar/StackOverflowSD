package org.example.stackoverflowsd;


import org.example.stackoverflowsd.configuration.JwtAuthFilter;
import org.example.stackoverflowsd.configuration.SecurityConfig;
import org.example.stackoverflowsd.controller.UserController;
import org.example.stackoverflowsd.model.User;
import org.example.stackoverflowsd.model.UserInfoDetails;
import org.example.stackoverflowsd.repository.QuestionRepository;
import org.example.stackoverflowsd.repository.UserRepository;
import org.example.stackoverflowsd.service.UserInfoService;
import org.example.stackoverflowsd.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication(scanBasePackages = {"org.example.stackoverflowsd.service"})
@ComponentScan(basePackageClasses = UserController.class)
@ComponentScan(basePackageClasses = UserService.class)
@ComponentScan(basePackageClasses = UserRepository.class)
@ComponentScan(basePackageClasses = User.class)
@ComponentScan(basePackageClasses = UserInfoDetails.class)
@ComponentScan(basePackageClasses = UserInfoService.class)
@ComponentScan(basePackageClasses = SecurityConfig.class)
@ComponentScan(basePackageClasses = JwtAuthFilter.class)
@ComponentScan(basePackageClasses = QuestionRepository.class)

//@ComponentScan("controller")
//@ComponentScan("service")
//@ComponentScan("model")
//@ComponentScan("repository")
public class StackOverflowSdApplication {

	public static void main(String[] args) {
		SpringApplication.run(StackOverflowSdApplication.class, args);
	}

}
