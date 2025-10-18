import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserRegistration } from '../model/user-registration';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl = 'http://localhost:8080/api/auth'; 

  constructor(private http: HttpClient) { }

  // Metoda koja šalje POST zahtev na backend
  register(user: UserRegistration): Observable<any> {
    return this.http.post(`${this.baseUrl}/register`, user, { responseType: 'text' });
  }

  verify(token: string): Observable<any> {
    const params = new HttpParams().set('token', token);
    return this.http.post(this.baseUrl + '/verify', null, { params, responseType: 'text' as 'json' });
  }

  getToken():string|null{
    return localStorage.getItem('jwtToken')
  }



}