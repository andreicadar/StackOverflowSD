package org.example.stackoverflowsd;

import org.example.stackoverflowsd.controller.UserController;
import org.example.stackoverflowsd.model.*;
import org.example.stackoverflowsd.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertNull;

@SpringBootTest
class StackOverflowSdApplicationTests {

	@Autowired
	UserRepository userRepository;
	@Autowired
	UserController userController;

	//test insert of a new user
	String getUserToken(String username, String password) {
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		String token = userController.authenticateAndGetToken(new AuthRequest(user.getUsername(), user.getPassword()));
		System.out.println(token);
		token = "Bearer " + token;
		return token;

	}

	@Test
	void testInsertUser() {
		//test insertion of new user in data base
		User user = new User();
		user.setUsername("test");
		user.setPassword("test");
		user.setEmail("aaa@.com");
		user.setRole("ROLE_USER");
		user.setScore(0);


		ResponseEntity<String> response = userController.addNewUser(user);
		assertEquals(200, response.getStatusCodeValue());
	}

	void insertUserWithoutAssert(String username) {
		//test insertion of new user in data base
		User user = new User();
		user.setUsername(username);
		user.setPassword("test");
		user.setEmail("aaa@.com");
		user.setRole("ROLE_USER");
		user.setScore(0);
		ResponseEntity<String> response = userController.addNewUser(user);
	}

	//test get user by username


	@Test
	void testGetUserByUsername() {
		//test get user by username
		//login and get a token

		try {
			insertUserWithoutAssert("test");
		} catch (Exception e) {
			System.out.println("User already exists");
		}
		User user = new User();
		user.setUsername("test");
		user.setPassword("test");
		String token = getUserToken(user.getUsername(), user.getPassword());

		//get user by username
		ResponseEntity<User> response2 = (ResponseEntity<User>) userController.getUserByUsername(token, "test");
		assertEquals(200, response2.getStatusCodeValue());


		ResponseEntity<String> response4 = userController.deleteUser(token, user.getUsername());
	}

	@Test
	void updateUser() throws IOException {
		//test update user
		//login and get a token
		try {
			insertUserWithoutAssert("test");
		} catch (Exception e) {
			System.out.println("User already exists");
		}


		User user = new User();
		user.setUsername("test");
		user.setPassword("test");
		String token = getUserToken(user.getUsername(), user.getPassword());

		//update user
		User user2 = new User();
		user2.setUsername("test2");
		user2.setPassword("test");
		user2.setEmail("aaa@.com");
		user2.setRole("ROLE_USER");
		user2.setScore(0);
		ResponseEntity<String> response3 = userController.updateUser(token, user.getUsername(), user2.getUsername(), user2.getPassword(), user2.getEmail());

		token = getUserToken(user2.getUsername(), user2.getPassword());
		assertNotNull("Token is not null", token);

		ResponseEntity<String> response4 = userController.deleteUser(token, user2.getUsername());
	}

	@Test
	void deleteUser() {
		//test delete user
		//login and get a token
		insertUserWithoutAssert("test");

		User user = new User();
		user.setUsername("test");
		user.setPassword("test");
		String token = getUserToken(user.getUsername(), user.getPassword());

		//delete user
		ResponseEntity<String> response4 = userController.deleteUser(token, user.getUsername());
		assertEquals(200, response4.getStatusCodeValue());

		try {
			token = getUserToken(user.getUsername(), user.getPassword());
		} catch (Exception e) {
			token = null;
		}
		assertNull("Token is null", token);
	}

	public static MultipartFile convert(String filePath) throws IOException {
		File file = new File(filePath);
		FileInputStream input = new FileInputStream(file);

		return new MultipartFile() {
			@Override
			public String getName() {
				return file.getName();
			}

			@Override
			public String getOriginalFilename() {
				return file.getName();
			}

			@Override
			public String getContentType() {
				return "application/octet-stream"; // You can change this if you know the content type
			}

			@Override
			public boolean isEmpty() {
				return file.length() == 0;
			}

			@Override
			public long getSize() {
				return file.length();
			}

			@Override
			public byte[] getBytes() throws IOException {
				byte[] bytes = new byte[(int) file.length()];
				input.read(bytes);
				return bytes;
			}

			@Override
			public InputStream getInputStream() throws IOException {
				return input;
			}

			@Override
			public void transferTo(File dest) throws IOException, IllegalStateException {
				new FileOutputStream(dest).write(getBytes());
			}
		};
	}

