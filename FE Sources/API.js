import axios from 'axios';

const BASE_URL = 'http://127.0.0.1:8080/user';

export const login = async (data) => {
    try {
        const config = {
            headers: {
                'Content-Type': 'application/json'
            }
        };

        const response = await axios.post(`${BASE_URL}/login`, data, config);

        if (response.status !== 200) {
            throw new Error(`Failed to login. Status: ${response.status}`);
        }

        return response.data;
    } catch (error) {
        console.error('Error posting data:', error);
        throw error;
    }
};

export const register = async (data) => {
    try {
        const config = {
            headers: {
                'Content-Type': 'application/json'
            }
        };

        const response = await axios.post(`${BASE_URL}/register`, data, config);

        if (response.status !== 200) {
            throw new Error(`Failed to login. Status: ${response.status}`);
        }

        return response.data;
    } catch (error) {
        console.error('Error posting data:', error);
        throw error;
    }
};

export const getQuestionsOfUser = async (username, token) => {
    try {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Add Authorization header with token
            }
        };

        const response = await axios.get(`${BASE_URL}/getQuestionsOfUser?username=${username}`, config); // Add username parameter to the URL

        if (response.status !== 200) {
            throw new Error(`Failed to get questions. Status: ${response.status}`);
        }

        return response.data;
    } catch (error) {
        console.error('Error fetching questions:', error);
        throw error;
    }
};

export const postQuestion = async (username, token, image, title, text, tags) => {
    try {
        const formData = new FormData();
        formData.append('author', username);
        formData.append('title', title);
        formData.append('text', text);
        formData.append('tags', tags);

        // Append the image file if it exists
        if (image) {
            formData.append('image', image);
        }

        const config = {
            headers: {
                'Content-Type': 'multipart/form-data',
                'Authorization': `Bearer ${token}`
            }
        };

        const response = await axios.post(`${BASE_URL}/postQuestion`, formData, config);

        if (response.status !== 200) {
            throw new Error(`Failed to post question. Status: ${response.status}`);
        }

        return response.data;
    } catch (error) {
        console.error('Error posting question:', error);
        throw error;
    }
};

export const getUserByUsername = async (username, token) => {
    try {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Add Authorization header with token
            }
        };

        const response = await axios.get(`${BASE_URL}/getUserByUsername?username=${username}`, config); // Add username parameter to the URL

        if (response.status !== 200) {
            throw new Error(`Failed to get userInfo. Status: ${response.status}`);
        }

        return response.data;
    } catch (error) {
        console.error('Error fetching userInfo:', error);
        throw error;
    }
};

export const getAnswersOfUser = async (username, token) => {
    try {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Add Authorization header with token
            }
        };

        const response = await axios.get(`${BASE_URL}/getAnswersOfUser?username=${username}`, config); // Add username parameter to the URL

        if (response.status !== 200) {
            throw new Error(`Failed to get answers. Status: ${response.status}`);
        }

        return response.data;
    } catch (error) {
        console.error('Error fetching answers:', error);
        throw error;
    }
};