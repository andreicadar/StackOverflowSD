import React, { useState } from 'react';
import { BrowserRouter as Router, Route } from 'react-router-dom';
import Login from './Login';
import Register from './Register';
import Home from './Home';

function App() {
    // Custom function to update username state
    const setUsername = (newUsername) => {
        setUsername2(newUsername);
    };

    // Custom function to update token state
    const setToken = (newToken) => {
        setTokenState(newToken);
    };

    // State variables for username and token
    const [username, setUsername2] = useState('');
    const [token, setTokenState] = useState('');

    return (
        <Router>
            {/* Pass custom functions as props to child components */}
            {}
            <Route path="/home" element={<Home />} />
            <Route path="/login/*" element={<Login setUsername= {setUsername} setToken={setToken} />} />

        </Router>
    );
}

export default App;
