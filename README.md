# StackOverflowSD
Full Stack project in my 3rd university year.
<br>
The project had to mimic the StackOverflow website, more detailed requirements can be seen [here][ProjectRequirments.pdf].
<br>
Done using
<ul>
	<li>Spring Boot</li>
	<li>React Native</li>
	<li>MySQL Server</li>
</ul>

The project uses JWT tokens for security.
<br>
## The website
When the user enters on the website it is redirected to the login page.
<br>

![login image](mdImages/login.png)
<br>
<br>
If he does not have an account he can go the register page and create one.
<br>
Upon successful login the user is presented with the main page of the app. By default he can see his posted questions.
<br>
In the top left part the main menu is presented where the user can
<ul>
	<li>Search questions</li>
	<li>See his questions</li>
	<li>Post a new question</li>
	<li>See his answers</li>
</ul>
<br>

![mainMenu image](mdImages/mainMenu.png)
<br>
<br>
By clicking on a question the user can post an answer to that question and see the answers posted to that question.
<br>
If the user is the author it also has access to edit and delete buttons.
<br>

![questionAnswers image](mdImages/questionAnswers.png)

<br>
<br>

When editing a question or an answer the UI is very simple.

![editAnswer image](mdImages/editAnswer.png)

<br>
<br>

To post a new question the user must input the title and text for the question, optionally it can add as many tags as he wants and an image.

![postQuestion image](mdImages/postQuestion.png)

<br>
And is redirected to "My questions page"

![myQuestionsCat image](mdImages/myQuestionsCat.png)