# Sistema di Gamification - Punti Fedeltà

## Panoramica

Sistema completo di gamification integrato in DataFood con punti fedeltà. Gli utenti accumulano punti in base agli ordini effettuati e possono utilizzarli come sconto nei checkout successivi.

---

## Architettura del Sistema

### Database Schema
Modifiche al database:
- **Tabella `UTENTE`**: Aggiunti campi `PUNTI_TOTALI` (INT, default 0) e `PUNTI_DISPONIBILI` (INT, default 0)

### Backend (Java/Spring Boot)

#### Entità e DTO

1. **Utente Entity** (`src/main/java/.../model/Utente.java`)
   - Aggiunto: `Integer puntiTotali` (default 0)
   - Aggiunto: `Integer puntiDisponibili` (default 0)

2. **UtenteDTO** (`src/main/java/.../dto/UtenteDTO.java`)
   - Aggiunto: `Integer puntiTotali`
   - Aggiunto: `Integer puntiDisponibili`

3. **OrderDTO** (`src/main/java/.../dto/OrderDTO.java`)
   - Aggiunto: `Integer puntiGuadagnati` - Punti guadagnati con questo ordine
   - Aggiunto: `Integer puntiUtilizzati` - Punti usati come sconto
   - Aggiunto: `Double scontoApplicato` - Euro di sconto applicato

4. **CheckoutRequestDTO** (`src/main/java/.../dto/CheckoutRequestDTO.java`)
   - `boolean usePunti` - Flag per indicare se usare i punti come sconto
   - `Integer puntiDaUtilizzare` - (Opzionale per future espansioni)

5. **DiscountPreviewDTO** (`src/main/java/.../dto/DiscountPreviewDTO.java`)
   - Preview dello sconto prima del checkout

#### Servizi

**PuntiService** (`src/main/java/.../service/PuntiService.java`)
- Servizio dedicato alla gestione dei punti
- Metodi principali:
  - `calculatePuntiGuadagnati(BigDecimal importoOrdine)` - Calcola punti guadagnati (1 punto ogni €20)
  - `aggiungiPunti(Utente utente, int puntiGuadagnati)` - Incrementa punti totali e disponibili
  - `convertPuntiToEuro(int punti)` - Converte punti in euro (1:1)
  - `applicaScontoWithPunti(int puntiDisponibili, BigDecimal totaleOrdine)` - Calcola sconto massimo applicabile
  - `usaPunti(Utente utente, int puntiUsati)` - Decrementa punti disponibili (SOLO disponibili, non totali)

**OrderService** (`src/main/java/.../service/OrderService.java`)
- Aggiunto `@Transactional` al metodo `checkout()` per garantire consistenza
- Nuovo metodo `checkoutWithPoints(HttpSession session, boolean usePunti)`:
  - Calcola totale ordine
  - Se `usePunti=true`, applica massimo sconto possibile
  - Calcola punti guadagnati (sul totale DOPO lo sconto)
  - Usa i punti disponibili e aggiunge i punti guadagnati
- Nuovo metodo `previewDiscount(HttpSession session)`:
  - Ritorna anteprima dello sconto senza confermare l'ordine

#### Controller

**OrderController** (`src/main/java/.../controller/OrderController.java`)
- Endpoint modificato: `POST /api/orders/checkout`
  - Ora accetta opzionale `CheckoutRequestDTO` nel body
  - Se `usePunti=true`, applica logica di sconto
  - Backward compatible con body vuoto
- Nuovo endpoint: `GET /api/orders/preview-discount`
  - Ritorna anteprima dello sconto per il carrello attuale
  - Nessuna modifica di stato

### Frontend (Angular)

#### Modelli

1. **Utente Model** (`src/app/dto/utente.model.ts`)
   - Aggiunto: `puntiTotali?: number`
   - Aggiunto: `puntiDisponibili?: number`

2. **Order Model** (`src/app/dto/order.model.ts`)
   - Aggiunto: `puntiGuadagnati?: number`
   - Aggiunto: `puntiUtilizzati?: number`
   - Aggiunto: `scontoApplicato?: number`

#### Servizi

