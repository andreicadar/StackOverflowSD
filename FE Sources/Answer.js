import React from 'react';

function Answer({ text, creationTime, pictureBase64, score, questionID, author, onDelete, onEdit }) {
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
        meta: {
            fontSize: '18px',
            color: '#666',
            marginBottom: '20px',
            marginTop: '20px'
        },
        score: {
            fontWeight: 'bold',
            color: '#108ee9',
            fontSize: '16px'
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
            right: '125px',
            backgroundColor: '#f1ac2d',
            color: 'white',
            border: 'none',
            borderRadius: '5px',
            padding: '10px 20px',
            cursor: 'pointer',
            fontSize: '16px',
            lineHeight: '1',
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
            fontSize: '16px',
            lineHeight: '1',
        }
    };

    creationTime = creationTime.replace('T', ' ');

    return (
        <div style={styles.container}>
            <div style={styles.text}>{text}</div>
            {pictureBase64 ? (
                <img src={`data:image/jpeg;base64,${pictureBase64}`} alt="Answer related" style={styles.image}/>
            ) : (
                <div style={styles.imagePlaceholder}>Picture Placeholder</div>
            )}
            <div style={styles.meta}>Answered on {creationTime} by {author}</div>
            <div style={styles.score}>Score: {score}</div>
            <button style={styles.editButton} onClick={onEdit}>✏️</button>
            <button style={styles.deleteButton} onClick={onDelete}>DELETE</button>
        </div>
    );
}

export default Answer;
