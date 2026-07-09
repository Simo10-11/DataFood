# DataFood 🍔

Applicazione per la gestione e visualizzazione di prodotti alimentari in stile e-commerce, composta da un backend Spring Boot, un frontend Angular e un database MySQL.

## Requisiti per l'avvio

Prima di avviare il progetto assicurati di avere installato:

* Java 17, richiesto dal backend Spring Boot.
* Gradle, ma puoi usare direttamente il wrapper incluso nel progetto.
* Node.js con npm, necessario per il frontend Angular.
* Angular CLI 19.2.x.
* MySQL 8.0, in locale 

## Tecnologie usate

* Angular 19
* Spring Boot 3.4.4
* MySQL 8.0

## Avvio rapido

### Backend

Dal folder `IngSW2026-BE`:

1. Avvia MySQL in locale.
2. Avvia il backend con `./gradlew bootRun` .

### Frontend

Dal folder `IngSW2026-FE`:

1. Installa le dipendenze con `npm install`.
2. Avvia l'app con `npm start`.

## Nota sul database

La configurazione del backend punta a un database MySQL locale su `localhost:3306`. Verifica che nome database, utente e password siano allineati con quelli indicati in `application.yaml`.

## Autori

* Elisabetta - https://github.com/elisabettabaghiu
* Simone - https://github.com/Simo10-11


