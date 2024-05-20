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

export const postAnswerToQuestion = async (username, token, image, questionID, text) => {
    try {
        const formData = new FormData();
        formData.append('author', username);
        formData.append('questionID', questionID);
        formData.append('text', text);

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

        const response = await axios.post(`${BASE_URL}/answerQuestion`, formData, config);

        if (response.status !== 200) {
            throw new Error(`Failed to post answer. Status: ${response.status}`);
        }

        return response.data;
    } catch (error) {
        console.error('Error posting answer:', error);
        throw error;
    }
}

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

export const searchQuestions = async (username, titleQuery, tagQuery, userQuery, token) => {
    try {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Add Authorization header with token
            }
        };
        let url;
        url = `${BASE_URL}/searchQuestions?username=${username}`;
        if(titleQuery) {
            url+=`&title=${titleQuery}`;
        }
        if(tagQuery) {
            url+=`&tags=${tagQuery}`;
        }
        if(userQuery) {
            url+=`&author=${userQuery}`;
        }
        const response = await axios.get(url, config);
        //const response = await axios.get(`${BASE_URL}/searchQuestions?username=${username}&title=${titleQuery}`, config); // Add titleQuery parameter to the URL

        if (response.status !== 200) {
            throw new Error(`Failed to search questions. Status: ${response.status}`);
        }

        return response.data;
    }
    catch (error) {
        console.error('Error searching questions:', error);
        throw error;
    }

}

export const seeQuestionDetails = async (username, questionID, token) => {

    try {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Add Authorization header with token
            }
        };
        const response = await axios.get(`${BASE_URL}/getQuestionDetails?username=${username}&questionID=${questionID}`, config); // Add questionID parameter to the URL

        if (response.status !== 200) {
            throw new Error(`Failed to see question details. Status: ${response.status}`);
        }

        return response.data;
    }
    catch (error) {
        console.error('Error seeing question details:', error);
        throw error;
    }
}

export const upvoteQuestion = async (username, questionID, token) => {
    try {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Add Authorization header with token
            }
        };
        console.log()
        const response = await axios.get(`${BASE_URL}/upvoteQuestion?username=${username}&questionID=${questionID}`, config);
        console.log(response.data);
        if (response.status !== 200) {
            throw new Error(`Failed to upvote question. Status: ${response.status}`);
        }

        return response.data;
    }
    catch (error) {
        console.error('Error upvoting question:', error);
        throw error;
    }
}

export const downvoteQuestion = async (username, questionID, token) => {
    try {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Add Authorization header with token
            }
        };
        const response = await axios.get(`${BASE_URL}/downvoteQuestion?username=${username}&questionID=${questionID}`, config);

        if (response.status !== 200) {
            throw new Error(`Failed to downvote question. Status: ${response.status}`);
        }

        return response.data;
    }
    catch (error) {
        console.error('Error downvoting question:', error);
        throw error;
    }
}

export const getQuestionByID = async (username, questionID, token) => {
    try {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Add Authorization header with token
            }
        };
        const response = await axios.get(`${BASE_URL}/getQuestionByID?username=${username}&questionID=${questionID}`, config);

        if (response.status !== 200) {
            throw new Error(`Failed to get question. Status: ${response.status}`);
        }

        return response.data;
    }
    catch (error) {
        console.error('Error getting question:', error);
        throw error;
    }
}

export const upvoteAnswer = async (username, answerID, token) => {
    try {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Add Authorization header with token
            }
        };
        const response = await axios.get(`${BASE_URL}/upvoteAnswer?username=${username}&answerID=${answerID}`, config);

        if (response.status !== 200) {
            throw new Error(`Failed to upvote answer. Status: ${response.status}`);
        }

        return response.data;
    } catch (error) {
        console.error('Error upvoting answer:', error);
        throw error;
    }
}

export const downvoteAnswer = async (username, answerID, token) => {
    try {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Add Authorization header with token
            }
        };
        const response = await axios.get(`${BASE_URL}/downvoteAnswer?username=${username}&answerID=${answerID}`, config);

        if (response.status !== 200) {
            throw new Error(`Failed to downvote answer. Status: ${response.status}`);
        }

        return response.data;
    } catch (error) {
        console.error('Error downvoting answer:', error);
        throw error;
    }
}

export const getAnswerByID = async (username, answerID, token) => {
    try {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Add Authorization header with token
            }
        };
        const response = await axios.get(`${BASE_URL}/getAnswerByID?username=${username}&answerID=${answerID}`, config);

        if (response.status !== 200) {
            throw new Error(`Failed to get question. Status: ${response.status}`);
        }

        return response.data;
    }
    catch (error) {
        console.error('Error getting question:', error);
        throw error;
    }
}

export const deleteAnswer = async (username, answerID, token) => {
    try {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Add Authorization header with token
            }
        };
        const response = await axios.get(`${BASE_URL}/deleteAnswer?username=${username}&answerID=${answerID}`, config);

        if (response.status !== 200) {
            throw new Error(`Failed to delete answer. Status: ${response.status}`);
        }

        return response.data;
        }
    catch (error) {
        console.error('Error deleting answer:', error);
        throw error;
    }
}

export const deleteQuestion = async (username, questionID, token) => {
    try {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Add Authorization header with token
            }
        };
        const response = await axios.get(`${BASE_URL}/deleteQuestion?username=${username}&questionID=${questionID}`, config);

        if (response.status !== 200) {
            throw new Error(`Failed to delete question. Status: ${response.status}`);
        }

        return response.data;
    }
    catch (error) {
        console.error('Error deleting question:', error);
        throw error;
    }
}

export const deleteUser = async (username, token) => {
    try {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Add Authorization header with token
            }
        };
        const response = await axios.get(`${BASE_URL}/deleteUser?username=${username}`, config);

        if (response.status !== 200) {
            throw new Error(`Failed to delete user. Status: ${response.status}`);
        }

        return response.data;
    }
    catch (error) {
        console.error('Error deleting user:', error);
        throw error;
    }
}

export const updateQuestion = async (author, token, questionID, title, text) => {
    try {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Add Authorization header with token
            }
        };
        const response = await axios.get(`${BASE_URL}/updateQuestion?author=${author}&questionID=${questionID}&title=${title}&text=${text}`, config);

        if (response.status !== 200) {
            throw new Error(`Failed to update question. Status: ${response.status}`);
        }

        return response.data;

    }
    catch (error) {
        console.error('Error updating question:', error);
        throw error;
    }
}

export const updateAnswer = async (username, token, answerID, text) => {
    try {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Add Authorization header with token
            }
        };
        const response = await axios.get(`${BASE_URL}/updateAnswer?username=${username}&answerID=${answerID}&text=${text}`, config);

        if (response.status !== 200) {
            throw new Error(`Failed to update answer. Status: ${response.status}`);
        }

        return response.data;

    }
    catch (error) {
        console.error('Error updating answer:', error);
        throw error;
    }
}

export const banUser = async (username, token, userToBan) => {
    try {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Add Authorization header with token
            }
        };
        const response = await axios.get(`${BASE_URL}/banUser?username=${username}&userToBan=${userToBan}`, config);

        if (response.status !== 200) {
            throw new Error(`Failed to ban user. Status: ${response.status}`);
        }

        return response.data;
    }
    catch (error) {
        console.error('Error banning user:', error);
        throw error;
    }
}
