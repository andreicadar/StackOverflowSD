import React, {useEffect, useState} from 'react';
import {
    banUser,
    deleteAnswer, deleteQuestion, deleteUser,
    getAnswersOfUser,
    getQuestionsOfUser,
    getUserByUsername,
    postAnswerToQuestion,
    postQuestion,
    searchQuestions, seeQuestionDetails, updateAnswer, updateQuestion
} from "./API";
import Question from "./Question";
import Answer from "./Answer";
import * as PropTypes from "prop-types";
import {Navigate} from "react-router-dom";
import { useNavigate } from 'react-router-dom';


function Redirect(props) {
    return null;
}

Redirect.propTypes = {to: PropTypes.string};

function Home({username, token}) {
    const [questions, setQuestions] = useState([]);
    const [questionToBeDetailed, setQuestionToBeDetailed] = useState(null);
    const [answers, setAnswers] = useState([]);
    const [errorMessage, setErrorMessage] = useState('');
    const [showQuestionForm, setShowQuestionForm] = useState(false);
    const [showAnswersForm, setShowAnswersForm] = useState(false);
    const [showQuestions, setShowQuestions] = useState(true);
    const [showAnswers, setShowAnswers] = useState(false);
    const [questionIDToAnswer, setQuestionIDToAnswer] = useState(null);
    const [showSearchQuestions, setShowSearchQuestions] = useState(false);
    const [titleToSearchFor, setTitleToSearchFor] = useState('');
    const [tagToSearchFor, setTagToSearchFor] = useState('');
    const [userToSearchFor, setUserToSearchFor] = useState('');
    const [searchQuestionButtonWasPressed, setSearchQuestionButtonWasPressed] = useState(false);
    const [seeQuestionDetailsBoolean, setSeeQuestionDetailsBoolean] = useState(false);
    const [showEditQuestionForm, setShowEditQuestionForm] = useState(false);
    const [questionToBeEdited, setQuestionToBeEdited] = useState(null);
    const [showEditAnswerForm, setShowEditAnswerForm] = useState(false);
    const [answerToBeEdited, setAnswerToBeEdited] = useState(null);
    const [userToBan, setUserToBan] = useState('');
    const navigate = useNavigate();

    const [questionFormData, setQuestionFormData] = useState({
        title: '',
        text: '',
        tags: [],
    });
    const [answerFormData, setAnswerFormData] = useState({
        text: '',
    });
    const [showUserInfo, setShowUserInfo] = useState(false);
    const [userInfo, setUserInfo] = useState(null);
    const [successMessage, setSuccessMessage] = useState('');
    const [redirectToLogin, setRedirectToLogin] = useState(false);

    useEffect(() => {

        if (!username || !token) {
            setRedirectToLogin(true);
        } else {
            // Fetch questions when username or token changes
            fetchQuestions().then(r => console.log('Questions fetched')).catch(e => console.error('Error fetching questions:', e));
        }
    }, [username, token]);

    if (redirectToLogin) {
        console.log("Redirecting to login...");

        return <Navigate to="/login"/>;
    }

    const fetchUserInformation = async () => {
        try {
            const userData = await getUserByUsername(username, token);
            console.log(userData);
            setUserInfo(userData);
        } catch (error) {
            setErrorMessage('Error fetching user information. Please try again later.');
        }
    };

    const handleUsernameClick = () => {
        fetchUserInformation();
        setShowEditAnswerForm(false);
        setShowEditQuestionForm(false);
        setSeeQuestionDetailsBoolean(false);
        setShowSearchQuestions(false);
        setShowQuestionForm(false);
        setShowQuestions(false);
        setErrorMessage('');
        setShowUserInfo(true);
        setShowAnswers(false);
        setShowAnswersForm(false);
        setErrorMessage('');

    };

    const fetchQuestions = async () => {
        try {
            const response = await getQuestionsOfUser(username, token);
            setSeeQuestionDetailsBoolean(false);
            setShowEditAnswerForm(false);
            setShowEditQuestionForm(false);
            setShowSearchQuestions(false);
            setQuestions(response);
            setShowQuestionForm(false);
            setShowUserInfo(false);
            setShowQuestions(true);
            setShowAnswers(false);
            setShowAnswersForm(false);
            setErrorMessage('');

        } catch (error) {
            setErrorMessage('Error fetching questions. Please try again later.')
        }
    };

    const fetchSearchedQuestions = async () => {
        try {
            const response = await searchQuestions(username, titleToSearchFor, tagToSearchFor, userToSearchFor, token);
            setSeeQuestionDetailsBoolean(false);
            setSearchQuestionButtonWasPressed(true);
            setShowEditAnswerForm(false);
            setShowEditQuestionForm(false);
            setShowSearchQuestions(true);
            setQuestions(response);
            setShowQuestionForm(false);
            setShowUserInfo(false);
            setShowQuestions(false);
            setShowAnswers(false);
            setShowAnswersForm(false);
            setErrorMessage('');

        } catch (error) {
            setErrorMessage('Error fetching questions. Please try again later.')
        }
    };

    const handlePostQuestion = async (event) => {
        event.preventDefault();
        try {
            const {title, text, tags, image} = questionFormData;
            await postQuestion(username, token, image, title, text, tags.join(', '));

            setQuestionFormData({title: '', text: '', tags: [], image: null});
            setSuccessMessage('Question posted successfully.');
            setTimeout(() => {
                setSuccessMessage('');
            }, 3000);
            await fetchQuestions();
        } catch (error) {
            if (error.response && error.response.data && error.response.data.error) {
                setErrorMessage(error.response.data.error);
            } else {
                setErrorMessage('Error posting question. Please try again later.');
            }
        }
    };

    const handleTagChange = (e, index) => {
        const newTags = [...questionFormData.tags];
        newTags[index] = e.target.value;
        setQuestionFormData({...questionFormData, tags: newTags});
    };

    const handleAddTag = () => {
        if (questionFormData.tags.length === 0 || questionFormData.tags[questionFormData.tags.length - 1].trim() !== '') {
            setQuestionFormData({...questionFormData, tags: [...questionFormData.tags, '']});
        }
    };

    const handleImageChangeQuestionForm = (e) => {
        const file = e.target.files[0];
        setQuestionFormData({...questionFormData, image: file});
    };

    const handleImageChangeAnswerForm = (e) => {
        const file = e.target.files[0];
        setAnswerFormData({...answerFormData, image: file});
    };


    const fetchAnswers = async () => {

        try {

            const userAnswers = await getAnswersOfUser(username, token);
            setSeeQuestionDetailsBoolean(false);
            setShowEditAnswerForm(false);
            setShowSearchQuestions(false);
            setAnswers(userAnswers);
            setShowAnswers(true);
            setShowQuestions(false);
            setShowAnswersForm(false);
            setShowQuestionForm(false);
            setShowEditQuestionForm(false);
            setShowUserInfo(false);

        } catch (error) {
            setErrorMessage('Error fetching answers. Please try again later.');
        }
    };

    const handleSeeMyAnswers = async () => {
        setSeeQuestionDetailsBoolean(false);
        setShowSearchQuestions(false);
        setShowQuestionForm(false);
        setShowUserInfo(false);
        setShowEditAnswerForm(false);
        setShowQuestionForm(false);
        setShowAnswers(true);
        setShowEditQuestionForm(false);
        setErrorMessage('');
        await fetchAnswers();
    };

    function handleUserEdit() {

    }

    async function handleUserDelete() {
        try {
            setSuccessMessage('User deleted successfully.')
            await deleteUser(username, token);
            console.log("Redirecting to login...");
            setTimeout(() => {
                navigate('/login');
            }, 2000);
            return <Navigate to="/login"/>;
        }
        catch (error){
            setErrorMessage('Error deleting user. Please try again later.');
        }
    }

    async function handleAnswerDelete(id) {
        try{
            await deleteAnswer(username, id, token);
            setSuccessMessage('Answer deleted successfully.');
            setTimeout(() => {
                setSuccessMessage('');
            }, 3000);
            if(seeQuestionDetailsBoolean){
                await handleSeeQuestionDetailsButton(questionIDToAnswer);
            }
            else if(showAnswers)
            {
                await fetchAnswers();
            }
        }
        catch (error) {
            setErrorMessage('Error deleting answer. Please try again later.');
        }
    }

    async function handleQuestionEdit(id) {
            const question = questions.find(q => q.id === id);
            setSeeQuestionDetailsBoolean(false);
            setShowSearchQuestions(false);
            setShowQuestionForm(false);
            setShowAnswers(false);
            setShowEditQuestionForm(true);
            setShowEditAnswerForm(false);
            setShowUserInfo(false);
            setShowQuestions(false);
            setShowAnswersForm(false);
            setErrorMessage('');
            setShowEditQuestionForm(true);
            console.log(question);
            question.tags = question.tags.split(', ').map(tag => tag.trim());
            setQuestionToBeEdited(question);
    }
    function handleAnswerEdit(id) {
        const answer = answers.find(a => a.id === id);
        setSeeQuestionDetailsBoolean(false);
        setShowSearchQuestions(false);
        setShowQuestionForm(false);
        setShowAnswers(false);
        setShowEditQuestionForm(false);
        setShowEditAnswerForm(true);
        setShowUserInfo(false);
        setShowQuestions(false);
        setShowAnswersForm(false);
        setErrorMessage('');
        console.log(answer);
        setAnswerToBeEdited(answer);
    }

    async function handleQuestionDelete(id) {
        try {
            await deleteQuestion(username, id, token);
            setSuccessMessage('Question deleted successfully.');
            setTimeout(() => {
                setSuccessMessage('');
            }, 3000);
            if (seeQuestionDetailsBoolean) {
                await fetchSearchedQuestions();
            } else if (showQuestions) {
                await fetchQuestions();
            }
            else if(searchQuestionButtonWasPressed)
            {
                await fetchSearchedQuestions();
            }
        } catch (error) {
            setErrorMessage('Error question answer. Please try again later.');
        }
    }


    async function handlePostAnswer(event, id) {
        event.preventDefault();
        try {
            const {text, image} = answerFormData;
            await postAnswerToQuestion(username, token, image, questionIDToAnswer, text);
            setAnswerFormData({text: '', image: null});
            setSuccessMessage('Answer posted successfully.');
            setTimeout(() => {
                setSuccessMessage('');
            }, 3000);
            await handleSeeQuestionDetailsButton(id);

        } catch (error) {
            if (error.response && error.response.data && error.response.data.error) {
                setErrorMessage(error.response.data.error);
            } else {
                setErrorMessage('Error posting answer. Please try again later.');
            }
        }

    }

    function handlePostAnAnswerButton(id) {
        setQuestionIDToAnswer(id);
        setSeeQuestionDetailsBoolean(false);
        setShowSearchQuestions(false);
        setShowEditQuestionForm(false);
        setShowQuestionForm(false);
        setShowAnswers(false);
        setShowEditAnswerForm(false);
        setShowUserInfo(false);
        setShowQuestions(false)
        setShowAnswersForm(true);
    }

    function handlePostAQuestionButton() {
        setShowSearchQuestions(false);
        setSeeQuestionDetailsBoolean(false);
        setShowQuestionForm(true);
        setShowEditAnswerForm(false);
        setShowAnswers(false);
        setShowEditQuestionForm(false);
        setShowUserInfo(false);
        setShowQuestions(false);
        setShowAnswersForm(false);
    }

    function handleSearchQuestionsButton() {
        setTitleToSearchFor('')
        setSearchQuestionButtonWasPressed(false);
        setQuestions([]);
        setShowSearchQuestions(true);
        setShowQuestionForm(false);
        setShowAnswers(false);
        setShowEditAnswerForm(false);
        setShowEditQuestionForm(false);
        setShowUserInfo(false);
        setShowQuestions(false);
        setShowAnswersForm(false);
        setSeeQuestionDetailsBoolean(false);
    }

    async function handleSeeQuestionDetailsButton(id) {
        setQuestionIDToAnswer(id);
        setSeeQuestionDetailsBoolean(true);
        setShowSearchQuestions(false);
        setShowQuestionForm(false);
        setShowAnswers(false);
        setShowEditQuestionForm(false);
        setShowEditAnswerForm(false);
        setShowUserInfo(false);
        setShowQuestions(false);
        setShowAnswersForm(false);

        try {
            const questionDetails = await seeQuestionDetails(username, id, token);

            setQuestionToBeDetailed(questionDetails.question);

            setAnswers(questionDetails.answers);
        } catch (error) {
            setErrorMessage('Error fetching question details. Please try again later.');
        }
    }

    async function handleQuestionEditButton(event) {
        event.preventDefault();
        try {
            const response = await updateQuestion(username, token, questionToBeEdited.id, questionToBeEdited.title, questionToBeEdited.text, questionToBeEdited.tags.join(', '));
            console.log(response)
            setSuccessMessage('Question updated successfully.');
            setTimeout(() => {
                setSuccessMessage('');
            }, 3000)
            await handleSeeQuestionDetailsButton(questionToBeEdited.id);
        }
        catch (error) {
            setErrorMessage('Error updating question. Please try again later.');
            setTimeout(() => {
                setErrorMessage('');
            }, 3000);
        }
    }

    async function handleAnswerEditButton(event) {
        event.preventDefault();
        try{
            const response = await updateAnswer(username, token, answerToBeEdited.id, answerToBeEdited.text);
            console.log(response);
            setSuccessMessage('Answer updated successfully.');
            setTimeout(() => {
                setSuccessMessage('');
            }, 3000);
            await handleSeeQuestionDetailsButton(questionIDToAnswer);
        }
        catch (error) {
            setErrorMessage('Error updating answer. Please try again later.');
            setTimeout(() => {
                setErrorMessage('');
            }, 3000);
        }
    }

    async function banUserButton(event)
    {
       event.preventDefault();
        try {
            const response = await banUser(username, token, userToBan);
            console.log(response);
            setSuccessMessage('User banned successfully.');
            setTimeout(() => {
                setSuccessMessage('');
            }, 3000);
        }
        catch (error) {
            setErrorMessage('Error banning user. Please try again later.');
            setTimeout(() => {
                setErrorMessage('');
            }, 3000);
        }
    }

    return (
        <div style={styles.container}>
            <div style={styles.sideMenu}>
                <button style={styles.menuButton} onClick={handleSearchQuestionsButton}>Search Questions</button>
                <button style={styles.menuButton} onClick={fetchQuestions}>See My Questions</button>
                <button style={styles.menuButton} onClick={handlePostAQuestionButton}>Post a Question</button>
                <button style={styles.menuButton} onClick={handleSeeMyAnswers}>See My Answers</button>
            </div>

            <div style={styles.content}>
                {errorMessage && (
                    <div style={styles.errorMessage}>
                        {errorMessage}
                    </div>
                )}
                {successMessage && (
                    <div style={styles.successMessage}>
                        {successMessage}
                    </div>
                )}
                <button style={styles.button} onClick={handleUsernameClick}>{username}</button>
                {showUserInfo && userInfo && (
                    <div style={{...styles.formContainer, ...userInfoStyles.userInfoContainer}}>
                        <h2 style={userInfoStyles.userInfoTitle}>User Information</h2>
                        <div>
                            <label style={userInfoStyles.userInfoLabel}>ID:</label>
                            <input style={userInfoStyles.userInfoInput} type="text" value={userInfo.id} readOnly/>
                        </div>
                        <div>
                            <label style={userInfoStyles.userInfoLabel}>Username:</label>
                            <input style={userInfoStyles.userInfoInput} type="text" value={userInfo.username} readOnly/>
                        </div>
                        <div>
                            <label style={userInfoStyles.userInfoLabel}>Email:</label>
                            <input style={userInfoStyles.userInfoInput} type="text" value={userInfo.email} readOnly/>
                        </div>
                        <div>
                            <label style={userInfoStyles.userInfoLabel}>Password:</label>
                            <input style={userInfoStyles.userInfoInput} type="text" value={userInfo.password} readOnly/>
                        </div>
                        <div>
                            <label style={userInfoStyles.userInfoLabel}>Role:</label>
                            <input style={userInfoStyles.userInfoInput} type="text" value={userInfo.role} readOnly/>
                        </div>
                        <div>
                            <label style={userInfoStyles.userInfoLabel}>Score:</label>
                            <input style={userInfoStyles.userInfoInput} type="text" value={userInfo.score} readOnly/>
                        </div>
                        <div style={{textAlign: 'center'}}> {}
                            <button style={styles.userInfoEditButton} onClick={() => handleUserEdit()}>✏️</button>

                            <button style={styles.deleteButtonStyle} onClick={() => handleUserDelete()}>DELETE</button>
                        </div>
                        <br></br>
                        {userInfo.role === 'ROLE_MODERATOR' && (
                            <div style={userInfoStyles.userBanContainer}>
                                <div style={userInfoStyles.userToBanRow}>
                                    <label style={{marginRight: '15px',fontSize: '24px', fontWeight: 'bold'}}>User to ban:</label>
                                    <input
                                        style={userInfoStyles.userToBanInput}
                                        type="text"
                                        placeholder="Username"
                                        value={userToBan}
                                        onChange={(e) => setUserToBan(e.target.value)}
                                    />
                                </div>
                                <button
                                    style={userInfoStyles.banUserButton}
                                    onClick={banUserButton}
                                >
                                    BAN USER
                                </button>
                            </div>)
                        }

                    </div>
                )}

                {showSearchQuestions && (
                    <div style={styles.searchContainer}>
                        <h2 style={styles.title}>Search Questions</h2>
                        <div style={styles.searchInputContainer}>
                            <div style={styles.searchRow}>
                                <label style={styles.searchLabel}>Title:</label>
                                <input
                                    style={styles.searchInput}
                                    type="text"
                                    placeholder="Title to search for"
                                    value={titleToSearchFor}
                                    onChange={(e) => setTitleToSearchFor(e.target.value)}
                                />
                            </div>
                            <div style={styles.searchRow}>
                                <label style={styles.searchLabel}> Tag:  </label>
                                <input
                                    style={styles.searchInput}
                                    type="text"
                                    placeholder="Tag to search for"
                                    value={tagToSearchFor}
                                    onChange={(e) => setTagToSearchFor(e.target.value)}
                                />
                            </div>
                            <div style={styles.searchRow}>
                                <label style={styles.searchLabel}>User:</label>
                                <input
                                    style={styles.searchInput}
                                    type="text"
                                    placeholder="User to search for"
                                    value={userToSearchFor}
                                    onChange={(e) => setUserToSearchFor(e.target.value)}
                                />
                            </div>
                            <button
                                style={styles.searchButton}
                                onClick={fetchSearchedQuestions}
                            >
                                Search questions
                            </button>
                        </div>
                        <div style={styles.questionList}>
                            {questions.length > 0 ? (
                                questions.map(question => (
                                    <Question
                                        username={username}
                                        key={question.id}
                                        onPostAnswer={handlePostAnAnswerButton}
                                        {...question}
                                        onDelete={() => handleQuestionDelete(question.id)}
                                        onEdit={() => handleQuestionEdit(question.id)}
                                        onSeeQuestionDetails={() => handleSeeQuestionDetailsButton(question.id)}
                                        comesFromQuestionDetails={false}
                                        token={token}
                                    />
                                ))
                            ) : (
                                searchQuestionButtonWasPressed && (
                                    <p style={styles.noQuestionsText}>No questions found. Please try a different search.</p>
                                )
                            )}
                        </div>
                    </div>
                )}


                {showQuestionForm && (
                    <div style={styles.formContainer}>
                        <h1 style={styles.title}>Post a Question</h1>
                        <form onSubmit={handlePostQuestion}>
                            <label style={styles.formLabel}>Title:</label>
                            <input style={styles.formInput} type="text" value={questionFormData.title}
                                   onChange={(e) => setQuestionFormData({...questionFormData, title: e.target.value})}/><br/>
                            <label style={styles.formLabel}>Text:</label>
                            <textarea style={styles.formTextarea} value={questionFormData.text}
                                      onChange={(e) => setQuestionFormData({
                                          ...questionFormData,
                                          text: e.target.value
                                      })}></textarea><br/>
                            <label style={styles.formLabel}>Tags:</label>
                            <div style={styles.tagContainer}>
                                {questionFormData.tags.map((tag, index) => (
                                    <div key={index} style={styles.tag}>
                                        <input style={styles.formTagInput} type="text" value={tag}
                                               onChange={(e) => handleTagChange(e, index)}/>
                                    </div>
                                ))}
                                <button style={styles.formTagButton} type="button" onClick={handleAddTag}>+</button>
                            </div>
                            <label style={styles.formLabel}>Image:</label>
                            <input style={styles.formInput} type="file" accept="image/*"
                                   onChange={handleImageChangeQuestionForm}/><br/>
                            <button style={styles.formButton} type="submit">Post Question</button>
                        </form>
                    </div>)}

                {showQuestions && (
                    <div>
                        <h1 style={styles.title}>My Questions</h1>
                        {questions.map(question => (
                            <Question
                                username={username}
                                key={question.id}
                                onPostAnswer={handlePostAnAnswerButton}
                                {...question}
                                onDelete={() => handleQuestionDelete(question.id)}
                                onEdit={() => handleQuestionEdit(question.id)}
                                onSeeQuestionDetails={() => handleSeeQuestionDetailsButton(question.id)}
                                comesFromQuestionDetails={false}
                                token={token}
                            />
                        ))}
                    </div>
                )}

                {showAnswersForm && (() => {
                    const selectedQuestion = questions.find(question => question.id === questionIDToAnswer);
                    return (
                        <div style={styles.formContainer}>
                            {selectedQuestion && (
                                <Question
                                    username={username}
                                    key={selectedQuestion.id}
                                    {...selectedQuestion}
                                    onPostAnswer={handlePostAnAnswerButton}
                                    onDelete={() => handleQuestionDelete(selectedQuestion.id)}
                                    onEdit={() => handleQuestionEdit(selectedQuestion.id)}
                                    onSeeQuestionDetails={() => handleSeeQuestionDetailsButton(selectedQuestion.id)}
                                    comesFromQuestionDetails={false}
                                    token={token}
                                />
                            )}
                            <h2>Post an Answer</h2>
                            <form onSubmit={(event) => handlePostAnswer(event, selectedQuestion.id)}>
                                <label style={styles.formLabel}>Text:</label>
                                <textarea
                                    style={styles.formTextarea}
                                    value={answerFormData.text}
                                    onChange={(e) => setAnswerFormData({...answerFormData, text: e.target.value})}
                                ></textarea><br/>
                                <label style={styles.formLabel}>Image:</label>
                                <input
                                    style={styles.formInput}
                                    type="file"
                                    accept="image/*"
                                    onChange={handleImageChangeAnswerForm}
                                /><br/>
                                <button style={styles.formButton} type="submit">Post Answer</button>
                            </form>
                        </div>
                    );
                })()}

                {showAnswers && (
                    <div>
                        <h1 style={styles.title}>My Answers</h1>
                        {answers.map(answer => (
                            <Answer username={username} token={token} key={answer.id} comesFromQuestionDetails={false} onDelete={() => handleAnswerDelete(answer.id)}
                                    onEdit={() => handleAnswerEdit(answer.id)} {...answer} />
                        ))}
                    </div>
                )}

                {seeQuestionDetailsBoolean && questionToBeDetailed && (
                <div style={styles.detailsContainer}>
                    <Question
                        username={username}
                        key={questionToBeDetailed.id}
                        {...questionToBeDetailed}
                        onPostAnswer={handlePostAnAnswerButton}
                        onDelete={() => handleQuestionDelete(questionToBeDetailed.id)}
                        onEdit={() => handleQuestionEdit(questionToBeDetailed.id)}
                        onSeeQuestionDetails={() => handleSeeQuestionDetailsButton(questionToBeDetailed.id)}
                        comesFromQuestionDetails={true}
                        token={token}
                    />
                    {answers.length > 0 && (
                        <div style={styles.answersContainer}>
                            <h2 style={styles.subtitle}>Answers</h2>
                            {answers.map(answer => (
                                <Answer
                                    comesFromQuestionDetails={true}
                                    username={username}
                                    key={answer.id}
                                    onDelete={() => handleAnswerDelete(answer.id)}
                                    onEdit={() => handleAnswerEdit(answer.id)}
                                    {...answer}
                                />
                            ))}
                        </div>
                    )}
                    {answers.length === 0 && (
                        <p style={styles.noQuestionsText}>No answers for this question.</p>
                    )}
                </div>
            )}

                {showEditQuestionForm && (
                    <div style={styles.formContainer}>
                        <h1 style={styles.title}>Edit Question</h1>
                        <form onSubmit={handleQuestionEditButton}>
                            <label style={styles.formLabel}>Title:</label>
                            <input style={styles.formInput} type="text" value={questionToBeEdited.title}
                                   onChange={(e) => {setQuestionToBeEdited({...questionToBeEdited, title: e.target.value});}}/><br/>
                            <label style={styles.formLabel}>Text:</label>
                            <textarea style={styles.formTextarea} value={questionToBeEdited.text}
                                      onChange={(e) => {setQuestionToBeEdited({...questionToBeEdited, text: e.target.value});}}></textarea><br/>
                            {questionToBeEdited.pictureBase64 ? (
                                <img src={`data:image/jpeg;base64,${questionToBeEdited.pictureBase64}`} alt="Question related"
                                     style={styles.image}/>
                            ) : (
                                <div style={styles.imagePlaceholder}>Picture Placeholder</div>
                            )}
                            <div style={styles.questionTags}>
                                {questionToBeEdited.tags.map((tag, index) => (
                                    <div key={index} style={styles.questionTag}>{tag}</div>
                                ))}
                            </div>
                            <div style={styles.questionScore}>Score: {questionToBeEdited.score}</div>
                            <button style={styles.editQuestionButton} type="submit">Edit question</button>
                        </form>
                    </div>
                )
                }

                {showEditAnswerForm && (
                    <div style={styles.formContainer}>
                        <h1 style={styles.title}>Edit Answer</h1>
                        <form onSubmit={handleAnswerEditButton}>
                            <label style={styles.formLabel}>Text:</label>
                            <textarea style={styles.formTextarea} value={answerToBeEdited.text}
                                        onChange={(e) => {setAnswerToBeEdited({...answerToBeEdited, text: e.target.value});}}></textarea><br/>
                            {answerToBeEdited.pictureBase64 ? (
                                <img src={`data:image/jpeg;base64,${answerToBeEdited.pictureBase64}`} alt="Answer related"
                                        style={styles.image}/>
                            ) : (
                                <div style={styles.imagePlaceholder}>Picture Placeholder</div>
                            )}
                            <div style={styles.questionScore}>Score: {answerToBeEdited.score}</div>
                            <button style={styles.editQuestionButton} type="submit">Edit answer</button>
                        </form>
                    </div>
                )
                }




            </div>
        </div>
    );
}

