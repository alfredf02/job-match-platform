-- Index email lookups to speed up authentication queries against the users table.
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Index profile lookups by user_id since profile endpoints frequently access by authenticated user id.
CREATE INDEX IF NOT EXISTS idx_user_profiles_user_id ON user_profiles(user_id);

/*
Explanation
- Speeds up email-based user retrieval for login/registration flows where UserRepository.findByEmail is used.
- Optimizes profile fetch/update paths that query user_profiles by user_id after JWT authentication resolves the user id.
*/