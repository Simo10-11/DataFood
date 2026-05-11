# Quick Start - Sistema Gamification

## Per Sviluppatori

### Avvio Rapido

#### 1. Database
```bash
# Eseguire gli statement SQL da script.sql
# Aggiunge PUNTI_TOTALI e PUNTI_DISPONIBILI a UTENTE
```

#### 2. Backend
```bash
cd IngSW2026-BE
./gradlew clean build
./gradlew bootRun
```

#### 3. Frontend
```bash
cd IngSW2026-FE
npm install
npm start
```

---

## Flussi Principali

### Flusso 1: Registrazione Utente
```
POST /api/auth/register
  ↓
Backend crea Utente con puntiTotali=0, puntiDisponibili=0
  ↓
Frontend riceve Utente e lo salva in localStorage
  ↓
✅ Pronto per acquisti
```

### Flusso 2: Acquisto Senza Punti
```
POST /api/orders/checkout (body vuoto oppure { usePunti: false })
  ↓
OrderService.checkoutWithPoints(false)
  ↓
- Crea ordine
- Calcola: puntiGuadagnati = floor(totale / 20)
- PuntiService.aggiungiPunti(utente, puntiGuadagnati)
- Salva
  ↓
Response OrderDTO:
{
  ...
  totale: 45.99,
  puntiGuadagnati: 2,
  puntiUtilizzati: 0,
  scontoApplicato: 0
}
  ↓
Frontend:
- Mostra "Ordine confermato! Hai guadagnato 2 punti"
- Aggiorna navbar
```

### Flusso 3: Anteprima Sconto
```
GET /api/orders/preview-discount (sessionID del user)
  ↓
OrderService.previewDiscount()
  ↓
- Legge cart dalla sessione
- Calcola totale
- PuntiService.applicaScontoWithPunti(punti, totale)
- Ritorna DiscountPreview
  ↓
Response:
{
  "totaleOrdine": 45.99,
  "puntiDisponibili": 50,
  "valorePuntiInEuro": 50.00,
  "scontoApplicabile": 45.99,
  "totaleConSconto": 0.00,
  "puntiUtilizzabili": 45
}
  ↓
Frontend:
- CartComponent riceve preview
- Mostra: "Sconto: €45.99, Nuovo totale: €0.00"
```

### Flusso 4: Acquisto Con Punti
```
POST /api/orders/checkout { usePunti: true }
  ↓
OrderService.checkoutWithPoints(true)
  ↓
- Crea ordine
- Calcola sconto: PuntiService.applicaScontoWithPunti(...)
- Totale FINALE = totale - sconto
- PuntiService.usaPunti(utente, puntiUtilizzati) ← decrementa disponibili
- Calcola punti guadagnati sul totale FINALE
- PuntiService.aggiungiPunti(utente, puntiGuadagnati) ← incrementa entrambi
- Salva
  ↓
Response OrderDTO:
{
  ...
  totale: 35.00,           ← totale DOPO sconto
  puntiGuadagnati: 1,      ← calcolato su €35 (0 punti se < €20)
  puntiUtilizzati: 10,     ← quanti punti sono stati usati
  scontoApplicato: 10.00   ← sconto in euro
}
  ↓
Frontend:
- Mostra "Ordine confermato! Hai guadagnato 1 punto"
- Mostra "Sconto applicato: €10.00"
```

---

## Debugging

### Verificare Punti in Database
```sql
SELECT id, nome, email, puntiTotali, puntiDisponibili FROM UTENTE;
```

### Test Endpoint Preview
```bash
curl -X GET http://localhost:8080/api/orders/preview-discount \
  -H "Cookie: JSESSIONID=<session_id>"
```

### Test Endpoint Checkout
```bash
# Senza punti
curl -X POST http://localhost:8080/api/orders/checkout \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=<session_id>" \
  -d '{"usePunti": false}'

# Con punti
curl -X POST http://localhost:8080/api/orders/checkout \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=<session_id>" \
  -d '{"usePunti": true}'
```

