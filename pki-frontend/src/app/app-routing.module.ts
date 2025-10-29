import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserRegistrationComponent } from './user-registration/user-registration.component';
import { CsrRequestComponent } from './componets/csr-request/csr-request.component';
import { UserLoginComponent } from './user-login/user-login.component';
import { UserHomeComponent } from './user-home/user-home.component';
import { ActivateComponent } from './activate/activate.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { CreateRootCertificateComponent } from './certificate/create-root-certificate/create-root-certificate.component';
import { CreateIntermediateCertificateComponent } from './certificate/create-intermediate-certificate/create-intermediate-certificate.component';
import { CertificateListComponent } from './certificate/certificate-list/certificate-list.component';
import { PendingCsrListComponent } from './componets/pending-csr-list/pending-csr-list.component';
import { CaCertificateListComponent } from './certificate/ca-certificate-list/ca-certificate-list.component';
import { CreateTemplateComponent } from './certificate/create-template/create-template.component';
import { ActiveSessionsComponent } from './active-sessions/active-sessions.component';
import { EeCertificateListComponent } from './certificate/ee-certificate-list/ee-certificate-list.component';

const routes: Routes = [
    { path: 'register', component: UserRegistrationComponent },
    { path: 'activate', component: ActivateComponent },
    { path: 'createRootCertificate', component:CreateRootCertificateComponent},
    {path: 'create-intermediate', component:CreateIntermediateCertificateComponent},
    { path: 'certificateList', component:CertificateListComponent},
    { path: 'login', component: UserLoginComponent},
    { path: 'user-home', component: UserHomeComponent},
    { path: 'csr-request', component: CsrRequestComponent },
    { path: 'csr-pending', component: PendingCsrListComponent},
    { path: 'forgot-password', component: ForgotPasswordComponent},
    { path: 'reset-password', component: ResetPasswordComponent},
    { path: 'ca-certificateList', component:CaCertificateListComponent},
    { path: 'active-sessions', component: ActiveSessionsComponent},
    { path: 'create-template', component:CreateTemplateComponent},
    { path: 'ee-certificateList', component:EeCertificateListComponent},
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    //{ path: '**', redirectTo: 'login' },

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
