import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {

  resetForm!: FormGroup;
  token: string = '';
  submitting = false;
  message = '';
  passwordRules = { minLength: false, uppercase: false, number: false };
  passwordStrengthPercent = 0;
  passwordStrengthColor = '#d92d20';
  passwordFeedback = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
  ) { }

  ngOnInit(): void {
    this.resetForm = this.fb.group({
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required]
    }, { validators: this.passwordsMatch });

    this.route.queryParams.subscribe(params => {
      this.token = params['token'];
    });
  }

  passwordsMatch(group: AbstractControl) {
    const pass = group.get('newPassword')?.value;
    const confirm = group.get('confirmPassword')?.value;
    return pass === confirm ? null : { notMatching: true };
  }

  onPasswordInput() {
    const password = this.resetForm.get('newPassword')?.value || '';
    this.passwordRules.minLength = password.length >= 8;
    this.passwordRules.uppercase = /[A-Z]/.test(password);
    this.passwordRules.number = /\d/.test(password);

    const rulesMet = Object.values(this.passwordRules).filter(v => v).length;
    this.passwordStrengthPercent = (rulesMet / 3) * 100;
    this.passwordStrengthColor = this.passwordStrengthPercent < 50 ? '#d92d20' : (this.passwordStrengthPercent < 100 ? '#f59e0b' : '#16a34a');
    this.passwordFeedback = this.passwordStrengthPercent < 100 ? 'Lozinka može biti jača' : 'Lozinka je jaka';
  }

    onSubmit() {
      if (this.resetForm.invalid || !this.token) return;

      this.submitting = true;

      const newPassword = this.resetForm.value.newPassword;

      this.authService.resetPassword(this.token, newPassword).subscribe({
        next: (response) => {
          this.message = response.message;
          setTimeout(() => this.router.navigate(['/login']), 1500); // nakon 1.5s navigacija na login
        },
        error: (err) => {
          this.message = err.error?.message || 'Došlo je do greške prilikom resetovanja lozinke.';
          this.submitting = false;
        }
      });
  }

}