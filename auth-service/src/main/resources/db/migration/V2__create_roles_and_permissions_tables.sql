-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    role_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

-- Create permissions table
CREATE TABLE IF NOT EXISTS permissions (
    permission_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    resource VARCHAR(100),
    action VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

-- Create user_roles junction table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE
);

-- Create role_permissions junction table
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);
CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions(permission_id);

-- Insert default roles
INSERT INTO roles (name, description) VALUES 
    ('ROLE_USER', 'Default user role'),
    ('ROLE_ADMIN', 'Administrator role'),
    ('ROLE_SUPPORT', 'Customer support role'),
    ('ROLE_SYSTEM', 'System role for automated processes');

-- Insert default permissions
INSERT INTO permissions (name, description, resource, action) VALUES 
    ('user:read', 'Read user information', 'user', 'read'),
    ('user:write', 'Update user information', 'user', 'write'),
    ('wallet:read', 'Read wallet information', 'wallet', 'read'),
    ('wallet:write', 'Perform wallet operations', 'wallet', 'write'),
    ('card:read', 'Read card information', 'card', 'read'),
    ('card:write', 'Manage cards', 'card', 'write'),
    ('transaction:read', 'View transactions', 'transaction', 'read'),
    ('transaction:write', 'Create transactions', 'transaction', 'write'),
    ('admin:read', 'Read admin information', 'admin', 'read'),
    ('admin:write', 'Perform admin operations', 'admin', 'write');

-- Assign permissions to roles
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id 
FROM roles r, permissions p 
WHERE r.name = 'ROLE_USER' 
AND p.name IN ('user:read', 'user:write', 'wallet:read', 'wallet:write', 'card:read', 'card:write', 'transaction:read', 'transaction:write');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id 
FROM roles r, permissions p 
WHERE r.name = 'ROLE_ADMIN';