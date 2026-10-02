import client from './client';

export async function getBudgets() {
    const response = await client.get('/budgets');
    return response.data;
}

export async function setBudget(category, limitAmount) {
    const response = await client.post('/budgets', { category, limitAmount });
    return response.data;
}