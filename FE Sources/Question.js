import React from 'react';

function Question({ author, title, text, creationTime, tags, score, onDelete }) {
    // Define styles for the component elements
    const styles = {
        container: {
            fontFamily: 'Arial, sans-serif',
            border: '1px solid #ddd',
            borderRadius: '8px',
            padding: '20px',
            margin: '20px 0',
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
            backgroundColor: '#fff',
            position: 'relative', // Required for absolute positioning of the delete button
        },
        header: {
            borderBottom: '1px solid #eee',
            paddingBottom: '10px',
            marginBottom: '20px', // Increased spacing
        },
        title: {
            fontWeight: 'bold',
            fontSize: '24px',
            color: '#333'
        },
        meta: {
            fontSize: '18px', // Increased font size
            color: '#666',
            marginBottom: '10px'
        },
        author: {
            color: '#0074d9' // Color the author blue
        },
        text: {
            lineHeight: '1.6',
            color: '#444',
            marginBottom: '10px'
        },
        tags: {
            display: 'flex',
            flexWrap: 'wrap',
            gap: '10px',
            marginBottom: '10px'
        },
        tag: {
            background: '#e1ecf4',
            borderRadius: '20px',
            padding: '5px 15px',
            fontSize: '14px',
            color: '#0074d9'
        },
        score: {
            fontWeight: 'bold',
            color: '#108ee9',
            fontSize: '16px'
        },
        imagePlaceholder: {
            backgroundColor: '#f0f0f0',
            width: '100%',
            height: '200px',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            color: '#bbb',
            fontSize: '20px',
            marginBottom: '10px',
            borderRadius: '8px'
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

    const tagsArray = tags ? tags.split(', ') : [];
    creationTime = creationTime.replace('T', ' ');

    return (
        <div style={styles.container}>
            <div style={styles.header}>
                <div style={styles.title}>{title}</div>
                <div style={styles.meta}>Asked by <span style={styles.author}>{author}</span> on {creationTime}</div>
            </div>
            <div style={styles.imagePlaceholder}>
                Picture Placeholder
            </div>
            <div style={styles.text}>{text}</div>
            <div style={styles.tags}>
                {tagsArray.map((tag, index) => (
                    <div key={index} style={styles.tag}>{tag}</div>
                ))}
            </div>
            <div style={styles.score}>Score: {score}</div>
            <button style={styles.deleteButton} onClick={onDelete}>DELETE</button>
        </div>
    );
}

export default Question;
