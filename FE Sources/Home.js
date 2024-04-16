import React, {useState} from 'react';
import {getQuestionsOfUser, getUserByUsername, postQuestion} from "./API";
import Question from "./Question";


function Home({ username, token }) {
    const [questions, setQuestions] = useState([]);
    const [errorMessage, setErrorMessage] = useState('');
    const [showQuestionForm, setShowQuestionForm] = useState(false); // State to toggle between question list and question form
    const [showQuestions, setShowQuestions] = useState(true);
    const [formData, setFormData] = useState({
        title: '',
        text: '',
        tags: [],
    });

    const [showUserInfo, setShowUserInfo] = useState(false); // State to toggle display of user info
    const [userInfo, setUserInfo] = useState(null); // State to store user information

    const [successMessage, setSuccessMessage] = useState('');

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
        setShowQuestionForm(false); // Hide question form
        setShowQuestions(false);
        setErrorMessage(''); // Clear error message
        setShowUserInfo(true); // Show user info
    };

    const handleDeleteUser = () => {

    };

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
            width: '200px', // Fixed width for the side menu
            background: '#f0f0f0', // A light grey background for the menu
            padding: '20px', // Padding around the menu items
            boxSizing: 'border-box', // Include padding in width calculation
            height: '100vh', // Full-height side menu
        },
        content: {
            flex: 1, // Takes up the remaining space
            padding: '20px' // Padding for the content area
        },
        button: {
            position: 'absolute',
            top: '5px',
            right: '5px',
            padding: '8px 16px',
            backgroundColor: '#007bff',
            color: 'white',
            border: 'none',
            borderRadius: '5px',
            cursor: 'pointer',
            fontSize: '16px',
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
            alignItems: 'flex-start', // Ensure flex items start from the top
        },
        tag: {
            background: '#e1ecf4',
            borderRadius: '20px',
            padding: '5px 15px',
            fontSize: '14px',
            color: '#0074d9',
            display: 'inline-flex', // Make tag container a flex container
            alignItems: 'center', // Align items vertically
            width: 'auto', // Allow the bubble to adjust its width based on content
        },
        formTagButton: {
            backgroundColor: '#007bff',
            color: '#fff',
            border: 'none',
            borderRadius: '5px',
            padding: '5px 10px',
            cursor: 'pointer',
            fontSize: '16px',
            flexShrink: 0, // Ensure button doesn't shrink
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
    };


    const fetchQuestions = async () => {
        try {
            const response = await getQuestionsOfUser(username, token);
            setQuestions(response);
            setShowQuestionForm(false); // Hide the question form when fetching questions
            setShowUserInfo(false); // Hide user info when fetching questions
            setShowQuestions(true); // Show the question list
        } catch (error) {
            setErrorMessage('Error fetching questions. Please try again later.')
        }
    };

    const handlePostQuestion = async (event) => {
        event.preventDefault();
        try {
            const { title, text, tags, image } = formData;
            await postQuestion(username, token, image, title, text, tags.join(', '));

            setFormData({ title: '', text: '', tags: [], image: null });
            setShowQuestionForm(false); // Hide the question form after posting
            fetchQuestions(); // Fetch updated question list
            setSuccessMessage('Question posted successfully.');
            setTimeout(() => {
                setSuccessMessage('');
            }, 3000); // Hide success message after 3 seconds
        } catch (error) {
            if (error.response && error.response.data && error.response.data.error) {
                setErrorMessage(error.response.data.error);
            } else {
                setErrorMessage('Error posting question. Please try again later.');
            }
        }
    };

    const handleTagChange = (e, index) => {
        const newTags = [...formData.tags];
        newTags[index] = e.target.value;
        setFormData({ ...formData, tags: newTags });
    };

    const handleAddTag = () => {
        if (formData.tags.length === 0 || formData.tags[formData.tags.length - 1].trim() !== '') {
            setFormData({ ...formData, tags: [...formData.tags, ''] });
        }
    };

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        setFormData({ ...formData, image: file });
    };

    function handleDeleteQuestion(id) {
        
    }

    return (
        <div style={styles.container}>
            <div style={styles.sideMenu}>
                <button style={styles.menuButton} onClick={fetchQuestions}>See My Questions</button>
                <button style={styles.menuButton} onClick={() => { setShowQuestionForm(true); setShowUserInfo(false); setShowQuestions(false)}}>Post a Question</button>
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
                        <div style={{textAlign: 'center'}}> {/* Aligns content center horizontally */}
                            <button style={styles.deleteButtonStyle} onClick={() => handleDeleteUser(userInfo.id)}>DELETE
                            </button>
                        </div>

                    </div>
                )}
                {showQuestions && (
                    questions.map(question => (
                        <Question key={question.id} {...question} onDelete={() => handleDeleteQuestion(question.id)} />
                    )))
                }
            </div>
        </div>
    );
}

export default Home;