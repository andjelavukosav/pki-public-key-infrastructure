import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { UserRegistrationComponent } from './user-registration/user-registration.component';
import { HomePageComponent } from './home-page/home-page.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NavBarComponent } from './nav-bar/nav-bar.component';
import { CsrRequestComponent } from './componets/csr-request/csr-request.component';
import { HTTP_INTERCEPTORS, HttpClient, HttpClientModule } from '@angular/common/http';
import { ActivateComponent } from './activate/activate.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { CreateRootCertificateComponent } from './certificate/create-root-certificate/create-root-certificate.component';
import { CreateIntermediateCertificateComponent } from './certificate/create-intermediate-certificate/create-intermediate-certificate.component';
import { UserLoginComponent } from './user-login/user-login.component';
import { RecaptchaModule } from 'ng-recaptcha';
import { AuthInterceptor } from './auth/interceptor';

import { UserHomeComponent } from './user-home/user-home.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import {MatDividerModule} from '@angular/material/divider'
import { CertificateListComponent } from './certificate/certificate-list/certificate-list.component';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import { CreateTemplateComponent } from './certificate/create-template/create-template.component'
import{MatCheckboxModule} from '@angular/material/checkbox';
import { CaCertificateListComponent } from './certificate/ca-certificate-list/ca-certificate-list.component'


@NgModule({
  declarations: [
    AppComponent,
    UserRegistrationComponent,
    HomePageComponent,
    NavBarComponent,
    CsrRequestComponent,
    ActivateComponent,
    UserLoginComponent,
    UserHomeComponent,
    ActivateComponent,
    ForgotPasswordComponent,
    ResetPasswordComponent,
    CreateRootCertificateComponent,
    CreateIntermediateCertificateComponent,
    CertificateListComponent,
    CreateTemplateComponent,
    CaCertificateListComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    FormsModule,
    BrowserAnimationsModule,
    MatSnackBarModule,
    MatTableModule,   
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    RecaptchaModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatSlideToggleModule,
    MatDividerModule, 
    MatCheckboxModule

  ],
  providers: [{ provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}],
  bootstrap: [AppComponent]
})
export class AppModule { }
