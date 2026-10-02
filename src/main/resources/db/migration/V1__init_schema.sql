CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE expense (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    amount DOUBLE NOT NULL,
    category VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    recurring BOOLEAN NOT NULL DEFAULT FALSE,
    recurrence_frequency VARCHAR(50),
    next_due_date DATE,
    receipt_image LONGBLOB,
    receipt_content_type VARCHAR(100),
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE budget (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(255) NOT NULL,
    limit_amount DOUBLE NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE savings_goal (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    target_amount DOUBLE NOT NULL,
    current_amount DOUBLE NOT NULL DEFAULT 0,
    target_date DATE,
    category VARCHAR(255),
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    balance DOUBLE NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE income (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source VARCHAR(255) NOT NULL,
    amount DOUBLE NOT NULL,
    date DATE NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE monthly_target (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    target_surplus DOUBLE NOT NULL,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_expense_user ON expense(user_id);
CREATE INDEX idx_budget_user ON budget(user_id);
CREATE INDEX idx_savings_goal_user ON savings_goal(user_id);
CREATE INDEX idx_account_user ON account(user_id);
CREATE INDEX idx_income_user ON income(user_id);