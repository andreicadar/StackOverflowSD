import React, { useState } from 'react';
import InputField from './InputField';
import { useNavigate } from 'react-router-dom';
import { register } from "./API";

function Register() {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const navigate = useNavigate();

    const handleRegister = async () => {
        const userData = {
            username: username,
            email: email,
            password: password,
            confirmPassword: confirmPassword
        };

        const jsonData = JSON.stringify(userData);

        if (password !== confirmPassword) {
            setErrorMessage('Passwords do not match');
            return;
        }

        try {
            const response = await register(jsonData);
            console.log(response);
            setSuccessMessage('Registration successful! Redirecting to login...');
            setTimeout(() => {
                navigate('/login');
            }, 3000); // Redirect after 3 seconds
        } catch (error) {
            console.error('Error registering:', error.message);
            if (error.response) {
                console.log('Status:', error.response.status);
                console.log('Data:', error.response.data);
                console.log('Headers:', error.response.headers);
                setErrorMessage("Error: " + error.response.data);
            } else if (error.request) {
                console.log('Request:', error.request);
                setErrorMessage('Oops! Something went wrong. Please check your internet connection.');
            } else {
                console.log('Error:', error.message);
                setErrorMessage('Oops! Something went wrong. Please try again later.');
            }
        }
    };

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
        button: {
            padding: '12px 24px', // Increased padding for the buttons
            fontSize: '18px', // Increased font size for the buttons
            border: 'none',
            borderRadius: '5px',
            cursor: 'pointer',
            marginBottom: '10px',
            width: '300px',
            boxSizing: 'border-box',
            boxShadow: '0px 4px 10px rgba(0, 0, 0, 0.1)',
            transition: 'transform 0.3s ease',
            background: '#FF6347', // Red color
            color: '#fff',
        },
        text: {
            marginBottom: '10px',
            fontSize: '14px',
        },errorMessage: {
            color: '#FF6347', // Light red color
            fontSize: '20px',
            textAlign: 'center',
            margin: '10px',
            width: '100%', // Set width to 100%
        },
        successMessage: {
            color: '#28a745', // Green color
            fontSize: '20px',
            textAlign: 'center',
            margin: '10px',
            width: '100%',
        },
    };

    // Merging button styles with common button styles
    styles.button = { ...styles.button };

    return (
        <div className="Register" style={styles.container}>
            <h1 style={styles.title}>Register</h1>
            <form onSubmit={(e) => e.preventDefault()} style={{ textAlign: 'center' }}>
                <InputField
                    type="text"
                    placeholder="Enter your username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
                <InputField
                    type="email"
                    placeholder="Enter your email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
                <InputField
                    type="password"
                    placeholder="Enter your password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <InputField
                    type="password"
                    placeholder="Confirm your password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                />
                <div style={{ height: errorMessage ? '24px' : '0' }} /> {/* Empty placeholder for error message */}
                {errorMessage && (
                    <div style={styles.errorMessage}>
                        {errorMessage}
                    </div>
                )}
                <div style={{ height: successMessage ? '24px' : '0' }} /> {/* Empty placeholder for success message */}
                {successMessage && (
                    <div style={styles.successMessage}>
                        {successMessage}
                    </div>
                )}
                <button style={styles.button} onClick={handleRegister}>
                    Register
                </button>
            </form>
        </div>
    );
}

export default Register;
