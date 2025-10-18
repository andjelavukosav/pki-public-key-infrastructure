import { Component } from '@angular/core';
import { CertificateResponse } from '../model/certificateResponse';
import { CertificateService } from '../service/certificate.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-certificate-list',
  templateUrl: './certificate-list.component.html',
  styleUrls: ['./certificate-list.component.css']
})
export class CertificateListComponent {
  certificates: CertificateResponse[]=[];

  loading: boolean = true;

  constructor(private certifcateService: CertificateService, private router: Router){}

  ngOnInit(): void {
    this.certifcateService.getCertificates().subscribe({
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

  createEndEntity(certificate: CertificateResponse): void{
    this.router.navigate(['create-endEntity'],{
      queryParams:{issuerId:certificate.id}
    });
  }
}
