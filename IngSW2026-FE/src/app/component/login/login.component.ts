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
export class LoginComponent implements OnInit {
  private readonly rememberedEmailKey = 'datafood.rememberedEmail';

  email = '';
  password = '';
  rememberMe = false;
  isLoading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const rememberedEmail = localStorage.getItem(this.rememberedEmailKey);
    if (rememberedEmail) {
      this.email = rememberedEmail;
      this.rememberMe = true;
    }
  }

  onSubmit(form: NgForm): void {
    this.errorMessage = '';

    if (form.invalid || this.isLoading) {
      return;
    }

    this.login();
  }

  login(): void {
    // Guardia base per evitare chiamate HTTP con campi vuoti o submit multipli.
    if (!this.email || !this.password || this.isLoading) {
      this.errorMessage = 'Inserisci email e password per accedere.';
      return;
    }

    this.errorMessage = '';
    this.isLoading = true;

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

        // Salviamo l'email solo se richiesto dalla checkbox "Ricordami".
        if (this.rememberMe) {
          localStorage.setItem(this.rememberedEmailKey, this.email);
        } else {
          localStorage.removeItem(this.rememberedEmailKey);
        }

        // Se login ok, persistiamo l'utente e torniamo al catalogo.
        this.authService.saveCurrentUser(user);
        this.router.navigateByUrl('/');
      },
      error: (err) => {
        console.error('Errore login', err);

        const httpError = err as HttpErrorResponse;

        if (httpError.status === 401 || httpError.status === 403) {
          this.errorMessage = 'Email o password errate. Controlla le credenziali e riprova.';
          return;
        }

        if (httpError.status === 0) {
          this.errorMessage = 'Impossibile contattare il server. Verifica la connessione e riprova.';
          return;
        }

        this.errorMessage = 'Si è verificato un errore durante il login. Riprova tra qualche istante.';
      }
    });
  }
}
