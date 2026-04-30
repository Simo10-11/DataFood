import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs/operators';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  // Campi del form di registrazione
  email = '';
  password = '';
  showPassword = false; // Toggle mostra/nascondi password
  nome = '';
  cognome = '';
  isLoading = false;    // Indica se la richiesta HTTP \u00e8 in corso
  successMessage = '';  // Messaggio di successo
  errorMessage = '';    // Messaggio di errore

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  // Handler del form submit: valida i dati prima di registrarsi
  onSubmit(form: NgForm): void {
    this.successMessage = '';
    this.errorMessage = '';

    if (form.invalid || this.isLoading) {
      return; // Se il form non \u00e8 valido o \u00e8 gi\u00e0 in caricamento, non procedere
    }

    this.register();
  }

  // Esegue la registrazione dell'utente
  register(): void {
    // Controlla che tutti i campi siano compilati
    if (!this.email || !this.password || !this.nome || !this.cognome || this.isLoading) {
      this.errorMessage = 'Compila tutti i campi obbligatori prima di continuare.';
      return;
    }

    this.successMessage = '';
    this.errorMessage = '';
    this.isLoading = true; // Disabilita il bottone durante la richiesta

    this.authService.register({
      email: this.email,
      password: this.password,
      nome: this.nome,
      cognome: this.cognome
    }).pipe(
      finalize(() => {
        this.isLoading = false;
      })
    ).subscribe({
      next: (user) => {
        // Registrazione e login automatico riusciti
        console.log('Registrazione ok', user);
        this.authService.saveCurrentUser(user);
        this.successMessage = 'Registrazione completata. Reindirizzamento alla home...';
        this.router.navigateByUrl('/');
      },
      error: (err: HttpErrorResponse) => {
        // Gestisce i vari tipi di errori nella registrazione
        
        // Email gi\u00e0 registrata
        if (err.status === 409 || err.status === 400) {
          this.errorMessage = 'Questa email risulta gia registrata. Prova ad accedere o usa un\'altra email.';
          return;
        }

        // Dati non validi (es. password troppo corta, email malformata)
        if (err.status === 422) {
          this.errorMessage = 'Dati non validi. Controlla i campi inseriti e riprova.';
          return;
        }

        // Errore di connessione
        if (err.status === 0) {
          this.errorMessage = 'Impossibile contattare il server. Controlla la connessione e riprova.';
          return;
        }

        // Errore generico
        this.errorMessage = 'Errore durante la registrazione. Riprova tra poco.';
        console.error('Errore registrazione', err);
      }
    });
  }
}