### Browser Console Errors
```javascript
// Se preview non carica:
console.log(this.discountPreview);  // Dovrebbe non essere null

// Se checkbox non funziona:
console.log(this.usePunti);  // Dovrebbe toggleare

// Se punti non si aggiornano:
console.log(this.currentUser);  // Dovrebbe avere puntiTotali/Disponibili
```

---

## Modifica Rapida

### Aggiungere Novo Endpoint
```java
// In OrderController
@PostMapping("/checkout-admin")
public ResponseEntity<OrderDTO> checkoutAdmin(
    @PathVariable Long userId,
    @RequestParam boolean usePunti
) {
    // Implementare logica admin
}
```

### Cambiare Rapporto Punti
```java
// In PuntiService.java
private static final int PUNTI_PER_EURO = 20;  // ← Modificare qua

// Diventa: 1 punto ogni €10
// private static final int PUNTI_PER_EURO = 10;
```

### Aggiungere Messaggio Personalizzato
```typescript
// In cart.component.ts
let message = `Ordine #${order.id} confermato!`;
if (order.puntiGuadagnati && order.puntiGuadagnati > 0) {
  message += ` 🎉 Hai guadagnato ${order.puntiGuadagnati} punti straordinari!`;
}
```

---

## Performance Tips

1. **Caching**: Punti disponibili sono già cached in AuthService.currentUser$
2. **Lazy Load**: Preview sconto caricato solo se checkbox selezionato
3. **Database**: Aggiungere indice su UTENTE.PUNTI_DISPONIBILI se performance problema
4. **Transactions**: @Transactional garantisce consistency senza lock eccessivi

---

## Common Issues

| Problema | Causa | Soluzione |
|----------|-------|----------|
| Punti non incrementano | @Transactional missing | Verificare OrderService.checkout() ha @Transactional |
| Preview mostra €0 | Carrello vuoto | Verificare cartItems not empty |
| Checkbox non visibile | puntiDisponibili = 0 | Utente deve aver completato almeno un ordine |
| Sconto non applicato | usePunti=false | Verificare checkbox selezionato |
| Ordine non creato | Session expired | Re-login utente |

---

## Testing con Postman

1. **Import Collection** (POST requests):
   - POST /api/auth/register
   - POST /api/orders/checkout

2. **Pre-request Script**:
```javascript
// Salva session ID dai cookie
pm.environment.set("sessionId", pm.response.headers.get("Set-Cookie"));
```

3. **Body Checkout**:
```json
{
  "usePunti": true
}
```

---

## Estensioni Future - Come Fare

### Aggiungere "Punti Referral"
```java
// In PuntiService
public void aggiungiPuntiReferral(Utente referrer, Utente referee) {
    int PUNTI_REFERRAL = 50;
    aggiungiPunti(referrer, PUNTI_REFERRAL);
}
```

### Aggiungere "Expiration Punti"
```java
// Aggiungere a Utente entity
@Column(name = "DATA_PUNTI_SCADENZA")
private LocalDate dataPuntiScadenza;

// In PuntiService
public int getPuntiValidi(Utente utente) {
    if (utente.getDataPuntiScadenza().isAfter(LocalDate.now())) {
        return utente.getPuntiDisponibili();
    }
    return 0;
}
```

### Aggiungere "VIP Multiplier"
```java
// In PuntiService
public int calculatePuntiGuadagnati(BigDecimal importoOrdine, Utente utente) {
    int base = importoOrdine.divide(BigDecimal.valueOf(PUNTI_PER_EURO)).intValue();
    double multiplier = utente.getPuntiTotali() > 1000 ? 1.5 : 1.0;  // VIP
    return (int) (base * multiplier);
}
```

---

## Contatti

- **Backend Issues**: Controllare logs di OrderService e PuntiService
- **Frontend Issues**: Controllare browser console per HTTP errors
- **Database Issues**: Verificare schema con `DESC UTENTE;`

---

*Quick Start Guide v1.0*
*11 maggio 2026*
