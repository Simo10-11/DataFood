import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  email = '';
  password = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  login(): void {
    // Guardia base per evitare chiamate HTTP con campi vuoti.
    if (!this.email || !this.password) {
      return;
    }

    this.authService.login({
      email: this.email,
      password: this.password
    }).subscribe({
      next: (user) => {
        console.log('Login ok', user);
        // Se login ok, persistiamo l'utente e torniamo al catalogo.
        this.authService.saveCurrentUser(user);
        this.router.navigateByUrl('/');
      },
      error: (err) => {
        // Qui teniamo il log semplice per debug da console durante sviluppo.
        console.error('Errore login', err);
      }
    });
  }
}