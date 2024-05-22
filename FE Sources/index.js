import React, { useState } from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './Login';
import Register from './Register';
import Home from "./Home";

function App() {
    const [username, setUsername] = useState("");
    const [token, setToken] = useState("");

    return (
        <Router>
            <Routes>
                <Route path="/" element={<Navigate to="/login" />} />
                <Route path="/login" element={<Login setUsername={setUsername} setToken={setToken} />} />
                <Route path="/register" element={<Register />} />
                <Route path="/home" element={<Home usernameProps={username} tokenProps={token} />} />
            </Routes>
        </Router>
    );
}

ReactDOM.render(<App />, document.getElementById('root'));
