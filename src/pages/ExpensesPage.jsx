import { useEffect, useState } from 'react';
import { getExpenses, createExpense, updateExpense, deleteExpense } from '../api/expenses.js';
import { getCategories } from '../api/categories.js';
import { Link } from 'react-router-dom';

export default function ExpensesPage() {



    const [expenses, setExpenses] = useState([]);
    const [categories, setCategories] = useState({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const [editingId, setEditingId] = useState(null);

    const [form, setForm] = useState({
        description: '',
        amount: '',
        category: '',
        date: new Date().toISOString().slice(0, 10),
    });

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {
        setLoading(true);
        setError('');
        try {
            const [expensesData, categoriesData] = await Promise.all([
                getExpenses(),
                getCategories(),
            ]);
            setExpenses(expensesData);
            setCategories(categoriesData);

            const firstCategory = Object.keys(categoriesData)[0] || '';
            setForm((prev) => ({ ...prev, category: prev.category || firstCategory }));
        } catch (err) {
            setError('Could not load expenses.');
        } finally {
            setLoading(false);
        }
    }

    function handleChange(e) {
        const { name, value } = e.target;
        setForm((prev) => ({ ...prev, [name]: value }));
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setError('');

        const payload = {
            description: form.description,
            amount: parseFloat(form.amount),
            category: form.category,
            date: form.date,
            recurring: false,
        };

        try {
            if (editingId) {
                await updateExpense(editingId, payload);
                setEditingId(null);
            } else {
                await createExpense(payload);
            }

            setForm({
                description: '',
                amount: '',
                category: Object.keys(categories)[0] || '',
                date: new Date().toISOString().slice(0, 10),
            });

            loadData();
        } catch (err) {
            setError('Could not save expense. Check your inputs.');
        }
    }

    function startEdit(expense) {
        setEditingId(expense.id);
        setForm({
            description: expense.description,
            amount: expense.amount,
            category: expense.category,
            date: expense.date,
        });
    }

    function cancelEdit() {
        setEditingId(null);
        setForm({
            description: '',
            amount: '',
            category: Object.keys(categories)[0] || '',
            date: new Date().toISOString().slice(0, 10),
        });
    }

    async function handleDelete(id) {
        if (!window.confirm('Are you sure you want to delete this expense?')) return;

        try {
            await deleteExpense(id);
            loadData();
        } catch (err) {
            setError('Could not delete expense.');
        }
    }

    const total = expenses.reduce((sum, e) => sum + e.amount, 0);

    return (
        <div className="page-container">

                      <h1 className="page-title">My Expense Tracker</h1>

            <div className="total-card">
                <h2>Total Expenses</h2>
                <div className="total-amount">₦{total.toLocaleString(undefined, { minimumFractionDigits: 2 })}</div>
            </div>

            <div className="section-card">
                <h2>{editingId ? 'Edit Expense' : 'Add Expense'}</h2>

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Description</label>
                        <input
                            type="text"
                            name="description"
                            value={form.description}
                            onChange={handleChange}
                            placeholder="Example: Groceries"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Amount</label>
                        <input
                            type="number"
                            name="amount"
                            value={form.amount}
                            onChange={handleChange}
                            step="0.01"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Category</label>
                        <select name="category" value={form.category} onChange={handleChange}>
                            {Object.entries(categories).map(([name, icon]) => (
                                <option key={name} value={name}>
                                    {icon} {name}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group">
                        <label>Date</label>
                        <input
                            type="date"
                            name="date"
                            value={form.date}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    {error && <p className="error">{error}</p>}

                    <button type="submit">{editingId ? 'Update Expense' : 'Add Expense'}</button>

                    {editingId && (
                        <button type="button" className="secondary-btn" onClick={cancelEdit}>
                            Cancel
                        </button>
                    )}
                </form>
            </div>

            <div className="section-card">
                <h2>My Expenses</h2>

                {loading && <p>Loading...</p>}

                {!loading && expenses.length === 0 && (
                    <div className="empty-state">
                        <p>No expenses yet. Add one above.</p>
                    </div>
                )}

                {expenses.map((expense) => (
                    <div className="expense-row" key={expense.id}>
                        <div className="expense-top">
                            <strong>{expense.description}</strong>
                            <strong>₦{expense.amount.toLocaleString(undefined, { minimumFractionDigits: 2 })}</strong>
                        </div>
                        <div className="expense-bottom">
                            <span className="expense-meta">
                                {categories[expense.category]} {expense.category} • {expense.date}
                            </span>
                            <span className="expense-actions">
                                <a onClick={() => startEdit(expense)}>Edit</a>
                                <a className="delete" onClick={() => handleDelete(expense.id)}>Delete</a>
                            </span>
                        </div>
                    </div>
                ))}
            </div>

        </div>
    );
}