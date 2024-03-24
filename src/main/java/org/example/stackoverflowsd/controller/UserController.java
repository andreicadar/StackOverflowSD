package org.example.stackoverflowsd.controller;

import org.example.stackoverflowsd.model.AuthRequest;
import org.example.stackoverflowsd.model.Question;
import org.example.stackoverflowsd.model.User;
import org.example.stackoverflowsd.service.JwtService;
import org.example.stackoverflowsd.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    int checkIfUserMatchesToken(String token, String username) {
        if(token != null && token.startsWith("Bearer ")) {
            String tokenValue = token.substring(7);
            if(jwtService.extractUsername(tokenValue).equals(username)) {
                return 1;
            }
            else {
                return 0;
            }
        }
        return 0;
    }

    @GetMapping("/welcome")
    public ResponseEntity<String> welcome(@RequestHeader("Authorization") String token, @RequestParam String username) {

            if(checkIfUserMatchesToken(token, username) == 1) {
                return ResponseEntity.ok("Welcome to StackOverflowSD");
            }
            else {
                return ResponseEntity.status(401).build();
            }
    }

    @PostMapping("/register")
    public ResponseEntity<String> addNewUser(@RequestBody User userInfo) {
        if(userService.checkIfUserExists(userInfo.getUsername()) == 1)
        {
            return ResponseEntity.badRequest().body("User already exists");
        }
            return ResponseEntity.ok(userService.addUser(userInfo));
    }
//
//    @GetMapping("/user/userProfile")
//    @PreAuthorize("hasAuthority('ROLE_USER')")
//    public String userProfile() {
//        return "Welcome to User Profile";
//    }
//
//    @GetMapping("/admin/adminProfile")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public String adminProfile() {
//        return "Welcome to Admin Profile";
//    }

    @PostMapping("/login")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authRequest.getUsername());
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }
    }

    @PostMapping("/postQuestion")
    public ResponseEntity<String> postQuestion(@RequestHeader("Authorization") String token, @RequestParam MultipartFile image,
                                                @RequestParam String author, @RequestParam String title, @RequestParam String text,
                                               @RequestParam String tags) {
    try {
            Question question = new Question(author, title, text, tags);
            if (checkIfUserMatchesToken(token, question.getAuthor()) == 1) {
                if(userService.postQuestion(question, image) == 1)
                    return ResponseEntity.ok("Question posted successfully");
                else
                    return ResponseEntity.status(500).build();
            } else {
                return ResponseEntity.status(401).build();
            }
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/getQuestionsOfUser")
    public ResponseEntity<?> getQuestionsOfUser(@RequestHeader("Authorization") String token, @RequestParam String username) {
        if(checkIfUserMatchesToken(token, username) == 1) {
                return ResponseEntity.ok().body(userService.getQuestionsOfUser(username));
        }
        else {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/deleteQuestion")
    public ResponseEntity<String> deleteQuestion(@RequestHeader("Authorization") String token, @RequestParam String username, @RequestParam Long questionID) {
        if(checkIfUserMatchesToken(token, username) == 1) {
            if(userService.deleteQuestion(username, questionID) == 1) {
                return ResponseEntity.ok("Question deleted successfully");
            }
            else {
                return ResponseEntity.badRequest().build();
            }
        }
        else {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/updateQuestion")
    public ResponseEntity<String> updateQuestion(@RequestHeader("Authorization") String token, @RequestParam String author, @RequestParam int id, @RequestParam(required = false) String title, @RequestParam(required = false) String text,
                                                 @RequestParam(required = false) String tags, @RequestParam(required = false) MultipartFile image) throws IOException {
        if(checkIfUserMatchesToken(token, author) == 1) {

            if(userService.updateQuestion(author, id, title, text, tags, image) == 1) {
                return ResponseEntity.ok("Question updated successfully");
            }
            else {
                return ResponseEntity.badRequest().build();
            }
        }
        else {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/searchQuestions")
    public ResponseEntity<?> searchQuestions(@RequestHeader("Authorization") String token, @RequestParam String username, @RequestParam(required = false) String title, @RequestParam(required = false) String text, @RequestParam(required = false) String author, @RequestParam(required = false) String tags) {
        if(checkIfUserMatchesToken(token, username) == 1) {
            return ResponseEntity.ok(userService.searchQuestions(title, text, author, tags));
        }
        else {
            return ResponseEntity.status(401).build();
        }
    }


}
