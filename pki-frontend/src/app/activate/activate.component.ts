import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-activate',
  templateUrl: './activate.component.html',
  styleUrls: ['./activate.component.css']
})
export class ActivateComponent implements OnInit {
  loading = true;
  success = false;
  error = '';

  constructor(private route: ActivatedRoute, private auth: AuthService, private router: Router) {}

  ngOnInit(): void {
    const token =
      this.route.snapshot.queryParamMap.get('token')
      ?? this.route.snapshot.queryParamMap.get('code')
      ?? '';

    console.log('[ACTIVATE] token =', token, 'len=', token.length);

    if (!token) {
      this.loading = false;
      this.error = 'Nedostaje token u URL-u.';
      return;
    }

    this.auth.verify(token).subscribe({
      next: () => { this.success = true; this.loading = false; },
      error: (err) => {
        this.loading = false;
        this.error = (typeof err?.error === 'string') ? err.error : 'Link je nevalidan ili je istekao.';
      }
    });
  }

}
