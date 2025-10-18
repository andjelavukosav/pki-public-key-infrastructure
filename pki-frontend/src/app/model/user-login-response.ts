import { AuthUser } from "./auth-user.model";

export interface LoginResponse {
  token: string;
  message: string;
}