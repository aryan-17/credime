CREATE TABLE IF NOT EXISTS login_history (
    history_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    login_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent TEXT,
    device_info TEXT,
    location VARCHAR(255),
    login_status VARCHAR(50) NOT NULL,
    failure_reason TEXT,
    session_duration_seconds BIGINT,
    CONSTRAINT fk_login_history_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_login_status CHECK (login_status IN ('SUCCESS', 'FAILED', 'BLOCKED'))
);

CREATE INDEX idx_login_history_user_id ON login_history(user_id);
CREATE INDEX idx_login_history_login_time ON login_history(login_time);
CREATE INDEX idx_login_history_login_status ON login_history(login_status);