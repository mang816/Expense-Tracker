import { useEffect, useState } from 'react';
import { getSummary } from '../api/reports.js';
import { getCategories } from '../api/categories.js';
import CategoryChart from '../components/CategoryChart.jsx';

export default function DashboardPage() {
    const { logout } = useAuth();
    const [summary, setSummary] = useState(null);
    const [categories, setCategories] = useState({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {
        setLoading(true);
        setError('');
        try {
            const [summaryData, categoriesData] = await Promise.all([
                getSummary(),
                getCategories(),
            ]);
            setSummary(summaryData);
            setCategories(categoriesData);
        } catch (err) {
            setError('Could not load dashboard data.');
        } finally {
            setLoading(false);
        }
    }

    function formatMoney(value) {
        return '₦' + Number(value || 0).toLocaleString(undefined, { minimumFractionDigits: 2 });
    }

    return (
        <div className="page-container">

                        <h1 className="page-title">Dashboard</h1>

            {loading && <p>Loading...</p>}
            {error && <p className="error">{error}</p>}

            {summary && (
                <>
                    <div className="dashboard-grid">
                        <div className="total-card">
                            <h2>Total Expenses</h2>
                            <div className="total-amount">{formatMoney(summary.total)}</div>
                        </div>

                        <div className="total-card">
                            <h2>Number of Expenses</h2>
                            <div className="total-amount">{summary.count}</div>
                        </div>

                        <div className="total-card">
                            <h2>Average Expense</h2>
                            <div className="total-amount">{formatMoney(summary.average)}</div>
                        </div>
                    </div>

                    <div className="section-card">
                        <h2>Category Summary</h2>

                        {Object.keys(summary.categoryTotals).length === 0 && (
                            <div className="empty-state">
                                <p>No category data yet.</p>
                            </div>
                        )}

                        {Object.entries(summary.categoryTotals).map(([category, amount]) => (
                            <div className="plain-list-row" key={category}>
                                <span>{categories[category]} {category}</span>
                                <span>{formatMoney(amount)}</span>
                            </div>
                        ))}

                        <CategoryChart categoryTotals={summary.categoryTotals} />
                    </div>

                    <div className="section-card">
                        <h2>Monthly Summary</h2>

                        {Object.keys(summary.monthlyTotals).length === 0 && (
                            <div className="empty-state">
                                <p>No monthly data yet.</p>
                            </div>
                        )}

                        {Object.entries(summary.monthlyTotals).map(([month, amount]) => (
                            <div className="plain-list-row" key={month}>
                                <span>{month}</span>
                                <span>{formatMoney(amount)}</span>
                            </div>
                        ))}
                    </div>
                </>
            )}

        </div>
    );
}