import { Component, OnDestroy, OnInit } from '@angular/core';
import { AuthUser } from '../model/auth-user.model';
import { UserService } from '../service/user.service';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.css']
})
export class NavBarComponent implements OnInit {
  user: AuthUser | null = null;

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    // Pretplatimo se na stanje korisnika
    this.userService.user$.subscribe(user => this.user = user)
  }

  logout(){
    this.authService.clearToken();
    this.userService.clearUser();
    this.router.navigate(['']);
  }

  isLoggedAdmin(): boolean{
    return this.user?.role==='ADMIN';
  }
}
