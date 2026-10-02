import { useEffect, useState } from 'react';
import { getBudgets, setBudget } from '../api/budgets.js';
import { getGoals, createGoal, contributeToGoal, deleteGoal } from '../api/goals.js';
import { getCategories } from '../api/categories.js';

export default function BudgetsGoalsPage() {
    const { logout } = useAuth();
    const [budgets, setBudgets] = useState([]);
    const [goals, setGoals] = useState([]);
    const [categories, setCategories] = useState({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const [budgetForm, setBudgetForm] = useState({ category: '', limitAmount: '' });
    const [goalForm, setGoalForm] = useState({ name: '', targetAmount: '', targetDate: '', category: '' });
    const [contributions, setContributions] = useState({});

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {
        setLoading(true);
        setError('');
        try {
            const [budgetsData, goalsData, categoriesData] = await Promise.all([
                getBudgets(),
                getGoals(),
                getCategories(),
            ]);
            setBudgets(budgetsData);
            setGoals(goalsData);
            setCategories(categoriesData);

            const firstCategory = Object.keys(categoriesData)[0] || '';
            setBudgetForm((prev) => ({ ...prev, category: prev.category || firstCategory }));
        } catch (err) {
            setError('Could not load budgets and goals.');
        } finally {
            setLoading(false);
        }
    }

    function formatMoney(value) {
        return '₦' + Number(value || 0).toLocaleString(undefined, { minimumFractionDigits: 2 });
    }

    async function handleBudgetSubmit(e) {
        e.preventDefault();
        try {
            await setBudget(budgetForm.category, parseFloat(budgetForm.limitAmount));
            setBudgetForm((prev) => ({ ...prev, limitAmount: '' }));
            loadData();
        } catch (err) {
            setError('Could not save budget.');
        }
    }

    async function handleGoalSubmit(e) {
        e.preventDefault();
        try {
            await createGoal({
                name: goalForm.name,
                targetAmount: parseFloat(goalForm.targetAmount),
                targetDate: goalForm.targetDate || null,
                category: goalForm.category || null,
            });
            setGoalForm({ name: '', targetAmount: '', targetDate: '', category: '' });
            loadData();
        } catch (err) {
            setError('Could not create goal.');
        }
    }

    async function handleContribute(id) {
        const amount = parseFloat(contributions[id]);
        if (!amount) return;

        try {
            await contributeToGoal(id, amount);
            setContributions((prev) => ({ ...prev, [id]: '' }));
            loadData();
        } catch (err) {
            setError('Could not add contribution.');
        }
    }

    async function handleDeleteGoal(id) {
        if (!window.confirm('Delete this savings goal?')) return;

        try {
            await deleteGoal(id);
            loadData();
        } catch (err) {
            setError('Could not delete goal.');
        }
    }

    return (
        <div className="page-container">

                        <h1 className="page-title">Budgets & Goals</h1>

            {loading && <p>Loading...</p>}
            {error && <p className="error">{error}</p>}

            <div className="section-card">
                <h2>Budgets</h2>

                <form onSubmit={handleBudgetSubmit} className="inline-form">
                    <select
                        value={budgetForm.category}
                        onChange={(e) => setBudgetForm((prev) => ({ ...prev, category: e.target.value }))}
                    >
                        {Object.entries(categories).map(([name, icon]) => (
                            <option key={name} value={name}>{icon} {name}</option>
                        ))}
                    </select>

                    <input
                        type="number"
                        placeholder="Limit e.g. 50000"
                        step="0.01"
                        value={budgetForm.limitAmount}
                        onChange={(e) => setBudgetForm((prev) => ({ ...prev, limitAmount: e.target.value }))}
                        required
                    />

                    <button type="submit">Set Budget</button>
                </form>

                {budgets.length === 0 && (
                    <div className="empty-state">
                        <p>No budgets set yet.</p>
                    </div>
                )}

                <div className="budget-grid">
                    {budgets.map((b) => (
                        <div className="budget-card-item" key={b.category}>
                            <div className="budget-card-header">
                                <span>{categories[b.category]}</span>
                                <span className="budget-category-name">{b.category}</span>
                            </div>
                            <div className={`budget-remaining ${b.percentUsed >= 100 ? 'over-budget-text' : ''}`}>
                                {formatMoney(Math.max(b.limitAmount - b.spent, 0))} remaining out of your {formatMoney(b.limitAmount)} budget
                            </div>
                            <div className="budget-percent">{Math.round(b.percentUsed)}%</div>
                            <div className="budget-bar-track">
                                <div
                                    className={`budget-bar-fill ${b.percentUsed >= 100 ? 'over-budget' : ''}`}
                                    style={{ width: `${b.percentUsed}%` }}
                                />
                            </div>
                        </div>
                    ))}
                </div>
            </div>

            <div className="section-card">
                <h2>Savings Goals</h2>

                <form onSubmit={handleGoalSubmit} className="inline-form">
                    <input
                        type="text"
                        placeholder="Goal name e.g. Emergency Fund"
                        value={goalForm.name}
                        onChange={(e) => setGoalForm((prev) => ({ ...prev, name: e.target.value }))}
                        required
                    />
                    <input
                        type="number"
                        placeholder="Target e.g. 200000"
                        step="0.01"
                        value={goalForm.targetAmount}
                        onChange={(e) => setGoalForm((prev) => ({ ...prev, targetAmount: e.target.value }))}
                        required
                    />
                    <input
                        type="date"
                        value={goalForm.targetDate}
                        onChange={(e) => setGoalForm((prev) => ({ ...prev, targetDate: e.target.value }))}
                    />
                    <select
                        value={goalForm.category}
                        onChange={(e) => setGoalForm((prev) => ({ ...prev, category: e.target.value }))}
                    >
                        <option value="">No specific category</option>
                        {Object.entries(categories).map(([name, icon]) => (
                            <option key={name} value={name}>{icon} {name}</option>
                        ))}
                    </select>
                    <button type="submit">Add Goal</button>
                </form>

                {goals.length === 0 && (
                    <div className="empty-state">
                        <p>No savings goals yet.</p>
                    </div>
                )}

                {goals.map((g) => {
                    const percent = g.targetAmount > 0 ? Math.min((g.currentAmount / g.targetAmount) * 100, 100) : 0;
                    return (
                        <div className="goal-row" key={g.id}>
                            <div className="goal-label">
                                <span>{g.name}</span>
                                <span>{formatMoney(g.currentAmount)} / {formatMoney(g.targetAmount)}</span>
                            </div>
                            <div className="budget-bar-track">
                                <div className="budget-bar-fill" style={{ width: `${percent}%` }} />
                            </div>
                            <div className="goal-meta">
                                {g.targetDate && <span>Target: {g.targetDate}</span>}
                                {g.category && <span>{categories[g.category]} {g.category}</span>}
                            </div>
                            <div className="goal-contribute-form">
                                <input
                                    type="number"
                                    placeholder="Add amount"
                                    step="0.01"
                                    value={contributions[g.id] || ''}
                                    onChange={(e) => setContributions((prev) => ({ ...prev, [g.id]: e.target.value }))}
                                />
                                <button type="button" onClick={() => handleContribute(g.id)}>Add</button>
                                <a className="delete" onClick={() => handleDeleteGoal(g.id)}>Delete</a>
                            </div>
                        </div>
                    );
                })}
            </div>

        </div>
    );
}