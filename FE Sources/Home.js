import React, {useEffect, useState} from 'react';
import {
    getAnswersOfUser,
    getQuestionsOfUser,
    getUserByUsername,
    postAnswerToQuestion,
    postQuestion,
    searchQuestions
} from "./API";
import Question from "./Question";
import Answer from "./Answer";
import * as PropTypes from "prop-types";
import {Navigate} from "react-router-dom";

function Redirect(props) {
    return null;
}

Redirect.propTypes = {to: PropTypes.string};

function Home({ username, token }) {
    const [questions, setQuestions] = useState([]);
    const [answers, setAnswers] = useState([]);
    const [errorMessage, setErrorMessage] = useState('');
    const [showQuestionForm, setShowQuestionForm] = useState(false);
    const [showAnswersForm, setShowAnswersForm] = useState(false);
    const [showQuestions, setShowQuestions] = useState(true);
    const [showAnswers, setShowAnswers] = useState(false);
    const [questionIDToAnswer, setQuestionIDToAnswer] = useState(null);
    const [showSearchQuestions, setShowSearchQuestions] = useState(false);
    const [titleToSearchFor, setTitleToSearchFor] = useState('');
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
        }
        else {
            // Fetch questions when username or token changes
            fetchQuestions().then(r => console.log('Questions fetched')).catch(e => console.error('Error fetching questions:', e));
        }
    }, [username, token]);

    if (redirectToLogin) {
        console.log("Redirecting to login...");

        return <Navigate to="/login" />;
    }

    const fetchUserInformation = async () => {
        try {
            const userData = await getUserByUsername(username, token);
            setUserInfo(userData);
        } catch (error) {
            setErrorMessage('Error fetching user information. Please try again later.');
        }
    };

    const handleUsernameClick = () => {
        fetchUserInformation();
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
            const response = await searchQuestions(titleToSearchFor, token);
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

    const handlePostQuestion = async (event) => {
        event.preventDefault();
        try {
            const { title, text, tags, image } = questionFormData;
            await postQuestion(username, token, image, title, text, tags.join(', '));

            setQuestionFormData({ title: '', text: '', tags: [], image: null });
            setSuccessMessage('Question posted successfully.');
            setTimeout(() => {
                setSuccessMessage('');
            }, 5000);
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
        setQuestionFormData({ ...questionFormData, tags: newTags });
    };

    const handleAddTag = () => {
        if (questionFormData.tags.length === 0 || questionFormData.tags[questionFormData.tags.length - 1].trim() !== '') {
            setQuestionFormData({ ...questionFormData, tags: [...questionFormData.tags, ''] });
        }
    };

    const handleImageChangeQuestionForm = (e) => {
        const file = e.target.files[0];
        setQuestionFormData({ ...questionFormData, image: file });
    };

    const handleImageChangeAnswerForm = (e) => {
        const file = e.target.files[0];
        setAnswerFormData({ ...answerFormData, image: file });
    };


    const fetchAnswers = async () => {

        try {

            const userAnswers = await getAnswersOfUser(username, token);
            setShowSearchQuestions(false);
            setAnswers(userAnswers);
            setShowAnswers(true);
            setShowQuestions(false);
            setShowAnswersForm(false);
            setShowQuestionForm(false);
            setShowUserInfo(false);

        } catch (error) {
            setErrorMessage('Error fetching answers. Please try again later.');
        }
    };

    const handleSeeMyAnswers = async () => {
        setShowSearchQuestions(false);
        setShowQuestionForm(false);
        setShowUserInfo(false);
        setShowQuestionForm(false);
        setShowAnswers(true);
        setErrorMessage('');
        await fetchAnswers();
    };
    function handleUserEdit() {

    }

    function handleUserDelete()
    {

    }

    function handleAnswerEdit(id) {
    }

    function handleAnswerDelete(id) {

    }

    function handleQuestionEdit(id) {

    }

    function handleQuestionDelete(id) {

    }


    async function handlePostAnswer(event) {
        event.preventDefault();
        try {
            const {text, image} = answerFormData;
            await postAnswerToQuestion(username, token, image, questionIDToAnswer, text);
            setAnswerFormData({text: '', image: null});
            setSuccessMessage('Answer posted successfully.');
            setTimeout(() => {
                setSuccessMessage('');
            }, 5000);
            await fetchAnswers();

        }
        catch (error) {
            if (error.response && error.response.data && error.response.data.error) {
                setErrorMessage(error.response.data.error);
            } else {
                setErrorMessage('Error posting answer. Please try again later.');
            }
        }

    }

    function handlePostAnAnswerButton(id)
    {
        setQuestionIDToAnswer(id);
        setShowSearchQuestions(false);
        setShowQuestionForm(false);
        setShowAnswers(false);
        setShowUserInfo(false);
        setShowQuestions(false)
        setShowAnswersForm(true);
    }

    function handlePostAQuestionButton() {
        setShowSearchQuestions(false);
        setShowQuestionForm(true);
        setShowAnswers(false);
        setShowUserInfo(false);
        setShowQuestions(false);
        setShowAnswersForm(false);
    }

    function handleSearchQuestionsButton() {
        setShowSearchQuestions(true);
        setShowQuestionForm(false);
        setShowAnswers(false);
        setShowUserInfo(false);
        setShowQuestions(false);
        setShowAnswersForm(false);
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

                            <button style={styles.deleteButtonStyle} onClick={() => handleUserEdit()}>DELETE</button>

                        </div>

                    </div>
                )}

                {showSearchQuestions && (
                    <div style={styles.formContainer}>
                        <h2>Search Questions</h2>
                        <input style={styles.formInput} type="text" placeholder="Title to search for" value={titleToSearchFor} onChange={(e) => setTitleToSearchFor(e.target.value)} />
                        <button style={styles.formButton} onClick={fetchSearchedQuestions}>Search</button>
                        {questions.map(question => (
                        <Question
                            key={question.id}
                            onPostAnswer={handlePostAnAnswerButton}
                            {...question}
                            onDelete={() => handleQuestionDelete(question.id)}
                            onEdit={() => handleQuestionEdit(question.id)}
                        />
                        ))}
                    </div>
                )}


                {showQuestionForm && (
                    <div style={styles.formContainer}>
                        <h2>Post a Question</h2>
                        <form onSubmit={handlePostQuestion}>
                            <label style={styles.formLabel}>Title:</label>
                            <input style={styles.formInput} type="text" value={questionFormData.title} onChange={(e) => setQuestionFormData({ ...questionFormData, title: e.target.value })} /><br />
                            <label style={styles.formLabel}>Text:</label>
                            <textarea style={styles.formTextarea} value={questionFormData.text} onChange={(e) => setQuestionFormData({ ...questionFormData, text: e.target.value })}></textarea><br />
                            <label style={styles.formLabel}>Tags:</label>
                            <div style={styles.tagContainer}>
                                {questionFormData.tags.map((tag, index) => (
                                    <div key={index} style={styles.tag}>
                                        <input style={styles.formTagInput} type="text" value={tag} onChange={(e) => handleTagChange(e, index)} />
                                    </div>
                                ))}
                                <button style={styles.formTagButton} type="button" onClick={handleAddTag}>+</button>
                            </div>
                            <label style={styles.formLabel}>Image:</label>
                            <input style={styles.formInput} type="file" accept="image/*" onChange={handleImageChangeQuestionForm} /><br />
                            <button style={styles.formButton} type="submit">Post Question</button>
                        </form>
                    </div>)}
                {showQuestions && (
                    <div>
                        <h1 style={styles.title}>My Questions</h1>
                        {questions.map(question => (
                            <Question
                                key={question.id}
                                onPostAnswer={handlePostAnAnswerButton}
                                {...question}
                                onDelete={() => handleQuestionDelete(question.id)}
                                onEdit={() => handleQuestionEdit(question.id)}
                            />
                        ))}
                    </div>
                )}
                {showAnswersForm && (
                    <div style={styles.formContainer}>
                        <h2>Post an Answer</h2>
                        <form onSubmit={handlePostAnswer}>
                            <label style={styles.formLabel}>Text:</label>
                            <textarea style={styles.formTextarea} value={answerFormData.text} onChange={(e) => setAnswerFormData({ ...answerFormData, text: e.target.value })}></textarea><br />
                            <label style={styles.formLabel}>Image:</label>
                            <input style={styles.formInput} type="file" accept="image/*" onChange={handleImageChangeAnswerForm} /><br />
                            <button style={styles.formButton} type="submit">Post Answer</button>
                        </form>
                    </div>)}
                {showAnswers && (
                    <div>
                        <h1 style={styles.title}>My Answers</h1>
                        {answers.map(answer => (
                            <Answer key={answer.id} onDelete={handleAnswerDelete(answer.id)}
                                    onEdit={handleAnswerDelete(answer.id)} {...answer} />
                        ))}
                    </div>
                )}
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
        marginBottom: '10px'
    },
    formTextarea: {
        width: '100%',
        padding: '10px',
        borderRadius: '5px',
        border: '1px solid #ccc',
        marginBottom: '10px',
        resize: 'none',
        minHeight: '100px'
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
        marginTop:10,
    },errorMessage: {
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
    deleteButtonStyle :{
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
    },

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
};