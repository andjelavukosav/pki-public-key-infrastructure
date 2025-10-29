import { Component, OnInit } from '@angular/core';
import { CertificateResponse } from '../model/certificate-response';
import { CertificateService } from '../service/certificate.service';
import { Router } from '@angular/router';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-ee-certificate-list',
  templateUrl: './ee-certificate-list.component.html',
  styleUrls: ['./ee-certificate-list.component.css']
})
export class EeCertificateListComponent implements OnInit {

  certificates: CertificateResponse[] = [];
  loading: boolean = true;

  // Standardni razlozi revokacije
  revocationReasons = [
    { value: 'keyCompromise', label: 'Key Compromised' },
    { value: 'cACompromise', label: 'CA Compromised' },
    { value: 'affiliationChanged', label: 'Affiliation Changed' },
    { value: 'superseded', label: 'Superseded' },
    { value: 'cessationOfOperation', label: 'Cessation of Operation' },
    { value: 'unspecified', label: 'Unspecified' }
  ];

  constructor(
    private certificateService: CertificateService,
    private router: Router,
    private authService: UserService
  ) {}

  ngOnInit(): void {
    this.certificateService.getCACertificatesByUser().subscribe({
      next: (data) => {
        this.certificates = data.map(cert => ({
          ...cert,
          showReasonInput: false,
          revocationReason: this.revocationReasons[0].value // default razlog
        }));
        this.loading = false;
      },
      error: () => this.loading = true
    });
  }

  revoke(certificate: CertificateResponse, reason: string): void {
    if (!reason) {
      alert('Please select a reason for revocation.');
      return;
    }

    this.certificateService.revokeCertificate(certificate.id, reason).subscribe({
      next: () => {
        alert('Certificate revoked successfully');
        certificate.revoked = true; // update lokalnog UI-a
      },
      error: (err) => {
        alert('Failed to revoke certificate: ' + err.error);
      }
    });
  }

  onDownload(certId: number) {
    this.certificateService.downloadCertificate(certId).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `certificate-${certId}.cer`; // ime fajla
      link.click();
      window.URL.revokeObjectURL(url);
    });
  }
}
