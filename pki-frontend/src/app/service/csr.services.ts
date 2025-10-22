import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from 'src/app/service/auth.service';

@Injectable({
  providedIn: 'root'
})
export class CsrService {

  private apiUrl = 'http://localhost:8080/api/csr';

  constructor(private http: HttpClient, private authService: AuthService) { }

  uploadCSR(csrFile: File, selectedCaId: number, duration: number): Observable<any> {
    const formData = new FormData();
    formData.append('csrFile', csrFile);
    formData.append('selectedCaId', selectedCaId.toString());
    formData.append('requestedDurationDays', duration.toString());

    return this.http.post(`${this.apiUrl}/upload`, formData, { headers: this.getAuthHeaders() });
  }

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    let headers = new HttpHeaders();
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    return headers;
  }
}
