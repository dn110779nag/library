CREATE TABLE book_lendings (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL,
    subscriber_id BIGINT NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE RESTRICT,
    FOREIGN KEY (subscriber_id) REFERENCES subscribers(id) ON DELETE RESTRICT
);