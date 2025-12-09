-- Create the user_profiles table to store additional job preferences for each user.
CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGSERIAL PRIMARY KEY, -- Auto-incrementing primary key for profiles
    user_id BIGINT NOT NULL, -- References the owning user record
    location VARCHAR(255), -- Optional city/region string
    skills TEXT, -- Comma-separated skills list for v1
    min_salary INTEGER, -- Minimum desired salary
    max_salary INTEGER, -- Maximum desired salary
    desired_roles TEXT, -- Comma-separated desired role names
    CONSTRAINT fk_user_profiles_user_id FOREIGN KEY (user_id) REFERENCES users (id), -- Enforce user relationship
    CONSTRAINT uq_user_profiles_user_id UNIQUE (user_id) -- Ensure one profile per user
);