	@Test
	void postQuestion() throws IOException {
		//test post question
		//login and get a token
		insertUserWithoutAssert("test3");

		User user = new User();
		user.setUsername("test3");
		user.setPassword("test");
		String token = getUserToken(user.getUsername(), user.getPassword());

		//open multipart file from location on PC
		String filePath = ".\\images\\A4U3.jpg";
		MultipartFile image = convert(filePath);

		//post question
		ResponseEntity<String> response5 = userController.postQuestion(token, image, "test3", "test", "test", "aaa,bbb");
		assertEquals(200, response5.getStatusCodeValue());

		ResponseEntity<List<Question>> response = (ResponseEntity<List<Question>>) userController.getQuestionsOfUser(token, user.getUsername());
		Question question = null;
		if (response != null && response.getBody() != null && !response.getBody().isEmpty())
			question = response.getBody().get(0); // Use .get(0) for Lists
		assertNotNull("Question is not null", question);

		ResponseEntity<String> respoonse5 = userController.deleteQuestion(token, user.getUsername(), question.getId());
		assertEquals(200, respoonse5.getStatusCodeValue());
		ResponseEntity<String> response4 = userController.deleteUser(token, user.getUsername());
		assertEquals(200, respoonse5.getStatusCodeValue());
	}

	@Test
	void deleteQuestion() throws IOException {
		//test post question
		//login and get a token
		insertUserWithoutAssert("test3");

		User user = new User();
		user.setUsername("test3");
		user.setPassword("test");
		String token = getUserToken(user.getUsername(), user.getPassword());

		//open multipart file from location on PC
		String filePath = ".\\images\\A4U3.jpg";
		MultipartFile image = convert(filePath);

		//post question
		ResponseEntity<String> response5 = userController.postQuestion(token, image, "test3", "test", "test", "aaa,bbb");
		assertEquals(200, response5.getStatusCodeValue());

		ResponseEntity<List<Question>> response = (ResponseEntity<List<Question>>) userController.getQuestionsOfUser(token, user.getUsername());
		Question question = null;
		if (response != null && response.getBody() != null && !response.getBody().isEmpty())
			question = response.getBody().get(0); // Use .get(0) for Lists
		assertNotNull("Question is not null", question);

		ResponseEntity<String> response6 = userController.deleteQuestion(token, user.getUsername(), question.getId());
		assertEquals(200, response6.getStatusCodeValue());

		//check if question deleted search it by id
		ResponseEntity<String> response7 = (ResponseEntity<String>) userController.getQuestionByID(token, user.getUsername(), question.getId());
		assertEquals("Question not found", response7.getBody());


		ResponseEntity<String> response4 = userController.deleteUser(token, user.getUsername());
		assertEquals(200, response4.getStatusCodeValue());
	}

