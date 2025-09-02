-- Create databases for each service
CREATE DATABASE ccpay_auth;
CREATE DATABASE ccpay_user;
CREATE DATABASE ccpay_wallet;

-- Grant all privileges to the user
GRANT ALL PRIVILEGES ON DATABASE ccpay_auth TO ccpay_user;
GRANT ALL PRIVILEGES ON DATABASE ccpay_user TO ccpay_user;
GRANT ALL PRIVILEGES ON DATABASE ccpay_wallet TO ccpay_user;