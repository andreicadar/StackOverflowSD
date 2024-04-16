import React from 'react';

function Answer({ text, creationTime, picturePath, score, questionID, onDelete }) {

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
            marginBottom: '10px'
        },
        meta: {
            fontSize: '18px',
            color: '#666',
            marginBottom: '10px'
        },
        score: {
            fontWeight: 'bold',
            color: '#108ee9',
            fontSize: '16px'
        },
        image: {
            width: '100%',
            height: '200px',
            objectFit: 'cover',
            borderRadius: '8px',
            marginBottom: '10px',
        },
        deleteButton: {
            position: 'absolute',
            bottom: '10px',
            right: '10px',
            backgroundColor: 'red',
            color: 'white',
            border: 'none',
            borderRadius: '5px',
            padding: '8px 16px',
            cursor: 'pointer',
        }
    };

    creationTime = creationTime.replace('T', ' ');

    return (
        <div style={styles.container}>
            <div style={styles.text}>{text}</div>
            <div style={styles.meta}>Answered on {creationTime}</div>
            {picturePath && <img src={picturePath} alt="Answer" style={styles.image} />}
            <div style={styles.score}>Score: {score}</div>
            <div>Question ID: {questionID}</div>
            <button style={styles.deleteButton} onClick={onDelete}>DELETE</button>
        </div>
    );
}

export default Answer;