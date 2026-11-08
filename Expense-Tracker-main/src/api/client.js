import axios from 'axios';

const client = axios.create({
    // CHANGED: Port 8080 to 8081
    baseURL: 'http://localhost:8081/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

client.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');

    if (token) {
        // CHANGED: Added backticks () so the token variable actually works
        config.headers.Authorization = Bearer ${token}`;
    }

    return config;
});

client.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            localStorage.removeItem('token');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default client;