	@Test
	void updateQuestion() throws IOException {
		//test post question
		//login and get a token
		insertUserWithoutAssert("test3");

		User user = new User();
		user.setUsername("test3");
		user.setPassword("test");
		String token = getUserToken(user.getUsername(), user.getPassword());

		//open multipart file from location on PC
		String filePath = ".\\images\\A4U3.jpg";
		MultipartFile image = convert(filePath);

		//post question
		ResponseEntity<String> response5 = userController.postQuestion(token, image, "test3", "test", "test", "aaa,bbb");
		assertEquals(200, response5.getStatusCodeValue());

		ResponseEntity<List<Question>> response = (ResponseEntity<List<Question>>) userController.getQuestionsOfUser(token, user.getUsername());
		Question question = null;
		if (response != null && response.getBody() != null && !response.getBody().isEmpty())
			question = response.getBody().get(0); // Use .get(0) for Lists
		assertNotNull("Question is not null", question);

		//open multipart file from location on PC
		String filePath2 = ".\\images\\A4U3.jpg";
		MultipartFile image2 = convert(filePath2);

		//update question
		ResponseEntity<String> response6 = userController.updateQuestion(token, user.getUsername(), question.getId(), "test2", "test2", "aaa,bbb,ccc", image2);
		assertEquals(200, response6.getStatusCodeValue());

		ResponseEntity<List<Question>> response2 = (ResponseEntity<List<Question>>) userController.getQuestionsOfUser(token, user.getUsername());
		question = null;
		if (response2 != null && response2.getBody() != null && !response2.getBody().isEmpty())
			question = response2.getBody().get(0); // Use .get(0) for Lists

		assertEquals("test2", question.getTitle());
		assertEquals("test2", question.getText());
		assertEquals("aaa, bbb, ccc", question.getTags());

		ResponseEntity<String> response7 = userController.deleteQuestion(token, user.getUsername(), question.getId());
		assertEquals(200, response7.getStatusCodeValue());
		ResponseEntity<String> response4 = userController.deleteUser(token, user.getUsername());
		assertEquals(200, response4.getStatusCodeValue());

	}

