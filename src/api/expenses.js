import client from './client';

export async function getExpenses() {
    const response = await client.get('/expenses');
    return response.data;
}

export async function createExpense(expense) {
    const response = await client.post('/expenses', expense);
    return response.data;
}

export async function updateExpense(id, expense) {
    const response = await client.put(`/expenses/${id}`, expense);
    return response.data;
}

export async function deleteExpense(id) {
    await client.delete(`/expenses/${id}`);
}