import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { UserRegistration } from '../model/user-registration';
import { LoginRequest } from '../model/user-login-request';
import { LoginResponse } from '../model/user-login-response';
import { jwtDecode } from 'jwt-decode';
import { AuthUser } from '../model/auth-user.model';
import { UserRole } from '../model/user.model';
import { DecodedToken } from '../model/decoded-token.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl = 'http://localhost:8080/api/auth'; 
  private tokenKey = 'access_token';
    private currentUserSubject = new BehaviorSubject<DecodedToken | null>(this.loadUserFromToken());


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

  forgotPassword(email: string): Observable<{message: string}> {
    return this.http.post<{message: string}>(`${this.baseUrl}/forgot-password`, { email });
  }

  resetPassword(token: string, newPassword: string): Observable<{message: string}>{
    return this.http.post<{message: string}>(`${this.baseUrl}/reset-password`, { rawToken: token, newPassword });
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

  getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    let headers = new HttpHeaders();
    if(token)
    {
      headers=headers.set('Authorization',`Bearer ${token}`);
    }
    return headers;
  }

  getCurrentUser(): DecodedToken|null{
    return this.currentUserSubject.value;
  }

  logout(){
    localStorage.removeItem('jwtToken');
    this.currentUserSubject.next(null);
  }

  private loadUserFromToken(): DecodedToken | null {
    const token = this.getToken();
    if(token){
      try{
        const decoded = jwtDecode<DecodedToken>(token);

        if(this.isTokenExpired(decoded)){
          this.logout();
          return null;
        }

        return decoded;
        }
      catch
          {
              return null;
          }
    }
    return null;
  }

  private isTokenExpired(decoded: DecodedToken): boolean{
    return decoded.exp*1000 < Date.now();
  }

}