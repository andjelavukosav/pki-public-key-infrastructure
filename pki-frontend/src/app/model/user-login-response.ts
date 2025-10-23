import { AuthUser } from "./auth-user.model";

export interface LoginResponse {
  token: string;
  sessionId: string; 
  message: string;
}