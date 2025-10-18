import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-csr-request',
  templateUrl: './csr-request.component.html',
  styleUrls: ['./csr-request.component.css']
})
export class CsrRequestComponent {
  selectedFile: File | null = null;
  selectedCA: string = '';
  duration: number = 3650; // 10 godina
  caList: any[] = [];

  successMessage: string = '';

  constructor(private http: HttpClient) {}

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  onSubmit() {
    if (!this.selectedFile) {
      alert('Morate uploadovati CSR fajl!');
      return;
    }

    const formData = new FormData();
    formData.append('csrFile', this.selectedFile);
    formData.append('caId', this.selectedCA);
    formData.append('duration', this.duration.toString());

    this.http.post('http://localhost:8080/api/csr/upload', formData, { responseType: 'text' })
      .subscribe({
        next: (res) => this.successMessage = 'Zahtev uspešno poslat! ' + res,
        error: (err) => alert('Greška prilikom slanja zahteva: ' + err.message)
      });
  }
}
