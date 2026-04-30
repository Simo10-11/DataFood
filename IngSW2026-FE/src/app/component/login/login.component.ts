import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs/operators';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  // Campi del form di login
  email = '';
  password = '';
  showPassword = false; // Toggle mostra/nascondi password
  isLoading = false;    // Indica se la richiesta HTTP è in corso
  errorMessage = '';    // Messaggio di errore da mostrare all'utente

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  // Handler del form submit: valida prima di fare il login
  onSubmit(form: NgForm): void {
    this.errorMessage = '';

    if (form.invalid || this.isLoading) {
      return; // Se il form non è valido o è già in caricamento, non fare nulla
    }

    this.login();
  }

  // Esegue la chiamata di login al backend
  login(): void {
    // Controlla che i dati siano presenti e che non ci sia già una richiesta in corso
    if (!this.email || !this.password || this.isLoading) {
      this.errorMessage = 'Inserisci email e password per accedere.';
      return;
    }

    this.errorMessage = '';
    this.isLoading = true; // Disabilita il bottone durante la richiesta

    this.authService.login({
      email: this.email,
      password: this.password
    }).pipe(
      finalize(() => {
        this.isLoading = false;
      })
    ).subscribe({
      next: (user) => {
        console.log('Login ok', user);
        // Se login ok, persistiamo l'utente e torniamo al catalogo.
        this.authService.saveCurrentUser(user);
        this.router.navigateByUrl('/');
      },
      error: (err) => {
        // Gestisce gli errori e mostra messaggi appropriati all'utente
        console.error('Errore login', err);

        const httpError = err as HttpErrorResponse;

        // Credenziali non valide
        if (httpError.status === 401 || httpError.status === 403) {
          this.errorMessage = 'Email o password errate. Controlla le credenziali e riprova.';
          return;
        }

        // Errore di connessione
        if (httpError.status === 0) {
          this.errorMessage = 'Impossibile contattare il server. Verifica la connessione e riprova.';
          return;
        }

        // Errore generico del server
        this.errorMessage = 'Si è verificato un errore durante il login. Riprova tra qualche istante.';
      }
    });
  }
}
