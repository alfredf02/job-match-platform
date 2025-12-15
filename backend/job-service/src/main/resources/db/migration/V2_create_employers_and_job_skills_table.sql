-- Establish employers, jobs, and job_skills tables with indexes and audit timestamps.
DROP TABLE IF EXISTS job_skills;
DROP TABLE IF EXISTS jobs;

CREATE TABLE IF NOT EXISTS employers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    website VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS jobs (
    id BIGSERIAL PRIMARY KEY,
    employer_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(255),
    seniority VARCHAR(100),
    work_type VARCHAR(100),
    salary_min INTEGER,
    salary_max INTEGER,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_jobs_employer_id FOREIGN KEY (employer_id) REFERENCES employers (id)
);

CREATE INDEX IF NOT EXISTS idx_jobs_employer_id ON jobs (employer_id);
CREATE INDEX IF NOT EXISTS idx_jobs_location ON jobs (location);
CREATE INDEX IF NOT EXISTS idx_jobs_seniority ON jobs (seniority);
CREATE INDEX IF NOT EXISTS idx_jobs_work_type ON jobs (work_type);
CREATE INDEX IF NOT EXISTS idx_jobs_salary_min ON jobs (salary_min);
CREATE INDEX IF NOT EXISTS idx_jobs_salary_max ON jobs (salary_max);

CREATE TABLE IF NOT EXISTS job_skills (
    id BIGSERIAL PRIMARY KEY,
    job_id BIGINT NOT NULL,
    skill VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_job_skills_job_id FOREIGN KEY (job_id) REFERENCES jobs (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_job_skills_job_id ON job_skills (job_id);
CREATE INDEX IF NOT EXISTS idx_job_skills_skill ON job_skills (skill);