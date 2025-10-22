import { Component, OnInit } from '@angular/core';
import { CertificateService } from 'src/app/certificate/service/certificate.service';
import { CsrService } from 'src/app/service/csr.services';
import { CertificateResponse } from 'src/app/certificate/model/certificateResponse';

@Component({
  selector: 'app-csr-request',
  templateUrl: './csr-request.component.html',
  styleUrls: ['./csr-request.component.css']
})
export class CsrRequestComponent implements OnInit {
  csrFile: File | null = null;
  selectedCA: number = 0;
  duration: number = 0;
  successMessage: string = '';
  errorMessage: string = '';

  caList: CertificateResponse[] = [];

  constructor(private certificateService: CertificateService, private csrService: CsrService) {}

  ngOnInit() {
    this.loadCAList();
  }

  loadCAList() {
    this.certificateService.getCertificates().subscribe({
      next: (certificates) => {
        this.caList = certificates.filter(cert => cert.ca === true);
      },
      error: () => {
        this.errorMessage = 'Failed to load CA list';
      }
    });
  }

  onFileSelected(event: any) {
    this.csrFile = event.target.files[0];
  }

  onSubmit() {
    if (!this.csrFile) return;

    this.csrService.uploadCSR(this.csrFile, this.selectedCA, this.duration)
      .subscribe({
        next: (res: any) => {
          this.successMessage = res.message || 'Uploaded successfully!';
        },
        error: (err: any) => {
          this.errorMessage = err.error || 'Upload failed';
        }
      });
  }
}
