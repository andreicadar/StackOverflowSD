import React, { useState } from 'react';
import { BrowserRouter as Router, Route } from 'react-router-dom';
import Login from './Login';
import Register from './Register';
import Home from './Home';

function App() {

    const setUsername = (newUsername) => {
        setUsername2(newUsername);
    };

    const setToken = (newToken) => {
        setTokenState(newToken);
    };

    const [username, setUsername2] = useState('');
    const [token, setTokenState] = useState('');

    return (
        <Router>
            {}
            {}
            <Route path="/home" element={<Home />} />
            <Route path="/login/*" element={<Login setUsername= {setUsername} setToken={setToken} />} />

        </Router>
    );
}

export default App;