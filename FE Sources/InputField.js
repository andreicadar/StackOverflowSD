import React from 'react';

const InputField = ({ type, placeholder, value, onChange }) => {
    return (
        <input
            type={type}
            placeholder={placeholder}
            value={value}
            onChange={onChange}
            style={styles.input}
        />
    );
};

const styles = {
    input: {
        display: 'block',
        width: '280px',
        padding: '10px 15px',
        margin: '10px 0 20px',
        fontSize: '16px',
        lineHeight: '20px',
        color: '#333',
        backgroundColor: '#fff',
        border: '2px solid #ddd',
        borderRadius: '4px',
        transition: 'border-color 0.3s',
    }
};
export default InputField;
