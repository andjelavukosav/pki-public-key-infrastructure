import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserRegistrationComponent } from './user-registration/user-registration.component';
import { ActivateComponent } from './activate/activate.component';
import { CreateRootCertificateComponent } from './certificate/create-root-certificate/create-root-certificate.component';

const routes: Routes = [
    { path: 'register', component: UserRegistrationComponent },
    { path: 'activate', component: ActivateComponent },
    { path: 'createRootCertificate', component:CreateRootCertificateComponent},


];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
