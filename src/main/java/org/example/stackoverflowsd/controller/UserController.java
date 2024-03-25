package org.example.stackoverflowsd.controller;

import org.example.stackoverflowsd.model.Answer;
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

    @PostMapping("/updateUser")
    public ResponseEntity<String> updateUser(@RequestHeader("Authorization") String token, @RequestParam String username, @RequestParam(required = false) String newUsername, @RequestParam(required = false) String password, @RequestParam(required = false) String email) throws IOException {
        if(checkIfUserMatchesToken(token, username) == 1) {
            if(userService.updateUser(username, newUsername, password, email) == 1) {
                return ResponseEntity.ok("User updated successfully");
            }
            else {
                return ResponseEntity.badRequest().build();
            }
        }
        else {
            return ResponseEntity.status(401).build();
        }
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

    @GetMapping("/getUserByID")
    public ResponseEntity<?> getUserByID(@RequestHeader("Authorization") String token, @RequestParam int id) {

        String tokenUsername = jwtService.extractUsername(token.substring(7));
        User user = userService.getUserByID(tokenUsername, id);
        if(user != null) {
            return ResponseEntity.ok().body(user);
        }
        else {
            return ResponseEntity.status(401).build();
        }

    }

    @PostMapping("/login")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authRequest.getUsername());
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }
    }

    @PostMapping("/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestHeader("Authorization") String token, @RequestParam String username) {
        if(checkIfUserMatchesToken(token, username) == 1) {
            if(userService.deleteUser(username) == 1) {
                return ResponseEntity.ok("User deleted successfully");
            }
            else {
                return ResponseEntity.badRequest().build();
            }
        }
        else {
            return ResponseEntity.status(401).build();
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

    @PostMapping("/upvoteQuestion")
    public ResponseEntity<String> upvoteQuestion(@RequestHeader("Authorization") String token, @RequestParam String username, @RequestParam int questionID) {
        if(checkIfUserMatchesToken(token, username) == 1) {
            if(userService.upvoteQuestion(username, questionID) == 1) {
                return ResponseEntity.ok("Question upvoted successfully");
            }
            else if(userService.upvoteQuestion(username, questionID) == 2) {
                return ResponseEntity.badRequest().body("Question already upvoted");
            }
            else {
                return ResponseEntity.badRequest().body("Cannot vote own question");
            }
        }
        else {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/downvoteQuestion")
    public ResponseEntity<String> downvoteQuestion(@RequestHeader("Authorization") String token, @RequestParam String username, @RequestParam int questionID) {
        if(checkIfUserMatchesToken(token, username) == 1) {
            if(userService.downvoteQuestion(username, questionID) == 1) {
                return ResponseEntity.ok("Question downvoted successfully");
            }
            else if(userService.upvoteQuestion(username, questionID) == 2) {
                return ResponseEntity.badRequest().body("Question already downvoted");
            }
            else {
                return ResponseEntity.badRequest().body("Cannot downvote own question");
            }
        }
        else {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/getQuestionDetails")
    public ResponseEntity<?> getQuestionDetails(@RequestHeader("Authorization") String token, @RequestParam String username, @RequestParam int questionID) {
        if(checkIfUserMatchesToken(token, username) == 1) {
            if(userService.getQuestionDetails(questionID) != null) {
                return ResponseEntity.ok().body(userService.getQuestionDetails(questionID));
            }
            else {
                return ResponseEntity.badRequest().build();
            }
        }
        else {
            return ResponseEntity.status(401).build();
        }
    }


    @PostMapping("/answerQuestion")
    public ResponseEntity<String> answerQuestion(@RequestHeader("Authorization") String token, @RequestParam MultipartFile image,
                                                 @RequestParam String author, @RequestParam String text,
                                                 @RequestParam int questionID) {

        try {
            Answer answer = new Answer(author, text);
            if (checkIfUserMatchesToken(token, answer.getAuthor()) == 1) {
                if(userService.answerQuestion(answer, image, questionID) == 1)
                    return ResponseEntity.ok("Answer posted successfully");
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

    @PostMapping("/deleteAnswer")
    public ResponseEntity<String> deleteAnswer(@RequestHeader("Authorization") String token, @RequestParam String username, @RequestParam Long answerID) {
        if(checkIfUserMatchesToken(token, username) == 1) {
            if(userService.deleteAnswer(username, answerID) == 1) {
                return ResponseEntity.ok("Answer deleted successfully");
            }
            else {
                return ResponseEntity.badRequest().build();
            }
        }
        else {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/upvoteAnswer")
    public ResponseEntity<String> upvoteAnswer(@RequestHeader("Authorization") String token, @RequestParam String username, @RequestParam int answerID) {
        if(checkIfUserMatchesToken(token, username) == 1) {
            if(userService.upvoteAnswer(username, answerID) == 1) {
                return ResponseEntity.ok("Answer upvoted successfully");
            }
            else if(userService.upvoteAnswer(username, answerID) == 2) {
                return ResponseEntity.badRequest().body("Answer already upvoted");
            }
            else {
                return ResponseEntity.badRequest().body("Cannot vote own answer");
            }
        }
        else {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/downvoteAnswer")
    public ResponseEntity<String> downvoteAnswer(@RequestHeader("Authorization") String token, @RequestParam String username, @RequestParam int answerID) {
        if(checkIfUserMatchesToken(token, username) == 1) {
            if(userService.downvoteAnswer(username, answerID) == 1) {
                return ResponseEntity.ok("Answer downvoted successfully");
            }
            else if(userService.downvoteAnswer(username, answerID) == 2) {
                return ResponseEntity.badRequest().body("Answer already downvoted");
            }
            else {
                return ResponseEntity.badRequest().body("Cannot downvote own answer");
            }
        }
        else {
            return ResponseEntity.status(401).build();
        }
    }


}
