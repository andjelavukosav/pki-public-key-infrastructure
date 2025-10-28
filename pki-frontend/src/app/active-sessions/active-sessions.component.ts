import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { SessionInfo } from '../model/session-info.model';
import { Component } from '@angular/core';
import { SessionService } from '../service/session.service';

@Component({
  selector: 'app-active-sessions',
  templateUrl: './active-sessions.component.html',
  styleUrls: ['./active-sessions.component.css']
})
export class ActiveSessionsComponent {
  sessions: SessionInfo[] = [];
  loading = true;
  currentSessionId: string | null = null;

  constructor(
    private sessionService: SessionService,
    private router: Router,
    private snackBar: MatSnackBar   // 👈 dodato
  ) {}

  ngOnInit(): void {
    this.currentSessionId = sessionStorage.getItem('currentSessionId');
    this.loadSessions();
  }

  loadSessions(): void {
    this.sessionService.getSessions().subscribe({
      next: (data) => {
        this.sessions = data.map(s => ({
          sessionId: s.sessionId,
          ipAddress: s.ipAddress,
          userAgent: s.userAgent,
          lastActivity: s.lastActivity,
          device: s.device,
          userId: s.userId
        }));
        this.loading = false;
      },
      error: (err) => {
        console.error('Greška pri učitavanju sesija', err);
        this.loading = false;
      }
    });
  }

  revoke(id: string): void {
    this.sessionService.revokeSession(id).subscribe({
      next: () => {
        this.sessions = this.sessions.filter(s => s.sessionId !== id);
        this.snackBar.open('Sesija uspješno opozvana.', 'Zatvori', {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snackbar-success']
        });
      },
      error: (err) => {
        console.error('Greška pri opozivu sesije', err);
        this.snackBar.open('Došlo je do greške pri opozivu sesije.', 'Zatvori', {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snackbar-error']
        });
      }
    });
  }

  revokeAll(): void {
    const currentSessionId = sessionStorage.getItem('currentSessionId'); 
    this.sessionService.revokeAllOtherSessions(currentSessionId!).subscribe({
      next: () => {
        this.sessions = this.sessions.filter(s => s.sessionId === currentSessionId);
        this.snackBar.open('Sve druge sesije su odjavljene.', 'Zatvori', {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top'
        });
      },
      error: (err) => {
        console.error('Greška pri opozivu sesije', err);
        this.snackBar.open('Došlo je do greške pri opozivu sesije.', 'Zatvori', {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snackbar-error']
        });
      }
    });
  }

  logout(): void {
    sessionStorage.removeItem('access-token');
    sessionStorage.removeItem('currentSessionId');
    this.router.navigate(['/login']);
  }
}
