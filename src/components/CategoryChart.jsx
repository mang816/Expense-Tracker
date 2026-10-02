import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const COLORS = [
    '#2563eb', '#dc2626', '#16a34a', '#f59e0b', '#9333ea', '#6b7280',
    '#0891b2', '#db2777', '#65a30d', '#ea580c', '#7c3aed', '#0d9488',
    '#ca8a04', '#e11d48', '#475569',
];

export default function CategoryChart({ categoryTotals }) {
    const data = Object.entries(categoryTotals).map(([name, value]) => ({
        name,
        value,
    }));

    if (data.length === 0) {
        return <p className="empty-state">No data to chart yet.</p>;
    }

    return (
        <ResponsiveContainer width="100%" height={300}>
            <PieChart>
                <Pie
                    data={data}
                    dataKey="value"
                    nameKey="name"
                    cx="50%"
                    cy="50%"
                    innerRadius={60}
                    outerRadius={100}
                    paddingAngle={2}
                >
                    {data.map((entry, index) => (
                        <Cell key={entry.name} fill={COLORS[index % COLORS.length]} />
                    ))}
                </Pie>
                <Tooltip formatter={(value) => `₦${Number(value).toLocaleString(undefined, { minimumFractionDigits: 2 })}`} />
                <Legend />
            </PieChart>
        </ResponsiveContainer>
    );
}