**OrderService** (`src/app/service/order.service.ts`)
- Metodo `checkout(usePunti: boolean)` - Modificato per inviare flag di utilizzo punti
- Nuovo metodo `previewDiscount()` - Carica anteprima dello sconto
- Nuova interfaccia `DiscountPreview` con dati di anteprima

#### Componenti

**CartComponent** (`src/app/component/cart/cart.component.ts`)
- Nuovo campo: `usePunti: boolean` - Checkbox per usare i punti
- Nuovo campo: `discountPreview: DiscountPreview | null` - Dati dello sconto
- Nuovo campo: `currentUser: Utente | null` - Dati utente loggato
- Nuovo metodo: `onUsePuntiChange()` - Carica preview quando checkbox viene selezionato
- Nuovo metodo: `loadDiscountPreview()` - Fetch del preview dal backend
- Metodo `checkout()` aggiornato con logica di punti

**NavbarComponent** (`src/app/component/navbar/navbar.component.html`)
- Aggiunta sezione "Punti Fedeltà" nel menu utente
- Mostra punti disponibili e valore in euro (quando > 0)

---

## Regole di Gamification

### Accumulo Punti
- **Rapporto**: 1 punto ogni €20 spesi
- **Calcolo**: Per difetto usando `Math.floor(totale / 20)`
- **Esempi**:
  - €19.99 → 0 punti
  - €20.00 → 1 punto
  - €39.99 → 1 punto
  - €40.00 → 2 punti
  - €100.00 → 5 punti

### Utilizzo Punti
- **Rapporto di conversione**: 1 punto = €1 di sconto
- **Limite massimo**: Sconto non può superare il totale dell'ordine
- **Applicazione**: Scalati SOLO dai `puntiDisponibili`, NON da `puntiTotali`

### Punti Totali
- **Incrementano sempre**: Con ogni ordine
- **Non diminuiscono mai**: Traccia storica della fedeltà
- **Accessibili all'utente**: Visibili nel menu del profilo

### Punti Disponibili
- **Incrementano**: Quando vengono guadagnati in un ordine
- **Decrementano**: Solo quando usati come sconto nel checkout
- **Usabili**: Solo quantità disponibili possono essere usate

### Calcolo Punti con Sconto
Quando l'utente applica lo sconto punti:
1. Calcola totale ordine originale
2. Applica sconto punti (massimo i punti disponibili)
3. Nuovo totale = totale originale - sconto
4. **I punti guadagnati si calcolano sul NUOVO totale** (dopo lo sconto)
5. Punti usati vengono decrementati dai disponibili
6. Punti guadagnati vengono aggiunti a totali E disponibili

---

## Flusso Utente

### Registrazione
```
Nuovo utente
    ↓
puntiTotali = 0
puntiDisponibili = 0
    ↓
Pronto per accumulo punti
```

### Acquisto
```
Ordine confermato
    ↓
Calcolo: puntiGuadagnati = floor(totale / 20)
    ↓
Aggiornamento utente:
  puntiTotali += puntiGuadagnati
  puntiDisponibili += puntiGuadagnati
    ↓
Messaggio: "Ordine confermato! Hai guadagnato X punti"
    ↓
Frontend aggiorna punti nel menu
```

### Checkout con Punti
```
Carrello con articoli
    ↓
[☐] Usa i miei punti come sconto
    ↓
Se selezionato:
  - Fetch preview dello sconto
  - Mostra totale originale
  - Mostra sconto applicabile
  - Mostra nuovo totale
    ↓
Click Checkout
    ↓
Backend:
  1. Applica massimo sconto possibile
  2. Calcola totale finale
  3. Salva ordine con nuovo totale
  4. Decrementa puntiDisponibili
  5. Calcola punti guadagnati sul totale FINALE
  6. Incrementa punti
    ↓
Frontend:
  - Mostra "Ordine confermato!"
  - "Hai guadagnato X punti"
  - "Sconto applicato: €Y"
```

---

## Endpoint API

### Checkout con Punti
**POST** `/api/orders/checkout`

**Request Body** (opzionale):
```json
{
  "usePunti": true
}
```

