import React, { useState } from 'react';
import { Link, Routes, Route, Navigate, useNavigate } from 'react-router-dom'; // Added useNavigate
import Register from './Register';
import InputField from './InputField';
import { login } from './API';
import Home from "./Home";

function Login({setUsername, setToken }) {
    const [username, setUsernameLocal] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const navigate = useNavigate(); // Use useNavigate hook to navigate

    const handleLogin = async () => {
        const userData = {
            username: username,
            password: password
        };

        const jsonData = JSON.stringify(userData);

        try {
            const response = await login(jsonData);
            console.log(response);
            setUsername(username); // Assuming your response has a username field
            setToken(response);
            navigate('/home');

            // Here you would typically handle successful authentication
        } catch (error) {
            console.error('Error logging in:', error.message);
            // Display a nice error message to the user
            if (error.response) {
                // The request was made and the server responded with a status code
                if (error.response.status === 403) {
                    setErrorMessage('Invalid credentials. Please try again.');
                } else {
                    // Handle other error codes
                    console.log('Status:', error.response.status);
                    console.log('Data:', error.response.data);
                    console.log('Headers:', error.response.headers);
                    // Display generic error message to the user
                    setErrorMessage('Oops! There was an error. Please try again later.');
                }
            } else if (error.request) {
                // The request was made but no response was received
                console.log('Request:', error.request);
                // Display error message to the user
                setErrorMessage('Oops! Something went wrong. Please check your internet connection.');
            } else {
                // Something happened in setting up the request that triggered an Error
                console.log('Error:', error.message);
                // Display error message to the user
                setErrorMessage('Oops! Something went wrong. Please try again later.');
            }
        }
    };



    return (
        <div className="App" style={styles.container}>
            <Routes>
                <Route path="/register" element={<Register />} />
            </Routes>
            <h1 style={styles.title}>StackOverflow</h1>
            <form onSubmit={(e) => e.preventDefault()} style={styles.form}>
                <InputField
                    type="text"
                    placeholder="Enter your username"
                    value={username}
                    onChange={(e) => { setUsernameLocal(e.target.value); setErrorMessage(null); }}
                />
                <InputField
                    type="password"
                    placeholder="Enter your password"
                    value={password}
                    onChange={(e) => { setPassword(e.target.value); setErrorMessage(null); }}
                />
                {errorMessage && (
                    <div style={styles.errorMessage}>
                        {errorMessage}
                    </div>
                )}
                <button style={styles.redButton} onClick={handleLogin}>
                    Login
                </button>
                <p style={styles.text}>Don't have an account yet?</p>
                <Link to="/register">
                    <button style={styles.blueButton}>
                        Register
                    </button>
                </Link>
            </form>
        </div>
    );
}

export default Login;

const styles = {
    container: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '100vh',
    },
    title: {
        color: 'orange',
        marginBottom: '40px',
        fontSize: '40px', // Increased font size for the title
    },
    form: {
        width: '300px', // Set width of the form
        textAlign: 'center',
    },
    button: {
        padding: '12px 24px', // Increased padding for the buttons
        fontSize: '18px', // Increased font size for the buttons
        border: 'none',
        borderRadius: '5px',
        cursor: 'pointer',
        marginBottom: '10px',
        boxSizing: 'border-box',
        boxShadow: '0px 4px 10px rgba(0, 0, 0, 0.1)',
        transition: 'transform 0.3s ease',
    },
    redButton: {
        background: '#FF6347', // Red color
        color: '#fff',
    },
    blueButton: {
        background: '#4682B4', // Blue color
        color: '#fff',
    },
    text: {
        marginBottom: '10px',
        fontSize: '18px', // Increased font size
        color: '#555', // Dark gray color
        fontStyle: 'italic', // Italic font style
        textAlign: 'center', // Centered alignment
    },
    errorMessage: {
        color: '#FF6347', // Light red color
        fontSize: '20px',
        textAlign: 'center',
        margin: '10px',
        width: '100%', // Set width to 100%
    },
};

// Merging button styles with common button styles
styles.redButton = { ...styles.button, ...styles.redButton };
styles.blueButton = { ...styles.button, ...styles.blueButton };
