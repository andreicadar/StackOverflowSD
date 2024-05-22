package org.example.stackoverflowsd.controller;

import org.example.stackoverflowsd.model.Answer;
import org.example.stackoverflowsd.model.AuthRequest;
import org.example.stackoverflowsd.model.Question;
import org.example.stackoverflowsd.model.User;
import org.example.stackoverflowsd.service.EmailService;
import org.example.stackoverflowsd.service.JwtService;
import org.example.stackoverflowsd.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
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
@CrossOrigin("*")
public class UserController{

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

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
        if(userService.checkIfUserExists(userInfo.getUsername()) == 1 || userService.findUserByEmail(userInfo.getEmail()) == 1)
        {
            return ResponseEntity.badRequest().body("User already exists");
        }
            userInfo.setRole("ROLE_USER");
            return ResponseEntity.ok(userService.addUser(userInfo));
    }

    @GetMapping("/updateUser")
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

    @GetMapping("/getUserByUsername")
    public ResponseEntity<?> getUserByUsername(@RequestHeader("Authorization") String token, @RequestParam String username) {

        String tokenUsername = jwtService.extractUsername(token.substring(7));
        if(tokenUsername.equals(username))
        {
            User user = userService.getUserByUsername(username);
            if(user != null) {
                return ResponseEntity.ok().body(user);
            }
            else {
                return ResponseEntity.status(401).build();
            }
        }
        else {
            return ResponseEntity.status(401).build();
        }

    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        if(userService.checkIfUserIsBaned(authRequest.getUsername())) {
            return ResponseEntity.status(401).body("You are baned!");
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated() && !userService.checkIfUserIsBaned(authRequest.getUsername())) {
            return ResponseEntity.status(200).body(jwtService.generateToken(authRequest.getUsername()));
        } else {
            return ResponseEntity.status(400).body("Bad credentials");
        }
    }

    @GetMapping("/deleteUser")
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
                    return ResponseEntity.status(500).body("Internal Server Error");
            } else {
                return ResponseEntity.status(401).body("Unauthorized");
            }
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("Bad Request");
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

    @GetMapping("/deleteQuestion")
    public ResponseEntity<String> deleteQuestion(@RequestHeader("Authorization") String token, @RequestParam String username, @RequestParam Long questionID) {
        if(checkIfUserMatchesToken(token, username) == 1) {
            int result = userService.deleteQuestion(username, questionID);
            if(result == 1) {
                return ResponseEntity.ok("Question deleted successfully");
            }
            else if(result == 2) {
                return ResponseEntity.badRequest().body("Question not found");
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
        else {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/updateQuestion")
    public ResponseEntity<String> updateQuestion(@RequestHeader("Authorization") String token, @RequestParam String author, @RequestParam Long questionID, @RequestParam(required = false) String title, @RequestParam(required = false) String text,
                                                 @RequestParam(required = false) String tags, @RequestParam(required = false) MultipartFile image) throws IOException {
        if(checkIfUserMatchesToken(token, author) == 1) {

            if(userService.updateQuestion(author, questionID, title, text, tags, image) == 1) {
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

    @GetMapping("/getQuestionByID")
    public ResponseEntity<?> getQuestionByID(@RequestHeader("Authorization") String token, @RequestParam String username, @RequestParam Long questionID) {
        if(checkIfUserMatchesToken(token, username) == 1) {
            if(userService.getQuestionById(questionID) != null) {
                return ResponseEntity.ok().body(userService.getQuestionById(questionID));
            }
            else {
                return ResponseEntity.badRequest().body("Question not found");
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

    @GetMapping("/upvoteQuestion")
    public ResponseEntity<String> upvoteQuestion(@RequestHeader("Authorization") String token, @RequestParam String username, @RequestParam int questionID) {
        System.out.println("Upvote question222222");
        System.out.println(token);
        if(checkIfUserMatchesToken(token, username) == 1) {
            System.out.println("Upvote question");
            int result = userService.upvoteQuestion(username, questionID);
            if(result== 1) {
                return ResponseEntity.ok("Question upvoted successfully");
            }
            else if(result == 2) {
                System.out.println("Question already upvoted");
                return ResponseEntity.badRequest().body("Question already upvoted");
            }
            else {
                System.out.println("Cannot vote own question");
                return ResponseEntity.badRequest().body("Cannot vote own question");
            }
        }
        else {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/downvoteQuestion")
    public ResponseEntity<String> downvoteQuestion(@RequestHeader("Authorization") String token, @RequestParam String username, @RequestParam int questionID) {
        if(checkIfUserMatchesToken(token, username) == 1) {
            int result = userService.downvoteQuestion(username, questionID);
            if(result == 1) {
                return ResponseEntity.ok("Question downvoted successfully");
            }
            else if(result == 2) {
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
    public ResponseEntity<?> getQuestionDetails(@RequestHeader("Authorization") String token, @RequestParam String username, @RequestParam Long questionID) {
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
                                                 @RequestParam Long questionID) {

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

    @GetMapping("/getAnswerByID")
    public ResponseEntity<?> getAnswerByID(@RequestHeader("Authorization") String token, @RequestParam String username, @RequestParam Long answerID) {
        if(checkIfUserMatchesToken(token, username) == 1) {
            if(userService.getAnswerByID(answerID) != null) {
                return ResponseEntity.ok().body(userService.getAnswerByID(answerID));
            }
            else {
                return ResponseEntity.badRequest().body("Answer not found");
            }
        }
        else {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/getAnswersOfUser")
    public ResponseEntity<?> getAnswersOfUser(@RequestHeader("Authorization") String token, @RequestParam String username) {
        if(checkIfUserMatchesToken(token, username) == 1) {
            return ResponseEntity.ok().body(userService.getAnswersOfUser(username));
        }
        else {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/deleteAnswer")
    public ResponseEntity<String> deleteAnswer(@RequestHeader("Authorization") String token, @RequestParam String username, @RequestParam Long answerID) {
        if(checkIfUserMatchesToken(token, username) == 1) {
            int result = userService.deleteAnswer(username, answerID);
            if(result == 1) {
                return ResponseEntity.ok("Answer deleted successfully");
            }
            else if(result == 2) {
                return ResponseEntity.badRequest().body("Answer not found");
            }else{
                return ResponseEntity.badRequest().build();
            }
        }
        else {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/upvoteAnswer")
    public ResponseEntity<String> upvoteAnswer(@RequestHeader("Authorization") String token, @RequestParam String username, @RequestParam int answerID) {
        if(checkIfUserMatchesToken(token, username) == 1) {
            int result = userService.upvoteAnswer(username, answerID);
            if(result == 1) {
                return ResponseEntity.ok("Answer upvoted successfully");
            }
            else if(result == 2) {
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

    @GetMapping("/downvoteAnswer")
    public ResponseEntity<String> downvoteAnswer(@RequestHeader("Authorization") String token, @RequestParam String username, @RequestParam int answerID) {
        if(checkIfUserMatchesToken(token, username) == 1) {
            int result = userService.downvoteAnswer(username, answerID);
            if(result == 1) {
                return ResponseEntity.ok("Answer downvoted successfully");
            }
            else if(result == 2) {
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

    @GetMapping("/updateAnswer")
    public ResponseEntity<String> updateAnswer(@RequestHeader("Authorization") String token, @RequestParam String username, @RequestParam Long answerID, @RequestParam(required = false) String text,
                                               @RequestParam(required = false) MultipartFile image) throws IOException {
        if(checkIfUserMatchesToken(token, username) == 1) {
            if(userService.updateAnswer(username, answerID, text, image) == 1) {
                return ResponseEntity.ok("Answer updated successfully");
            }
            else {
                return ResponseEntity.badRequest().build();
            }
        }
        else {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/banUser")
    public ResponseEntity<String> banUser(@RequestHeader("Authorization") String token, @RequestParam String username, @RequestParam String userToBan) {
        if(checkIfUserMatchesToken(token, username) == 1) {
            int result = userService.banUser(username, userToBan);
            if(result == 1) {
                User user = userService.getUserByUsername(userToBan);
                try
                {
                    emailService.sendSimpleMessage(user.getEmail());
                }
                catch (Exception e) {
                    return ResponseEntity.badRequest().body("Cannot send email");
                }
                return ResponseEntity.ok("User banned successfully");
            }
            else if(result == 2){
                return ResponseEntity.status(401).build();
            }
            else if(result == 3) {
                return ResponseEntity.badRequest().body("Cannot ban yourself");
            }
            else if(result == 4) {
                return ResponseEntity.badRequest().build();
            }
            else
                return ResponseEntity.badRequest().body("Cannot ban user");
        }
        else {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/unbanUser")
    public ResponseEntity<String> unbanUser(@RequestHeader("Authorization") String token, @RequestParam String username, @RequestParam String userToUnban) {
        if(checkIfUserMatchesToken(token, username) == 1) {
            int result = userService.unbanUser(username, userToUnban);
            if(result == 1) {
                return ResponseEntity.ok("User unbanned successfully");
            }
            else if(result == 2){
                return ResponseEntity.status(401).build();
            }
            else if(result == 3) {
                return ResponseEntity.badRequest().body("Cannot unban yourself");
            }
            else if(result == 4) {
                return ResponseEntity.badRequest().build();
            }
            else
                return ResponseEntity.badRequest().body("Cannot unban user");
        }
        else {
            return ResponseEntity.status(401).build();
        }
    }

}
