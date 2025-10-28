import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from 'src/app/service/auth.service';
import { CertificateSigningRequest } from '../certificate/model/certificate-signing-request.model';
import { CSRDecisionDTO } from '../componets/model/csr-decision-dto.model';

@Injectable({
  providedIn: 'root'
})
export class CsrService {

  private apiUrl = 'http://localhost:8080/api/csr';

  constructor(private http: HttpClient, private authService: AuthService) { }

  uploadCSR(formData: FormData): Observable<any>{
    const headers = this.authService.getAuthHeaders()
    return this.http.post<any>(`${this.apiUrl}/upload`, formData, {headers});
  }

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    let headers = new HttpHeaders();
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    return headers;
  }

  getPendingCSRs(userId: number) {
    return this.http.get<CertificateSigningRequest[]>(`${this.apiUrl}/pending`, {
      params: { userId: userId.toString() },
      headers: this.authService.getAuthHeaders()
    });
  }

  processCSRDecision(decision: CSRDecisionDTO) {
    const userId = this.authService.getCurrentUser()?.userId
    return this.http.post(`${this.apiUrl}/process`, decision, {
      params: { userId: userId?.toString() || "" },
      headers: this.authService.getAuthHeaders()
    });
  }
}