	@Test
	void getQuestionByID()
	{
		//test post question
		//login and get a token
		insertUserWithoutAssert("test3");

		User user = new User();
		user.setUsername("test3");
		user.setPassword("test");
		String token = getUserToken(user.getUsername(), user.getPassword());

		//open multipart file from location on PC
		String filePath = ".\\images\\A4U3.jpg";
		MultipartFile image = null;
		try {
			image = convert(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//post question
		ResponseEntity<String> response5 = userController.postQuestion(token, image, "test3", "test", "test", "aaa,bbb");
		assertEquals(200, response5.getStatusCodeValue());

		ResponseEntity<List<Question>> response = (ResponseEntity<List<Question>>) userController.getQuestionsOfUser(token, user.getUsername());
		Question question = null;
		if (response != null && response.getBody() != null && !response.getBody().isEmpty())
			question = response.getBody().get(0); // Use .get(0) for Lists
		assertNotNull("Question is not null", question);

		ResponseEntity<String> response8 = (ResponseEntity<String>) userController.getQuestionByID(token, user.getUsername(), question.getId());
		assertEquals(200, response8.getStatusCodeValue());

		ResponseEntity<String> response7 = userController.deleteQuestion(token, user.getUsername(), question.getId());
		assertEquals(200, response7.getStatusCodeValue());
		ResponseEntity<String> response4 = userController.deleteUser(token, user.getUsername());
		assertEquals(200, response4.getStatusCodeValue());
	}

	@Test
	void insertAnswer()
	{
		//test post question
		//login and get a token
		insertUserWithoutAssert("test3");

		User user = new User();
		user.setUsername("test3");
		user.setPassword("test");
		String token = getUserToken(user.getUsername(), user.getPassword());

		//open multipart file from location on PC
		String filePath = ".\\images\\A4U3.jpg";
		MultipartFile image = null;
		try {
			image = convert(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//post question
		ResponseEntity<String> response5 = userController.postQuestion(token, image, "test3", "test", "test", "aaa,bbb");
		assertEquals(200, response5.getStatusCodeValue());

		ResponseEntity<List<Question>> response = (ResponseEntity<List<Question>>) userController.getQuestionsOfUser(token, user.getUsername());
		Question question = null;
		if (response != null && response.getBody() != null && !response.getBody().isEmpty())
			question = response.getBody().get(0); // Use .get(0) for Lists
		assertNotNull("Question is not null", question);

		//open multipart file from location on PC
		String filePath2 = ".\\images\\A4U3.jpg";
		MultipartFile image2 = null;
		try {
			image2 = convert(filePath2);
		} catch (IOException e) {
			e.printStackTrace();
		}


		ResponseEntity<String> response6 = userController.answerQuestion(token, image2, "test3", "testAnswer", question.getId());
		assertEquals(200, response6.getStatusCodeValue());

		ResponseEntity<QuestionAnswers> questionAnswer = (ResponseEntity<QuestionAnswers>) userController.getQuestionDetails(token, user.getUsername(), question.getId());

		ResponseEntity<String> response8 = userController.deleteAnswer(token, user.getUsername(), questionAnswer.getBody().getAnswers().get(0).getId());

		assertEquals(200, response8.getStatusCodeValue());
		ResponseEntity<String> response7 = userController.deleteQuestion(token, user.getUsername(), question.getId());
		assertEquals(200, response7.getStatusCodeValue());
		ResponseEntity<String> response4 = userController.deleteUser(token, user.getUsername());
		assertEquals(200, response4.getStatusCodeValue());
	}

	@Test
	void getAnswerByID() {
		//test post question
		//login and get a token
		insertUserWithoutAssert("test3");

		User user = new User();
		user.setUsername("test3");
		user.setPassword("test");
		String token = getUserToken(user.getUsername(), user.getPassword());

		//open multipart file from location on PC
		String filePath = ".\\images\\A4U3.jpg";
		MultipartFile image = null;
		try {
			image = convert(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//post question
		ResponseEntity<String> response5 = userController.postQuestion(token, image, "test3", "test", "test", "aaa,bbb");
		assertEquals(200, response5.getStatusCodeValue());

		ResponseEntity<List<Question>> response = (ResponseEntity<List<Question>>) userController.getQuestionsOfUser(token, user.getUsername());
		Question question = null;
		if (response != null && response.getBody() != null && !response.getBody().isEmpty())
			question = response.getBody().get(0); // Use .get(0) for Lists
		assertNotNull("Question is not null", question);

		//open multipart file from location on PC
		String filePath2 = ".\\images\\A4U3.jpg";
		MultipartFile image2 = null;
		try {
			image2 = convert(filePath2);
		} catch (IOException e) {
			e.printStackTrace();
		}

		ResponseEntity<String> response6 = userController.answerQuestion(token, image2, "test3", "testAnswer", question.getId());
		assertEquals(200, response6.getStatusCodeValue());

		ResponseEntity<QuestionAnswers> questionAnswer = (ResponseEntity<QuestionAnswers>) userController.getQuestionDetails(token, user.getUsername(), question.getId());


		ResponseEntity<String> response9 = (ResponseEntity<String>) userController.getAnswerByID(token, user.getUsername(), questionAnswer.getBody().getAnswers().get(0).getId());
		assertEquals(200, response9.getStatusCodeValue());

		ResponseEntity<String> response8 = userController.deleteAnswer(token, user.getUsername(), questionAnswer.getBody().getAnswers().get(0).getId());
		assertEquals(200, response8.getStatusCodeValue());
		ResponseEntity<String> response7 = userController.deleteQuestion(token, user.getUsername(), question.getId());
		assertEquals(200, response7.getStatusCodeValue());
		ResponseEntity<String> response4 = userController.deleteUser(token, user.getUsername());
		assertEquals(200, response4.getStatusCodeValue());

	}

	@Test
	void deleteAnswer()
	{
		//test post question
		//login and get a token
		insertUserWithoutAssert("test3");

		User user = new User();
		user.setUsername("test3");
		user.setPassword("test");
		String token = getUserToken(user.getUsername(), user.getPassword());

		//open multipart file from location on PC
		String filePath = ".\\images\\A4U3.jpg";
		MultipartFile image = null;
		try {
			image = convert(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//post question
		ResponseEntity<String> response5 = userController.postQuestion(token, image, "test3", "test", "test", "aaa,bbb");
		assertEquals(200, response5.getStatusCodeValue());

		ResponseEntity<List<Question>> response = (ResponseEntity<List<Question>>) userController.getQuestionsOfUser(token, user.getUsername());
		Question question = null;
		if (response != null && response.getBody() != null && !response.getBody().isEmpty())
			question = response.getBody().get(0); // Use .get(0) for Lists
		assertNotNull("Question is not null", question);

		//open multipart file from location on PC
		String filePath2 = ".\\images\\A4U3.jpg";
		MultipartFile image2 = null;
		try {
			image2 = convert(filePath2);
		} catch (IOException e) {
			e.printStackTrace();
		}

		ResponseEntity<String> response6 = userController.answerQuestion(token, image2, "test3", "testAnswer", question.getId());
		assertEquals(200, response6.getStatusCodeValue());

		ResponseEntity<QuestionAnswers> questionAnswer = (ResponseEntity<QuestionAnswers>) userController.getQuestionDetails(token, user.getUsername(), question.getId());

		ResponseEntity<String> response8 = userController.deleteAnswer(token, user.getUsername(), questionAnswer.getBody().getAnswers().get(0).getId());
		assertEquals(200, response8.getStatusCodeValue());

		ResponseEntity<String> response9 = (ResponseEntity<String>) userController.getAnswerByID(token, user.getUsername(), questionAnswer.getBody().getAnswers().get(0).getId());
		assertEquals(400, response9.getStatusCodeValue());
		assertEquals("Answer not found", response9.getBody());


		ResponseEntity<String> response7 = userController.deleteQuestion(token, user.getUsername(), question.getId());
		assertEquals(200, response7.getStatusCodeValue());
		ResponseEntity<String> response4 = userController.deleteUser(token, user.getUsername());
		assertEquals(200, response4.getStatusCodeValue());
	}

	@Test
	void updateAnswer() throws IOException {
		//test post question
		//login and get a token
		insertUserWithoutAssert("test3");

		User user = new User();
		user.setUsername("test3");
		user.setPassword("test");
		String token = getUserToken(user.getUsername(), user.getPassword());

		//open multipart file from location on PC
		String filePath = ".\\images\\A4U3.jpg";
		MultipartFile image = null;
		try {
			image = convert(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//post question
		ResponseEntity<String> response5 = userController.postQuestion(token, image, "test3", "test", "test", "aaa,bbb");
		assertEquals(200, response5.getStatusCodeValue());

		ResponseEntity<List<Question>> response = (ResponseEntity<List<Question>>) userController.getQuestionsOfUser(token, user.getUsername());
		Question question = null;
		if (response != null && response.getBody() != null && !response.getBody().isEmpty())
			question = response.getBody().get(0); // Use .get(0) for Lists
		assertNotNull("Question is not null", question);

		//open multipart file from location on PC
		String filePath2 = ".\\images\\A4U3.jpg";
		MultipartFile image2 = null;
		try {
			image2 = convert(filePath2);
		} catch (IOException e) {
			e.printStackTrace();
		}

		ResponseEntity<String> response6 = userController.answerQuestion(token, image2, "test3", "testAnswer", question.getId());
		assertEquals(200, response6.getStatusCodeValue());

		ResponseEntity<QuestionAnswers> questionAnswer = (ResponseEntity<QuestionAnswers>) userController.getQuestionDetails(token, user.getUsername(), question.getId());

		ResponseEntity<String> response10 = userController.updateAnswer(token, user.getUsername(), questionAnswer.getBody().getAnswers().get(0).getId(), "testAnswer2", image);
		assertEquals(200, response10.getStatusCodeValue());


		ResponseEntity<Answer> response11 = (ResponseEntity<Answer>) userController.getAnswerByID(token, user.getUsername(), questionAnswer.getBody().getAnswers().get(0).getId());
		assertEquals("testAnswer2", response11.getBody().getText());

		ResponseEntity<String> response8 = userController.deleteAnswer(token, user.getUsername(), questionAnswer.getBody().getAnswers().get(0).getId());
		assertEquals(200, response8.getStatusCodeValue());

		ResponseEntity<String> response7 = userController.deleteQuestion(token, user.getUsername(), question.getId());
		assertEquals(200, response7.getStatusCodeValue());
		ResponseEntity<String> response4 = userController.deleteUser(token, user.getUsername());
		assertEquals(200, response4.getStatusCodeValue());
	}
}
