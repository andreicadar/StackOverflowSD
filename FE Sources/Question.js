import React, { useState } from 'react';
import {downvoteQuestion, getQuestionByID, seeQuestionDetails, upvoteQuestion} from "./API";

function Question({username, token, id, author, title, text, creationTime, tags, score, onDelete, onEdit, pictureBase64, onPostAnswer, onSeeQuestionDetails, comesFromQuestionDetails }) {
    const [isClicked, setIsClicked] = useState(false);
    const [isHovered, setIsHovered] = useState(false);
    const [isHoveredAnotherButton, setIsHoveredAnotherButton] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [questionVoteState, setQuestionVoteState] = useState(0);
    const[internalScore, setInternalScore] = useState(score);


    const handleMouseDown = () => {
        setIsClicked(true);
    };

    const handleMouseUp = () => {
        setIsClicked(false);
    };

    const handleContainerHover = () => {
        if (!isHovered) {
            setIsHovered(true);
        }
    };

    const handleContainerLeave = () => {
        if (!isClicked) {
            setIsHovered(false);
        }
    };

    const styles = {
        container: {
            fontFamily: 'Arial, sans-serif',
            border: '1px solid #ddd',
            borderRadius: '8px',
            padding: '20px',
            margin: '20px 0',
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
            backgroundColor: isHovered && !isClicked && !isHoveredAnotherButton ? '#eaeaea' : isClicked && !isHoveredAnotherButton ? '#d1d1d1' : '#fff',
            position: 'relative',
            cursor: 'pointer',
            transition: 'background-color 0.2s ease',
        },
        header: {
            borderBottom: '1px solid #eee',
            paddingBottom: '10px',
            marginBottom: '20px',
        },
        title: {
            fontWeight: 'bold',
            fontSize: '24px',
            color: '#333',
        },
        meta: {
            fontSize: '18px',
            color: '#666',
            marginBottom: '10px',
        },
        author: {
            color: '#0074d9',
            fontWeight: 'bold',
            fontSize: '18px'
        },
        text: {
            lineHeight: '1.6',
            color: '#444',
            marginBottom: '10px',
        },
        tags: {
            display: 'flex',
            flexWrap: 'wrap',
            gap: '10px',
            marginBottom: '10px',
        },
        tag: {
            background: '#e1ecf4',
            borderRadius: '20px',
            padding: '5px 15px',
            fontSize: '18px',
            color: '#0074d9',
        },
        score: {
            fontWeight: 'bold',
            color: '#108ee9',
            fontSize: '18px',
        },
        image: {
            maxHeight: '450px', // Set the desired height
            width: 'auto',      // Maintain aspect ratio
            display: 'block',
            margin: '0 auto 10px auto',
            borderRadius: '8px',
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
            borderRadius: '8px',
        },
        answerButton: {
            position: 'absolute',
            bottom: '10px',
            right: author === username ? '220px' : '10px',
            backgroundColor: '#1170da',
            color: 'white',
            border: 'none',
            borderRadius: '5px',
            padding: '10px 20px',
            cursor: 'pointer',
            fontSize: '20px',
            lineHeight: '1',
            display: comesFromQuestionDetails === true ? 'block' : 'none',
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
            display: (author === username && comesFromQuestionDetails === true) ? 'block' : 'none',
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
            display: author === username ? 'block' : 'none',
        },
        voteButtonsContainer: {
            position: 'absolute',
            top: '10px',
            right: '20px',
            display: author !== username ? 'block' : 'none',
            zIndex: '1', // Ensure it's above other content
        },
        upvoteButton: {
            backgroundColor: questionVoteState === 1 ? '#b5e18f' : '#dddddd',
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
            backgroundColor: questionVoteState === -1 ? '#f8d7da' : '#ddd',
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

    const handleUpvote = async () => {
        try {
            console.log(id);
            console.log(username);
            console.log(token);

            await upvoteQuestion(username, id, token);

            setQuestionVoteState(1);
            const question = await getQuestionByID(username, id, token);
            setInternalScore(question.score)
            setErrorMessage('')
        }
        catch (error) {
            console.error('Error upvoting question:', error);
            setErrorMessage('You already upvoted this question');
            setTimeout(() => {
                setErrorMessage('');
            }, 5000);
        }

    };

    const handleDownvote = async () => {
        try {
            await downvoteQuestion(username, id, token);
            setQuestionVoteState(-1);
            const question = await getQuestionByID(username, id, token);
            setInternalScore(question.score)
            setErrorMessage('')
        } catch (error) {
            console.error('Error downvoting question:', error);
            setErrorMessage('You already downvoted this question');
            setTimeout(() => {
                setErrorMessage('');
            }, 5000);
        }
    };

    const tagsArray = tags ? tags.split(', ') : [];
    creationTime = creationTime.replace('T', ' ');

    return (
        <div
            style={styles.container}
            onMouseEnter={handleContainerHover}
            onMouseLeave={handleContainerLeave}
            onMouseDown={handleMouseDown}
            onMouseUp={handleMouseUp}
            onClick={() => onSeeQuestionDetails(id)}
        >

            {errorMessage && (
                <div style={styles.errorMessage}>
                    {errorMessage}
                </div>
            )}

            {/* Vote buttons */}
            <div style={styles.voteButtonsContainer}>
                <button style={styles.upvoteButton} onMouseEnter={() => setIsHoveredAnotherButton(true)}
                        onMouseLeave={() => setIsHoveredAnotherButton(false)} onClick={(e) => { e.stopPropagation(); handleUpvote(); }} >▲</button>
                <button style={styles.downvoteButton} onMouseEnter={() => setIsHoveredAnotherButton(true)}
                        onMouseLeave={() => setIsHoveredAnotherButton(false)} onClick={(e) => { e.stopPropagation(); handleDownvote(); }}>▼</button>
            </div>

            {/* Rest of the content */}
            <div style={styles.header}>
                <div style={styles.title}>{title}</div>
                <div style={styles.meta}>Asked by <span style={styles.author}>{author}</span> on {creationTime}</div>
            </div>
            {pictureBase64 ? (
                <img src={`data:image/jpeg;base64,${pictureBase64}`} alt="Question related" style={styles.image}/>
            ) : (
                <div style={styles.imagePlaceholder}>Picture Placeholder</div>
            )}
            <div style={styles.text}>{text}</div>
            <div style={styles.tags}>
                {tagsArray.map((tag, index) => (
                    <div key={index} style={styles.tag}>{tag}</div>
                ))}
            </div>
            <div style={styles.score}>Score: {internalScore}</div>

            <button style={styles.answerButton} onMouseEnter={() => setIsHoveredAnotherButton(true)}
                    onMouseLeave={() => setIsHoveredAnotherButton(false)} onClick={(e) => {
                e.stopPropagation();
                onPostAnswer(id);
            }}>Post an answer
            </button>

            <button style={styles.editButton} onMouseEnter={() => setIsHoveredAnotherButton(true)}
                    onMouseLeave={() => setIsHoveredAnotherButton(false)} onClick={(e) => {
                e.stopPropagation();
                onEdit();
            }}>✏️
            </button>

            <button style={styles.deleteButton} onMouseEnter={() => setIsHoveredAnotherButton(true)}
                    onMouseLeave={() => setIsHoveredAnotherButton(false)} onClick={(e) => {
                e.stopPropagation();
                onDelete();
            }}>Delete
            </button>
        </div>
    );
}

export default Question;
