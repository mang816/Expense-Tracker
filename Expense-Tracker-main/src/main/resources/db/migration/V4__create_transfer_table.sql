CREATE TABLE transfer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE NOT NULL,
    date DATE NOT NULL,
    description VARCHAR(255),
    from_account_id BIGINT NOT NULL,
    to_account_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (from_account_id) REFERENCES account(id),
    FOREIGN KEY (to_account_id) REFERENCES account(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);