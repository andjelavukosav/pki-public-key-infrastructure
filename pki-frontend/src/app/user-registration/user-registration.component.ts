import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../service/auth.service';
import { UserRegistration } from '../model/user-registration';
import * as zxcvbn from 'zxcvbn';

@Component({
  selector: 'app-user-registration',
  templateUrl: './user-registration.component.html',
  styleUrls: ['./user-registration.component.css']
})
export class UserRegistrationComponent {
  registerForm: FormGroup;
  passwordStrength = 0;         // jačina lozinke 0-4
  passwordFeedback = '';        // tekstualni savet

  constructor(private fb: FormBuilder, private authService: AuthService) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern('^(?=.*[A-Z])(?=.*[0-9]).{8,}$') // bar jedno veliko slovo i broj
      ]],
      confirmPassword: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      organization: ['', Validators.required],
    });

    // Praćenje promene lozinke za indikator jačine
    this.registerForm.get('password')?.valueChanges.subscribe(() => {
      this.onPasswordInput();
    });
  }

  // Poziva se dok korisnik kuca lozinku
  onPasswordInput() {
    const value = this.registerForm.get('password')?.value || '';
    const result = zxcvbn(value);
    this.passwordStrength = result.score; // vrednost 0-4
    this.passwordFeedback = result.feedback.warning || result.feedback.suggestions.join(', ');
  }

  // Registracija korisnika
  register() {
    if (this.registerForm.invalid) return;

    const formValue: UserRegistration = this.registerForm.value;

    if (formValue.password !== formValue.confirmPassword) {
      alert('Lozinke se ne poklapaju!');
      return;
    }

    this.authService.register(formValue).subscribe({
      next: (res) => {
        alert(res); // Backend vraća string "Registracija uspešna!"
        this.registerForm.reset();
        this.passwordStrength = 0;
        this.passwordFeedback = '';
      },
      error: (err) => {
        let msg = 'Došlo je do greške prilikom registracije.';
        if (err.error) {
          if (typeof err.error === 'string') {
            msg = err.error;
          } else if (err.error.message) {
            msg = err.error.message;
          }
        }
        alert(msg);
      }
    });
  }

  // Pravila lozinke (crvena, žuta, zelena traka)
  getPasswordRules() {
    const value = this.registerForm.get('password')?.value || '';
    return {
      minLength: value.length >= 8,
      uppercase: /[A-Z]/.test(value),
      number: /[0-9]/.test(value)
    };
  }

  passwordsMatch() {
    return this.registerForm.get('password')?.value === this.registerForm.get('confirmPassword')?.value;
  }

  getPasswordStrengthPercent() {
    return (this.passwordStrength / 4) * 100; // za width trake 0-100%
  }

  getPasswordStrengthColor() {
    switch (this.passwordStrength) {
      case 0: return 'red';
      case 1: return 'orange';
      case 2: return 'yellow';
      case 3: return 'yellowgreen';
      case 4: return 'green';
      default: return 'red';
    }
  }
}
