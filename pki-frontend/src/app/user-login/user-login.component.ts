import { Component, inject, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { RecaptchaComponent } from 'ng-recaptcha';
import { environment } from 'src/environments/environment';
import { LoginRequest } from '../model/user-login-request';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';
import { LoginResponse } from '../model/user-login-response';
import { AuthUser } from '../model/auth-user.model';
import { UserRole } from '../model/user.model';
import { jwtDecode } from 'jwt-decode';
import { UserService } from '../service/user.service';

@Component({
  selector: 'app-user-login',
  templateUrl: './user-login.component.html',
  styleUrls: ['./user-login.component.css']
})
export class UserLoginComponent {
  private fb = inject(FormBuilder);

  // referenca na reCAPTCHA komponentu iz HTML-a
  @ViewChild('captchaRef') captchaRef!: RecaptchaComponent;

  recaptchaSiteKey = environment.recaptchaSiteKey; // javni kljuc koji frontend koristi da bi prikazao reCAPTCHA komponentu i komunicirao sa Google-om
  captchaToken: string | null = null;

  showPassword = false;
  submitting = false;
  formError: string | null = null;
  captchaError = false;

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    //rememberMe: [false],
  });

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private router: Router,
  ) {}

  get email() { return this.loginForm.get('email')!; }
  get password() { return this.loginForm.get('password')!; }

  toggleShowPassword() {
    this.showPassword = !this.showPassword;
  }

  onCaptchaResolved(token: string | null) {
    this.captchaToken = token;
    this.captchaError = !token;
  }

  async onSubmit() {
    this.formError = null;

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }
    if (!this.captchaToken) {
      this.captchaError = true;
      return;
    }

    this.submitting = true;

    const payload : LoginRequest = {
      email: this.email.value!,
      password: this.password.value!,
      recaptchaToken: this.captchaToken!,
    };

    this.authService.login(payload).subscribe({
      next: (response: LoginResponse) => {

        this.authService.setToken(response.token);
        
        // Dekodovanje tokena da bi se autentifikovao korisnik
        const { user, error} = this.authService.decodeToken(response.token);
        if (error) {
          this.formError = error;
          this.authService.clearToken();
          this.captchaRef?.reset();
          this.captchaToken = null;
          this.submitting = false;
          return;
        }

        if(user){
          // Objavi user stanje pretplatnicima
          this.userService.setUser(user);

          if(user.role === UserRole.USER){
            this.router.navigate(['/user-home']);
          }
          if(user.role === UserRole.CA_USER){
            //this.router.navigate(['/ca-user-home']);
          }
          if(user.role === UserRole.ADMIN){
            //this.router.navigate(['/admin-home']);
          }
        }        
      },
      error: err => {
        this.formError = err.error?.message || 'Login greška';
        this.captchaRef?.reset();
        this.captchaToken = null;
        this.submitting = false;
      },
      complete: () => this.submitting = false
    });
  }

  onForgotPassword(){
    this.router.navigate(['forgot-password'])
  }

}
