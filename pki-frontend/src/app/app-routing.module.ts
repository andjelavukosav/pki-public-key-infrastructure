import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserRegistrationComponent } from './user-registration/user-registration.component';
import { ActivateComponent } from './activate/activate.component';

const routes: Routes = [
    { path: 'register', component: UserRegistrationComponent },
    { path: 'activate', component: ActivateComponent },

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
