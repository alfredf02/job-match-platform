export type ProfileListField = string | string[];

export interface UserProfile {
  userId?: number;
  email?: string;
  fullName?: string;
  location?: string;
  skills?: ProfileListField;
  desiredRoles?: ProfileListField;
  minSalary?: number;
  maxSalary?: number;
  salaryMin?: number;
  salaryMax?: number;
}

export interface UpdateProfileRequest {
  location?: string;
  skills?: ProfileListField;
  desiredRoles?: ProfileListField;
  minSalary?: number;
  maxSalary?: number;
  salaryMin?: number;
  salaryMax?: number;
}

export interface UpdatePreferencesRequest {
  location?: string;
  skills?: ProfileListField;
  desiredRoles?: ProfileListField;
  minSalary?: number;
  maxSalary?: number;
  salaryMin?: number;
  salaryMax?: number;
}
