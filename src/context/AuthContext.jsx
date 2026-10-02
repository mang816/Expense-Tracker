import { createContext, useContext, useState } from 'react';
import * as authApi from '../api/auth';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [token, setToken] = useState(localStorage.getItem('token'));

    const isAuthenticated = !!token;

    async function loginUser(username, password) {
        const data = await authApi.login(username, password);
        localStorage.setItem('token', data.token);
        setToken(data.token);
    }

    async function registerUser(username, password) {
        const data = await authApi.register(username, password);
        localStorage.setItem('token', data.token);
        setToken(data.token);
    }

    function logout() {
        localStorage.removeItem('token');
        setToken(null);
    }

    const value = {
        token,
        isAuthenticated,
        loginUser,
        registerUser,
        logout,
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
    const context = useContext(AuthContext);

    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }

    return context;
}