**Response**:
```json
{
  "id": 123,
  "nomeCliente": "Mario Rossi",
  "data": "2026-05-11T14:30:00",
  "status": "in_lavorazione",
  "totale": 35.50,
  "items": [...],
  "puntiGuadagnati": 1,
  "puntiUtilizzati": 0,
  "scontoApplicato": 0.0
}
```

### Preview Sconto
**GET** `/api/orders/preview-discount`

**Response**:
```json
{
  "totaleOrdine": 45.99,
  "puntiDisponibili": 150,
  "valorePuntiInEuro": 150.00,
  "scontoApplicabile": 45.99,
  "totaleConSconto": 0.00,
  "puntiUtilizzabili": 45
}
```

---

## Gestione dei Case Edge

### 1. Utente Senza Punti
- Checkbox "Usa punti" non visibile in checkout
- Nessun impatto su flusso di checkout
- Ordine confermato normalmente

### 2. Ordine a €0
- Se totale diventerebbe €0 con sconto: totale = €0.00 (non negativo)
- Punti rimanenti restano disponibili
- Utente riceve 0 punti da questo ordine

### 3. Punti Negativi
- Impossibile: validazione backend impedisce
- `puntiDisponibili` mai sceso sotto 0 (usar `Math.max()`)

### 4. Doppia Applicazione Sconto
- Prevenuto da sessione HTTP (carrello associato a utente)
- Backend verifica autenticazione per ogni operazione
- Transazione atomica garantisce consistenza

### 5. Race Condition
- `@Transactional` su `checkout()` e `checkoutWithPoints()`
- Database locks gestiti da JPA/Hibernate
- Punti garantiti consistenti anche con richieste parallele

### 6. Punti Non Sufficienti
- Backend calcola automaticamente massimo sconto applicabile
- Non tira errore: applica tutto quello che può
- Punti rimanenti restano disponibili

---

## Testing

### Test Unitari Backend (PuntiService)
```java
// Calcolo punti
Assert.assertEquals(1, puntiService.calculatePuntiGuadagnati(BigDecimal.valueOf(20)));
Assert.assertEquals(1, puntiService.calculatePuntiGuadagnati(BigDecimal.valueOf(39.99)));
Assert.assertEquals(2, puntiService.calculatePuntiGuadagnati(BigDecimal.valueOf(40)));

// Conversione euro
Assert.assertEquals(150.0, puntiService.convertPuntiToEuro(150));

// Sconto massimo
DiscountResult result = puntiService.applicaScontoWithPunti(100, BigDecimal.valueOf(45.99));
Assert.assertEquals(45, result.puntiUtilizzati);
Assert.assertEquals(45.99, result.scontoApplicato);
```

### Test Integrazione
1. **Registrazione utente**: Verifica `puntiTotali=0, puntiDisponibili=0`
2. **Primo ordine**: Ordine da €40 → 2 punti guadagnati
3. **Visualizzazione**: Menu mostra 2 punti e €2 di sconto
4. **Checkout con punti**: Ordine da €50, applica €2 sconto → €48 totale
5. **Punti finali**: 2-2=0 disponibili, 2+2=4 totali

### Test Frontend
1. Carrello con articoli a €60
2. User ha 50 punti disponibili
3. Seleziona checkbox "Usa punti"
4. Preview mostra: sconto €50, totale €10
5. Confirm checkout
6. Messaggio: "Guadagnato 0 punti" (€10 = 0 punti per difetto)

---

## Struttura File Modificati

### Backend
```
IngSW2026-BE/
├── src/main/java/it/unife/sample/backend/
│   ├── model/
│   │   └── Utente.java                  (+punti fields)
│   ├── dto/
│   │   ├── UtenteDTO.java              (+punti fields)
│   │   ├── OrderDTO.java               (+punti fields)
│   │   ├── CheckoutRequestDTO.java     (NEW)
│   │   └── DiscountPreviewDTO.java     (NEW)
│   ├── service/
│   │   ├── OrderService.java           (@Transactional + checkoutWithPoints)
│   │   └── PuntiService.java           (NEW)
│   ├── controller/
│   │   └── OrderController.java        (updated checkout endpoint)
│   └── mapper/
│       └── UtenteMapper.java           (+mappings)
├── script.sql                           (ALTER TABLE UTENTE)
```

