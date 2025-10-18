import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { UserRegistrationComponent } from './user-registration/user-registration.component';
import { HomePageComponent } from './home-page/home-page.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { NavBarComponent } from './nav-bar/nav-bar.component';
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

@NgModule({
  declarations: [
    AppComponent,
    UserRegistrationComponent,
    HomePageComponent,
    NavBarComponent,
    ActivateComponent,
    CreateRootCertificateComponent,
    CreateIntermediateCertificateComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MatSnackBarModule,
    MatTableModule,   // ⬅️ OVO je ključno
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule


  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
