CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    description VARCHAR(200) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    transaction_date DATE NOT NULL,
    category_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_transactions_amount
        CHECK (amount > 0),

    CONSTRAINT chk_transactions_type
        CHECK (type IN ('INCOME', 'EXPENSE')),

    CONSTRAINT fk_transactions_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
);

CREATE INDEX idx_transactions_date
    ON transactions (transaction_date);

CREATE INDEX idx_transactions_category
    ON transactions (category_id);