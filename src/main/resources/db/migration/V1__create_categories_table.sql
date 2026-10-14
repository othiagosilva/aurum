CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_categories_type
        CHECK (type IN ('INCOME', 'EXPENSE'))
);

CREATE UNIQUE INDEX uq_categories_name_type
    ON categories (LOWER(name), type);