import { post } from "@/lib/api/client";
import { USER_SERVICE_URL } from "@/lib/api/config";
import {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  RegisterResponse,
} from "@/types/auth";

const AUTH_BASE_URL = `${USER_SERVICE_URL}/api/auth`;

export function register(request: RegisterRequest): Promise<RegisterResponse> {
  return post<RegisterResponse>(`${AUTH_BASE_URL}/register`, { body: request });
}

export function login(request: LoginRequest): Promise<LoginResponse> {
  return post<LoginResponse>(`${AUTH_BASE_URL}/login`, { body: request });
}