### Frontend
```
IngSW2026-FE/
├── src/app/
│   ├── dto/
│   │   ├── utente.model.ts             (+punti fields)
│   │   └── order.model.ts              (+punti fields)
│   ├── service/
│   │   └── order.service.ts            (+checkout with points, preview)
│   ├── component/
│   │   ├── cart/
│   │   │   ├── cart.component.ts       (+punti logic)
│   │   │   ├── cart.component.html     (+punti UI)
│   │   │   └── cart.component.scss     (+punti styling)
│   │   └── navbar/
│   │       ├── navbar.component.html   (+punti display)
│   │       └── navbar.component.scss   (+punti styling)
```

---

## Deploying

### Prerequisiti
1. Database aggiornato con nuovo schema (script.sql)
2. Rebuild del backend: `./gradlew clean build`
3. Rebuild del frontend: `npm run build`

### Passi di Deploy
1. Backup database
2. Eseguire `script.sql` (aggiunge colonne con default 0)
3. Deploy backend build
4. Deploy frontend build
5. Verificare endpoints con Postman/curl

### Rollback
Se necessario, rimuovere i campi:
```sql
ALTER TABLE UTENTE DROP COLUMN PUNTI_TOTALI;
ALTER TABLE UTENTE DROP COLUMN PUNTI_DISPONIBILI;
```

---

## Estensibilità Futura

Il sistema è progettato per essere facilmente estendibile:

### Feature Possibili
1. **Classifiche utenti**: Ranking per punti totali
2. **Premi speciali**: Bonus punti per determinate categorie/periodi
3. **Livelli VIP**: Moltiplicatori basati su punti totali
4. **Espirazione punti**: Punti disponibili con scadenza
5. **Redenzione alternativa**: Coupon, prodotti esclusivi, spedizione gratis
6. **Animazioni**: Notifiche quando punti aumentano

### Struttura per Estensione
- `PuntiService` è già separata e riutilizzabile
- `OrderDTO` ha campi per punti (easy to add more)
- `UtenteDTO` aggiunge info utente (add reputation, level, ecc.)
- Frontend ha struttura modulare per aggiungere sezione "Punti Fedeltà" completa

### Aggiunte Consigliate
```typescript
// Futura: enum di tipo premio
enum TipoPunti {
  STANDARD,           // Ordini normali
  BONUS_CATEGORIA,    // Promo categoria
  BONUS_REFERRAL,     // Invita amici
  BONUS_ANNIVERSARIO  // Compleanno
}

// Futura: history dei punti
interface PuntiTransaction {
  id: number;
  userId: number;
  puntiVariazione: number;
  tipo: TipoPunti;
  dataTransazione: Date;
  note: string;
}
```

---

## Troubleshooting

### Punti non incrementano dopo ordine
1. Verificare @Transactional su OrderService
2. Verificare PuntiService.aggiungiPunti() viene chiamato
3. Verificare database: PUNTI_TOTALI e PUNTI_DISPONIBILI esistono
4. Verificare calcoloPunti: totale ordine >= 20€

### Preview sconto non carica
1. Verificare carrello non è vuoto
2. Verificare utente autenticato (session valid)
3. Controllare browser console per errori HTTP
4. Verificare CartService.getCart() funziona

### Sconto non applicato al checkout
1. Verificare flag `usePunti=true` viene inviato
2. Controllare backend logs per errori
3. Verificare punti disponibili > 0
4. Verificare totale ordine > 0

### UI punti non si aggiorna
1. Verificare AuthService.currentUser$ è observable
2. Verificare CartComponent subscribe a changes
3. Verificare NavbarComponent subscribe a currentUser$
4. Force refresh di pagina per ricaricare utente

---

## Contatti e Support

Per domande o bug reports sulla feature di gamification, si veda la documentazione del backend per contacts.

---

*Ultimo aggiornamento: 11 maggio 2026*
*Versione: 1.0 - Release Stabile*
