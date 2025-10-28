import { HttpClient } from "@angular/common/http";
import { SessionInfo } from "../model/session-info.model";
import { Observable } from "rxjs";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  private apiUrl = 'http://localhost:8080/api/sessions'; 

  constructor(private http: HttpClient) {}

  getSessions(): Observable<SessionInfo[]> {
    return this.http.get<SessionInfo[]>(this.apiUrl);
  }

  revokeSession(sessionId: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/revoke?sessionId=${sessionId}`, null);
  }

  
  revokeAllOtherSessions(currentSessionId: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/revoke-all-others?currentSessionId=${currentSessionId}`, null);
  }
}