import { APP_INITIALIZER, ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {provideHttpClient} from '@angular/common/http';
import { AuthService } from './service/auth.service';

export function initializeAuthSession(authService: AuthService): () => Promise<void> {
  return () => new Promise<void>((resolve) => {
    // All avvio riallineo il backend con l utente che il browser ha già salvato
    authService.restoreSession().subscribe({
      next: () => resolve(),
      error: () => resolve()
    });
  });
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(),
    {
      provide: APP_INITIALIZER,
      useFactory: initializeAuthSession,
      deps: [AuthService],
      multi: true
    }
  ]
};
