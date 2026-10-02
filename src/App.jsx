import { Routes, Route } from 'react-router-dom';
import LoginPage from './pages/LoginPage.jsx';
import RegisterPage from './pages/RegisterPage.jsx';
import ExpensesPage from './pages/ExpensesPage.jsx';
import DashboardPage from './pages/DashboardPage.jsx';
import BudgetsGoalsPage from './pages/BudgetsGoalsPage.jsx';
import Layout from './components/Layout.jsx';
import ProtectedRoute from './components/ProtectedRoute.jsx';
import './App.css';

function App() {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            <Route
                element={
                    <ProtectedRoute>
                        <Layout />
                    </ProtectedRoute>
                }
            >
                <Route path="/" element={<ExpensesPage />} />
                <Route path="/dashboard" element={<DashboardPage />} />
                <Route path="/budgets-goals" element={<BudgetsGoalsPage />} />
            </Route>
        </Routes>
    );
}

export default App;