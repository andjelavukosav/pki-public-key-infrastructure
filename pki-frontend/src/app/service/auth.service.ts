import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserRegistration } from '../model/user-registration';
import { LoginRequest } from '../model/user-login-request';
import { LoginResponse } from '../model/user-login-response';
import { jwtDecode } from 'jwt-decode';
import { AuthUser } from '../model/auth-user.model';
import { UserRole } from '../model/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl = 'http://localhost:8080/api/auth'; 
  private tokenKey = 'access_token';

  constructor(private http: HttpClient) { }

  // Metoda koja šalje POST zahtev na backend
  register(user: UserRegistration): Observable<any> {
    return this.http.post(`${this.baseUrl}/register`, user, { responseType: 'text' });
  }

  verify(token: string): Observable<any> {
    const params = new HttpParams().set('token', token);
    return this.http.post(this.baseUrl + '/verify', null, { params, responseType: 'text' as 'json' });
  }


  login(payload: LoginRequest): Observable<LoginResponse>{
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, payload);
  }

  setToken(token: string): void {
    sessionStorage.setItem(this.tokenKey, token);
  }

  getToken(): string | null {
    return sessionStorage.getItem(this.tokenKey);
  }

  clearToken(): void {
    sessionStorage.removeItem(this.tokenKey);
  }

  decodeToken(token: string | null): { user?: AuthUser; error?: string } {
    if (!token) return { error: 'Token ne postoji' };

    try {
      const decoded: any = jwtDecode(token);
      // Ako nema exp — token je nevalidan
      // provjera isteka
      if (!decoded.exp || decoded.exp * 1000 <= Date.now()) {
        console.warn('Token je istekao.');
        return { error: 'Token je istekao' };
      }
      
      if (!decoded.email || !decoded.role) {
        return { error: 'Nedostaju polja u tokenu' };
      }

      return {
        user: {
          id: decoded.userId ?? decoded.sub,
          email: decoded.email,
          role: decoded.role as UserRole
        }
      };

    } catch(err) {
      console.error('Greska prilikom dekodovanja tokena.', err);
      return { error: 'Nevalidan token' };
    }
  }

}