export default Home;

const styles = {
    container: {
        position: 'relative',
        minHeight: '100vh',
        width: '100%',
        overflow: 'hidden',
        margin: '0',
        padding: '0',
        display: 'flex'
    },
    sideMenu: {
        width: '200px',
        background: '#f0f0f0',
        padding: '20px',
        boxSizing: 'border-box',
        height: '100vh',
    },
    content: {
        flex: 1,
        padding: '20px'
    },
    button: {
        position: 'absolute',
        top: '5px',
        right: '10px',
        padding: '10px 20px',
        backgroundColor: '#126c02',
        color: 'white',
        border: 'none',
        borderRadius: '5px',
        cursor: 'pointer',
        fontSize: '22px',
    },
    menuButton: {
        display: 'block',
        backgroundColor: '#007bff',
        color: 'white',
        border: 'none',
        borderRadius: '5px',
        padding: '10px',
        marginBottom: '10px',
        cursor: 'pointer',
        fontSize: '16px',
        textAlign: 'center'
    },
    formContainer: {
        backgroundColor: '#fff',
        padding: '20px',
        borderRadius: '8px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
        marginBottom: '20px'
    },
    formLabel: {
        marginBottom: '5px',
        display: 'block',
        fontSize: '18px',
        fontWeight: 'bold'
    },
    formInput: {
        width: '100%',
        padding: '10px',
        borderRadius: '5px',
        border: '1px solid #ccc',
        marginBottom: '10px',
        fontSize: '16px'
    },
    formTextarea: {
        width: '100%',
        padding: '10px',
        borderRadius: '5px',
        border: '1px solid #ccc',
        marginBottom: '10px',
        resize: 'none',
        minHeight: '100px',
        fontSize: '16px'
    },
    formButton: {
        backgroundColor: '#007bff',
        color: '#fff',
        border: 'none',
        borderRadius: '5px',
        padding: '10px 20px',
        cursor: 'pointer',
        fontSize: '16px'
    },
    formTagInput: {
        width: 'calc(100% - 30px)',
        padding: '10px',
        borderRadius: '5px',
        border: '1px #ccc',
        marginBottom: '10px'
    },
    tagContainer: {
        display: 'inline-flex',
        flexWrap: 'wrap',
        gap: '10px',
        marginBottom: '10px',
        alignItems: 'flex-start',
    },
    tag: {
        background: '#e1ecf4',
        borderRadius: '20px',
        padding: '5px 15px',
        fontSize: '14px',
        color: '#0074d9',
        display: 'inline-flex',
        alignItems: 'center',
        width: 'auto',
    },
    formTagButton: {
        backgroundColor: '#007bff',
        color: '#fff',
        border: 'none',
        borderRadius: '5px',
        padding: '5px 10px',
        cursor: 'pointer',
        fontSize: '16px',
        flexShrink: 0,
        alignItems: 'center',
        marginTop: 10,
    }, errorMessage: {
        backgroundColor: '#f8d7da',
        color: '#721c24',
        borderRadius: '5px',
        padding: '10px',
        fontSize: '18px',
        textAlign: 'center',
        width: '100%',
        marginBottom: '20px'
    },
    successMessage: {
        backgroundColor: '#d4edda',
        color: '#155724',
        borderRadius: '5px',
        padding: '10px',
        fontSize: '18px',
        textAlign: 'center',
        width: '100%',
        marginBottom: '20px'
    },
    deleteButtonStyle: {
        backgroundColor: 'red',
        color: 'white',
        border: 'none',
        borderRadius: '8px',
        padding: '15px 30px',
        fontSize: '20px',
        marginTop: '20px',
        cursor: 'pointer',
        lineHeight: '1', // Standardize line height

    },
    userInfoEditButton: {
        backgroundColor: '#1fafdb',
        color: 'white',
        border: 'none',
        borderRadius: '8px',
        padding: '15px 30px',
        fontSize: '20px',
        marginTop: '20px',
        cursor: 'pointer',
        marginRight: '10px', // Add margin to separate from delete button
        lineHeight: '1',
        right: '125px',
    },
    title: {
        fontSize: '40px',
        fontWeight: 'bold',
        textAlign: 'center',
        marginBottom: '40px',
        color: '#000000', // Title color
        textShadow: '2px 2px 4px rgba(0, 0, 0, 0.5)', // Text shadow for a subtle effect
        backgroundColor: '#f8f9fa', // Background color for contrast
        padding: '10px 20px', // Padding for better readability
        borderRadius: '8px', // Rounded corners for a modern look
    }, searchContainer: {
        backgroundColor: '#fff',
        padding: '20px',
        borderRadius: '8px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
        marginBottom: '20px',
        justifyContent: 'center',
        alignItems: 'center',
    },

    searchInputContainer: {
        marginBottom: '20px',
        display: 'flex',
        flexDirection: 'column',
        textAlign: 'center', // Center-align the input within its container
        alignItems: 'center',
        justifyContent: 'center',
    },
    searchRow: {
        display: 'flex',
        alignItems: 'center', // Center vertically
        marginBottom: '20px',
        width: '25%',
    },
    searchInput: {
        flex: 1,
        padding: '10px',
        borderRadius: '5px',
        border: '1px solid #ccc',
        marginRight: '10px',
        width: '20%',
        fontSize: '18px',
    },
    searchButton: {
        backgroundColor: '#007bff',
        color: '#fff',
        border: 'none',
        borderRadius: '5px',
        padding: '10px 20px',
        cursor: 'pointer',
        fontSize: '20px',
        marginTop: '10px',
    },
    searchLabel: {
        marginRight: '10px', // Add some space between the label and input
        fontSize: '22px',
        fontWeight: 'bold',
    },
    questionList: {
        marginTop: '20px',
    },
    noQuestionsText: {
        fontSize: '25px',
        color: '#666',
        textAlign: 'center',
        marginTop: '40px',
    },
    detailsContainer: {
        fontFamily: 'Arial, sans-serif',
        padding: '20px',
        margin: '20px 0',
        borderRadius: '8px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
        backgroundColor: '#fff',
    },
    answersContainer: {
        marginTop: '60px',
        border: '1px solid #ddd',
        borderRadius: '8px',
        padding: '10px',
    },
    subtitle: {
        fontWeight: 'bold',
        fontSize: '20px',
        color: '#444',
        marginBottom: '10px',
    },
    imageStyle: {
        maxHeight: '450px',
        width: 'auto',
        display: 'block',
        marginBottom: '10px',
        marginTop: '10px',
        borderRadius: '8px',
    },
    questionTags: {
        display: 'flex',
        flexWrap: 'wrap',
        gap: '10px',
        marginBottom: '20px',
        marginTop: '20px',
    },
    questionTag: {
        background: '#e1ecf4',
        borderRadius: '20px',
        padding: '6px 18px',
        fontSize: '24px',
        color: '#0074d9',
    },
    questionScore: {
        fontWeight: 'bold',
        color: '#108ee9',
        fontSize: '22px',
        marginBottom: '20px',
    },
    editQuestionButton: {
        backgroundColor: '#007bff',
        color: '#fff',
        border: 'none',
        borderRadius: '5px',
        padding: '10px 20px',
        cursor: 'pointer',
        fontSize: '20px'
    }

};

