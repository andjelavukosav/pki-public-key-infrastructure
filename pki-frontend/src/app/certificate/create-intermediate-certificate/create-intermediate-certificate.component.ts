import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { CertificateService } from '../service/certificate.service';
import { UserService } from 'src/app/service/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CertificateRequest } from '../model/certificateRequest';

@Component({
  selector: 'app-create-intermediate-certificate',
  templateUrl: './create-intermediate-certificate.component.html',
  styleUrls: ['./create-intermediate-certificate.component.css']
})
export class CreateIntermediateCertificateComponent implements OnInit{
  intermediateForm!: FormGroup;
  userId!: number | null;
  issuerId!: number;
  maxDays!: number;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private certificateService: CertificateService,
    private authService: UserService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.issuerId = params['issuerId'];

      this.certificateService.getCertificateById(this.issuerId).subscribe(issuer => {
        const today = new Date();
        const end = new Date(issuer.endDate);
        const diff = Math.floor((end.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
        this.maxDays = diff;

        // Ažuriraj validator za durationInDays
        this.intermediateForm.get('durationInDays')?.setValidators([
          Validators.required,
          Validators.min(1),
          Validators.max(this.maxDays)
        ]);
        this.intermediateForm.get('durationInDays')?.updateValueAndValidity();
      });
    });

    this.intermediateForm = this.fb.group({
      cn: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(64),
        Validators.pattern(/^[a-zA-Z0-9\s\.\-_]+$/)
      ]],
      o: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(64),
        Validators.pattern(/^[a-zA-Z0-9\s\.\-_&]+$/)
      ]],
      ou: ['', [
        Validators.minLength(2),
        Validators.maxLength(64),
        Validators.pattern(/^[a-zA-Z0-9\s\.\-_]+$/)
      ]],
      c: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(2),
        Validators.pattern(/^[A-Z]{2}$/)
      ]],
      durationInDays: [1, [Validators.required, Validators.min(1)]],
      isRoot: [false],
      isIntermediate: [true],
      isEndEntity: [false],
      isCA: [true],
      extensions: this.fb.control({})
    });

    const currentUser = this.authService.getAuthenticatedUser();
    this.userId = currentUser ? currentUser.id : null;
  }

  onSubmit() {
    if (this.intermediateForm.invalid) {
      this.intermediateForm.markAllAsTouched();
      this.snackBar.open('Please fix all errors before submitting', 'Close', {
        duration: 3000,
        horizontalPosition: 'center'
      });
      return;
    }

    if (this.userId !== null) {
      const dto: CertificateRequest = {
        cn: this.intermediateForm.value.cn,
        o: this.intermediateForm.value.o,
        ou: this.intermediateForm.value.ou,
        c: this.intermediateForm.value.c,
        issuerId: this.issuerId,
        durationInDays: this.intermediateForm.value.durationInDays,
        isRoot: false,
        isIntermediate: true,
        isEndEntity: false,
        isCA: true,
        extensions: this.intermediateForm.value.extensions || {}
      };

      this.certificateService.issueCertificate(dto).subscribe({
        next: res => {
          console.log('Intermediate certificate issued' + res);
          this.snackBar.open('Intermediate certificate created', 'Close', {
            duration: 4000,
            horizontalPosition: 'center'
          });
          this.intermediateForm.reset({ durationInDays: 1, isCA: true });
        },
        error: err => {
          console.error('Error during making certificate', err);
          this.snackBar.open('Error during certificate issue', 'Close', {
            duration: 4000,
            horizontalPosition: 'center'
          });
        }
      });
    }
  }

  getErrorMessage(fieldName: string): string {
    const control = this.intermediateForm.get(fieldName);
    if (!control || !control.errors || !control.touched) return '';

    if (control.errors['required']) return `${fieldName} is required`;
    if (control.errors['minlength']) return `Minimum ${control.errors['minlength'].requiredLength} characters`;
    if (control.errors['maxlength']) return `Maximum ${control.errors['maxlength'].requiredLength} characters`;
    if (control.errors['min']) return `Minimum value is ${control.errors['min'].min}`;
    if (control.errors['max']) return `Maximum value is ${control.errors['max'].max} days (issuer validity)`;
    if (control.errors['pattern']) {
      if (fieldName === 'c') return 'Country must be 2 uppercase letters';
      return 'Invalid format';
    }
    return '';
  }

  hasError(fieldName: string): boolean {
    const control = this.intermediateForm.get(fieldName);
    return !!(control && control.invalid && control.touched);
  }

}
