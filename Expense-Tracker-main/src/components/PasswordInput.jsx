import { useState } from 'react';
import './PasswordInput.css';

export default function PasswordInput({ value, onChange, autoComplete = 'current-password' }) {
    const [visible, setVisible] = useState(false);

    return (
        <div className="password-input">
            <input
                type={visible ? 'text' : 'password'}
                value={value}
                onChange={onChange}
                autoComplete={autoComplete}
                required
            />
            <button
                type="button"
                className="password-toggle"
                onClick={() => setVisible((v) => !v)}
                aria-label={visible ? 'Hide password' : 'Show password'}
            >
                {visible ? 'Hide' : 'Show'}
            </button>
        </div>
    );
}