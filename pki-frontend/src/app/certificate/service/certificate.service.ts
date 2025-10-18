import { Injectable } from '@angular/core';
import { CertificateRequest } from '../model/certificateRequest';
import { CertificateResponse } from '../model/certificateResponse';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from 'src/app/service/auth.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CertificateService {
  private apiUrl = 'http://localhost:8080/api/certificates';

  constructor(private http: HttpClient, private authService: AuthService) { }

  issueCertificate(request: CertificateRequest) {
    return this.http.post<CertificateResponse>(`${this.apiUrl}/issue`, request,{headers: this.getAuthHeaders()});
  }

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    let headers = new HttpHeaders();
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    return headers;
  }

  getCertificateById(id: number): Observable<CertificateResponse> {
    return this.http.get<CertificateResponse>(`${this.apiUrl}/id/${id}`,
      { headers: this.getAuthHeaders() }
    );
  }

  getCertificates(): Observable<CertificateResponse[]>{
    return this.http.get<CertificateResponse[]>(`${this.apiUrl}/all`,{headers:this.getAuthHeaders()});
  }
}
