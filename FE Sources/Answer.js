import React, {useState} from 'react';
import {downvoteAnswer, getAnswerByID, upvoteAnswer} from "./API";

function Answer({username, token, id, text, creationTime, pictureBase64, score, questionID, author, onDelete, onEdit, comesFromQuestionDetails }) {
    const[answerVoteState, setAnswerVoteState] = useState(0);
    const[errorMessage, setErrorMessage] = useState('');
    const[answerScore, setAnswerScore] = useState(score);

    const styles = {
        container: {
            fontFamily: 'Arial, sans-serif',
            border: '1px solid #ddd',
            borderRadius: '8px',
            padding: '20px',
            margin: '20px 0',
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
            backgroundColor: '#fff',
            position: 'relative',
        },
        text: {
            lineHeight: '1.6',
            color: '#444',
            marginBottom: '10px',
            marginLeft: '10px'
        },
        author: {
            color: '#0074d9',
            fontWeight: 'bold',
        },
        meta: {
            fontSize: '18px',
            color: '#666',
            marginBottom: '20px',
            marginTop: '20px'
        },
        score: {
            fontWeight: 'bold',
            color: '#108ee9',
            fontSize: '18px'
        },
        image: {
            maxHeight: '450px', // Set the desired height
            width: 'auto',      // Maintain aspect ratio
            display: 'block',
            margin: '0 auto 10px auto',
            borderRadius: '8px'
        },
        imagePlaceholder: {
            backgroundColor: '#f0f0f0',
            width: '100%',
            height: '200px',    // Ensure the same height as images
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            color: '#bbb',
            fontSize: '20px',
            marginBottom: '10px',
            borderRadius: '8px'
        },
        editButton: {
            position: 'absolute',
            bottom: '10px',
            right: '130px',
            backgroundColor: '#f1ac2d',
            color: 'white',
            border: 'none',
            borderRadius: '5px',
            padding: '10px 20px',
            cursor: 'pointer',
            fontSize: '20px',
            lineHeight: '1',
            display: author === username && comesFromQuestionDetails === true? 'block' : 'none',
        },
        deleteButton: {
            position: 'absolute',
            bottom: '10px',
            right: '10px',
            backgroundColor: 'red',
            color: 'white',
            border: 'none',
            borderRadius: '5px',
            padding: '10px 20px',
            cursor: 'pointer',
            fontSize: '20px',
            lineHeight: '1',
        },
        voteButtonsContainer: {
            position: 'absolute',
            top: '10px',
            right: '20px',
            display: author !== username ? 'block' : 'none',
            zIndex: '1', // Ensure it's above other content
        },
        upvoteButton: {
            backgroundColor: answerVoteState === 1 ? '#b5e18f' : '#dddddd',
            border: 'none',
            borderRadius: '50%',
            width: '60px',
            height: '60px',
            textAlign: 'center',
            lineHeight: '30px',
            cursor: 'pointer',
            marginBottom: '25px', // Adjusted margin for spacing between buttons
            display: 'block', // Ensure they appear one below the other
            fontSize: '24px',
        },
        downvoteButton: {
            backgroundColor: answerVoteState === -1 ? '#f8d7da' : '#ddd',
            border: 'none',
            borderRadius: '50%',
            width: '60px',
            height: '60px',
            textAlign: 'center',
            lineHeight: '30px',
            cursor: 'pointer',
            marginBottom: '25px', // Adjusted margin for spacing between buttons
            display: 'block', // Ensure they appear one below the other
            fontSize: '24px',
        },
        errorMessage: {
            backgroundColor: '#f8d7da',
            color: '#721c24',
            borderRadius: '5px',
            padding: '10px',
            fontSize: '18px',
            textAlign: 'center',
            width: '100%',
            marginBottom: '20px'
        },
    };

    creationTime = creationTime.replace('T', ' ');

    const handleUpvote = async () => {
        try {
            await upvoteAnswer(username, id, token);
            setAnswerVoteState(1);
            const answer = await getAnswerByID(username, id, token);
            setAnswerScore(answer.score)
            setErrorMessage('');
        }
        catch (error) {
            console.error('Error upvoting answer:', error);
            setErrorMessage('You already upvoted this answer');
            setTimeout(() => {
                setErrorMessage('');
            }, 5000);
        }
    }

    const handleDownvote = async () => {
        try {
            await downvoteAnswer(username, id, token);
            setAnswerVoteState(-1);
            const answer = await getAnswerByID(username, id, token);
            setAnswerScore(answer.score)
            setErrorMessage('');
        } catch (error) {
            console.error('Error downvoting answer:', error);
            setErrorMessage('You already downvoted this answer');
            setTimeout(() => {
                setErrorMessage('');
            }, 5000);
        }
    }


    return (
        <div style={styles.container}>

            {errorMessage && (
                <div style={styles.errorMessage}>
                    {errorMessage}
                </div>
            )}


            <div style={styles.voteButtonsContainer}>
                <button style={styles.upvoteButton} onClick={(e) => {
                    e.stopPropagation();
                    handleUpvote();
                }}>▲
                </button>
                <button style={styles.downvoteButton}  onClick={(e) => {
                    e.stopPropagation();
                    handleDownvote();
                }}>▼
                </button>
            </div>


            <div style={styles.text}>{text}</div>
            {pictureBase64 ? (
                <img src={`data:image/jpeg;base64,${pictureBase64}`} alt="Answer related" style={styles.image}/>
            ) : (
                <div style={styles.imagePlaceholder}>Picture Placeholder</div>
            )}
            <div style={styles.meta}>Answered by <span style={styles.author}>{author}</span> on {creationTime}</div>
            <div style={styles.score}>Score: {answerScore}</div>
            <button style={styles.editButton} onClick={onEdit}>✏️</button>
            <button style={styles.deleteButton} onClick={onDelete}>Delete</button>
        </div>
    );
}

export default Answer;
