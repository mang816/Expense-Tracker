import client from './client';

export async function getSummary() {
    const response = await client.get('/reports/summary');
    return response.data;
}