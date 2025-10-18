import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserRegistrationComponent } from './user-registration/user-registration.component';
import { CsrRequestComponent } from './componets/csr-request/csr-request.component';

const routes: Routes = [
    { path: 'register', component: UserRegistrationComponent },
    { path: 'csr-request', component: CsrRequestComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