const userInfoStyles = {
    userInfoContainer: {
        backgroundColor: '#fff',
        padding: '20px',
        borderRadius: '8px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
        marginBottom: '20px',
    },
    userInfoTitle: {
        fontSize: '24px',
        fontWeight: 'bold',
        marginBottom: '10px',
        color: '#333',
    },
    userInfoLabel: {
        fontSize: '18px',
        fontWeight: 'bold',
        marginBottom: '5px',
        color: '#555',
    },
    userInfoInput: {
        width: '100%',
        padding: '10px',
        borderRadius: '5px',
        border: '1px solid #ccc',
        marginBottom: '10px',
    },
    userToBanInput: {
        flex: 1,
        padding: '10px',
        borderRadius: '5px',
        border: '1px solid #ccc',
        marginRight: '10px',
        width: '5%',
        fontSize: '20px',
    },
    userToBanRow: {
        display: 'flex',
        alignItems: 'center', // Center vertically
        marginBottom: '20px',
        width: '15%',
    },
    userBanContainer: {
        marginBottom: '20px',
        marginTop: '60px',
        display: 'flex',
        flexDirection: 'column',
        textAlign: 'center', // Center-align the input within its container
        alignItems: 'center',
        justifyContent: 'center',
    },
    banUserButton: {
        backgroundColor: 'red',
        color: '#fff',
        border: 'none',
        borderRadius: '5px',
        padding: '10px 20px',
        cursor: 'pointer',
        fontSize: '20px',
        fontWeight: 'bold',
        marginTop: '10px',
    }
};