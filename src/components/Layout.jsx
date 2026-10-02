import { NavLink, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';

export default function Layout() {
    const { logout } = useAuth();

    return (
        <div className="app-shell">
            <aside className="sidebar">
                <div className="sidebar-header">
                    <span className="sidebar-logo">💰</span>
                    <span className="sidebar-title">Finance Tracker</span>
                </div>

                <nav className="sidebar-nav">
                    <NavLink to="/" end className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
                        <span className="nav-icon">⬆️</span> Expenses
                    </NavLink>
                    <NavLink to="/dashboard" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
                        <span className="nav-icon">📊</span> Dashboard
                    </NavLink>
                    <NavLink to="/budgets-goals" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
                        <span className="nav-icon">🎯</span> Budgets &amp; Goals
                    </NavLink>
                </nav>

                <div className="sidebar-footer">
                    <button className="logout-btn" onClick={logout}>🚪 Log Out</button>
                </div>
            </aside>

            <main className="main-content">
                <Outlet />
            </main>
        </div>
    );
}