INSERT INTO matching_user_profiles (id, external_user_id, location, salary_min, salary_max, created_at, updated_at)
VALUES
    (1001, 1, 'Sydney', 80000, 120000, NOW(), NOW()),
    (1002, 2, 'Melbourne', 90000, 130000, NOW(), NOW())
ON CONFLICT (external_user_id) DO NOTHING;

-- User skills
INSERT INTO matching_user_skills (user_profile_id, skill)
VALUES
    (1001, 'Java'),
    (1001, 'Spring Boot'),
    (1001, 'PostgreSQL'),
    (1001, 'Docker'),
    (1002, 'Python'),
    (1002, 'AWS'),
    (1002, 'SQL')
ON CONFLICT (user_profile_id, skill) DO NOTHING;

-- User desired roles
INSERT INTO matching_user_desired_roles (user_profile_id, role)
VALUES
    (1001, 'Backend Developer'),
    (1001, 'Software Engineer'),
    (1002, 'Data Engineer')
ON CONFLICT (user_profile_id, role) DO NOTHING;

-- Jobs
INSERT INTO matching_jobs (id, external_job_id, title, company, location, salary_min, salary_max, work_type, seniority, created_at, updated_at)
VALUES
    (2001, 101, 'Backend Developer', 'TechCorp', 'Sydney', 90000, 120000, 'HYBRID', 'MID', NOW(), NOW()),
    (2002, 102, 'Frontend Developer', 'Webify', 'Sydney', 70000, 100000, 'ONSITE', 'MID', NOW(), NOW()),
    (2003, 103, 'Data Engineer', 'DataWorks', 'Melbourne', 100000, 140000, 'HYBRID', 'SENIOR', NOW(), NOW()),
    (2004, 104, 'Junior Software Engineer', 'StartupHub', 'Sydney', 65000, 85000, 'ONSITE', 'JUNIOR', NOW(), NOW()),
    (2005, 105, 'DevOps Engineer', 'CloudOps', 'Remote', 110000, 150000, 'REMOTE', 'SENIOR', NOW(), NOW())
ON CONFLICT (external_job_id) DO NOTHING;

-- Job skills
INSERT INTO matching_job_skills (job_id, skill)
VALUES
    (2001, 'Java'),
    (2001, 'Spring Boot'),
    (2001, 'PostgreSQL'),
    (2002, 'React'),
    (2002, 'TypeScript'),
    (2002, 'CSS'),
    (2003, 'Python'),
    (2003, 'PostgreSQL'),
    (2003, 'AWS'),
    (2004, 'Java'),
    (2004, 'Git'),
    (2004, 'SQL'),
    (2005, 'Docker'),
    (2005, 'AWS'),
    (2005, 'Kubernetes')
ON CONFLICT (job_id, skill) DO NOTHING;