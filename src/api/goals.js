import client from './client';

export async function getGoals() {
    const response = await client.get('/goals');
    return response.data;
}

export async function createGoal(goal) {
    const response = await client.post('/goals', goal);
    return response.data;
}

export async function contributeToGoal(id, amount) {
    const response = await client.post(`/goals/${id}/contribute`, { amount });
    return response.data;
}

export async function deleteGoal(id) {
    await client.delete(`/goals/${id}`);
}