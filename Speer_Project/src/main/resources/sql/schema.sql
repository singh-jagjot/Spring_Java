CREATE TABLE IF NOT EXISTS users
(
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS notes
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT       NOT NULL,
    title   VARCHAR(100) NOT NULL UNIQUE,
    content TEXT         NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS shared
(
    id      BIGSERIAL PRIMARY KEY,
    shared_by_id BIGINT NOT NULL,
    shared_with_id BIGINT NOT NULL,
    note_id BIGINT NOT NULL,
    FOREIGN KEY (shared_by_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (shared_with_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (note_id) REFERENCES notes (id) ON DELETE CASCADE,
    CONSTRAINT check_columns_not_equal CHECK (shared_by_id != shared_with_id),
    CONSTRAINT unique_column_combination1 UNIQUE (shared_with_id, note_id),
    CONSTRAINT unique_column_combination2 UNIQUE (shared_by_id, shared_with_id, note_id)
);

-- CREATE INDEX IF NOT EXISTS idx_users_username ON users (username);
-- CREATE INDEX IF NOT EXISTS idx_notes_user_id ON notes (user_id);
CREATE INDEX IF NOT EXISTS notes_content_idx ON notes USING GIN (to_tsvector('english', title || ' ' || content));
CREATE INDEX IF NOT EXISTS idx_shared_shared_by_id ON shared (shared_by_id);
CREATE INDEX IF NOT EXISTS idx_shared_shared_with_id ON shared (shared_with_id);
CREATE INDEX IF NOT EXISTS idx_shared_note_id ON shared (note_id);