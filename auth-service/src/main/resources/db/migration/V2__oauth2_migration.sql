-- Remove password-related columns and add OAuth2 columns
ALTER TABLE users 
    DROP COLUMN IF EXISTS password_hash,
    DROP COLUMN IF EXISTS failed_login_attempts,
    DROP COLUMN IF EXISTS locked_until,
    ADD COLUMN oauth_provider VARCHAR(50) NOT NULL,
    ADD COLUMN oauth_id VARCHAR(255) NOT NULL,
    ADD COLUMN profile_image_url VARCHAR(500);

-- Add unique constraint for OAuth provider and ID combination
ALTER TABLE users 
    ADD CONSTRAINT uk_oauth_provider_id UNIQUE (oauth_provider, oauth_id);

-- Create index for faster OAuth lookups
CREATE INDEX idx_oauth_provider_id ON users(oauth_provider, oauth_id);