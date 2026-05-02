CREATE TABLE IF NOT EXISTS matching_user_profiles (
    id BIGSERIAL PRIMARY KEY,
    external_user_id BIGINT NOT NULL UNIQUE,
    location VARCHAR(255),
    salary_min INTEGER,
    salary_max INTEGER,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create local read-model table for user skills
CREATE TABLE IF NOT EXISTS matching_user_skills (
    user_profile_id BIGINT NOT NULL REFERENCES matching_user_profiles(id) ON DELETE CASCADE,
    skill VARCHAR(100) NOT NULL,
    PRIMARY KEY (user_profile_id, skill)
);

-- Create local read-model table for user desired roles
CREATE TABLE IF NOT EXISTS matching_user_desired_roles (
    user_profile_id BIGINT NOT NULL REFERENCES matching_user_profiles(id) ON DELETE CASCADE,
    role VARCHAR(150) NOT NULL,
    PRIMARY KEY (user_profile_id, role)
);

-- Create local read-model table for job snapshots used by matching-service
CREATE TABLE IF NOT EXISTS matching_jobs (
    id BIGSERIAL PRIMARY KEY,
    external_job_id BIGINT NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    company VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    salary_min INTEGER,
    salary_max INTEGER,
    work_type VARCHAR(50),
    seniority VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create local read-model table for job skills
CREATE TABLE IF NOT EXISTS matching_job_skills (
    job_id BIGINT NOT NULL REFERENCES matching_jobs(id) ON DELETE CASCADE,
    skill VARCHAR(100) NOT NULL,
    PRIMARY KEY (job_id, skill)
);

-- Indexes for deterministic lookup and filtering
CREATE INDEX IF NOT EXISTS idx_matching_user_profiles_external_user_id
    ON matching_user_profiles (external_user_id);

CREATE INDEX IF NOT EXISTS idx_matching_jobs_external_job_id
    ON matching_jobs (external_job_id);

CREATE INDEX IF NOT EXISTS idx_matching_jobs_location
    ON matching_jobs (location);

CREATE INDEX IF NOT EXISTS idx_matching_jobs_salary_range
    ON matching_jobs (salary_min, salary_max);

CREATE INDEX IF NOT EXISTS idx_matching_user_skills_skill
    ON matching_user_skills (skill);

CREATE INDEX IF NOT EXISTS idx_matching_job_skills_skill
    ON matching_job_skills (skill);