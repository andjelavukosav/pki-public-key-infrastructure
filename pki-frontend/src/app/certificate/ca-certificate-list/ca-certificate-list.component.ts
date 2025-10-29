import { Component } from '@angular/core';
import { CertificateResponse } from '../model/certificate-response';
import { CertificateService } from '../service/certificate.service';
import { Router } from '@angular/router';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-ca-certificate-list',
  templateUrl: './ca-certificate-list.component.html',
  styleUrls: ['./ca-certificate-list.component.css']
})
export class CaCertificateListComponent {
 certificates: CertificateResponse[]=[];
  
    loading: boolean = true;
  
    constructor(private certifcateService: CertificateService, 
                private router: Router,
                private authService: UserService){}
  
    ngOnInit(): void {
      ///const currentUser = this.authService.getCurrentUser();
      //if (!currentUser) return;
      //const userId = currentUser.userId;
      this.certifcateService.getCACertificatesByOrg().subscribe({
        next:(data) =>{
          this.certificates=data;
          this.loading=false;
        },error: ()=>{
          this.loading=true;
        }
      })
    }
  
    createIntermediate(certificate: CertificateResponse): void{
      this.router.navigate(['create-intermediate'],{
        queryParams:{issuerId:certificate.id}
      });
    }


    createTemplate(cert: any) {
      this.router.navigate(['create-template'], {
      queryParams: { issuerId: cert.id }
    });
}
}
