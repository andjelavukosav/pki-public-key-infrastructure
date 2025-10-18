import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { AuthUser } from '../model/auth-user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  
  private userSubject = new BehaviorSubject<AuthUser | null>(null);
  user$ = this.userSubject.asObservable();

  constructor() { }

  setUser(user: AuthUser): void {
    this.userSubject.next(user);
  }

  getAuthenticatedUser(): AuthUser | null {
    return this.userSubject.getValue();
  }

  clearUser(): void{
    this.userSubject.next(null);
  }
}
