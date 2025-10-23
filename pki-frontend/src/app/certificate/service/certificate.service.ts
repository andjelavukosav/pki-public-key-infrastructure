import { Injectable } from '@angular/core';
import { CertificateRequest } from '../model/certificateRequest';
import { CertificateResponse } from '../model/certificateResponse';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from 'src/app/service/auth.service';
import { Observable } from 'rxjs';
import { CertificateTemplate } from '../model/CertificateTemplate';
import { UserService } from 'src/app/service/user.service';

@Injectable({
  providedIn: 'root'
})
export class CertificateService {
  private apiUrl = 'http://localhost:8080/api/certificates';
  private apiTemplateUrl = 'http://localhost:8080/api/templates';

  constructor(private http: HttpClient, private authService: AuthService, private userService: UserService) { }

  issueCertificate(request: CertificateRequest) {
    return this.http.post<CertificateResponse>(`${this.apiUrl}/issue`, request,{headers: this.getAuthHeaders()});
  }

  createTemplate(template: CertificateTemplate) {
    const headers = this.authService.getAuthHeaders();
    const userId = this.userService.getAuthenticatedUser();

      const params = { userId: userId?.toString() || ''}
    return this.http.post(`${this.apiTemplateUrl}/create`, template, {params, headers});
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

  getAllTemplatesByIssuer(issuerId: number) {
  return this.http.get<CertificateTemplate[]>(`${this.apiTemplateUrl}/by-issuer/${issuerId}`);
}
  
  getCACertificatesByOrg(): Observable<any[]> {
    const headers = this.authService.getAuthHeaders();
    const userId = this.userService.getAuthenticatedUser()?.id;

    // 1) konvertuj broj u string
    const params = { userId: userId?.toString() || '' };

    return this.http.get<any[]>(`${this.apiUrl}/caOrg`, { headers, params });
  }

}
