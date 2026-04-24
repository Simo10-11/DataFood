import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  email = '';
  password = '';
  nome = '';
  cognome = '';
  successMessage = '';
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  register(): void {
    // Guardia base per evitare chiamate HTTP con campi vuoti o undefined.
    if (!this.email || !this.password || !this.nome || !this.cognome) {
      return;
    }

    this.successMessage = '';
    this.errorMessage = '';

    this.authService.register({
      email: this.email,
      password: this.password,
      nome: this.nome,
      cognome: this.cognome
    }).subscribe({
      next: (user) => {
        console.log('Registrazione ok', user);
        // Login automatico dopo registrazione riuscita.
        this.authService.saveCurrentUser(user);
        this.successMessage = 'Registrazione completata. Reindirizzamento alla home...';
        this.router.navigateByUrl('/');
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 400) {
          this.errorMessage = 'Email gia registrata. Usa una email diversa.';
          return;
        }

        this.errorMessage = 'Errore registrazione. Riprova tra poco.';
        console.error('Errore registrazione', err);
      }
    });
  }
}