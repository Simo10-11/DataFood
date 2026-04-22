# DataFood 🍔

Applicazione backend per la gestione e visualizzazione di dati alimentari tramite API REST e database MySQL.

---

## 🚀 Features

* Connessione a database MySQL
* API REST con Express.js
* Recupero dati alimentari
* Servizio backend semplice e scalabile

---

## 🛠️ Tecnologie

* Node.js
* Express.js
* MySQL
* JavaScript

---

## 📁 Struttura del progetto

```
DataFood/
│── server.js
│── db.js
│── package.json
│── public/
```

---

## ⚙️ Installazione

1. Clona il repository:

```bash
git clone https://github.com/Simo10-11/DataFood.git
```

2. Vai nella cartella del progetto:

```bash
cd DataFood
```

3. Installa le dipendenze:

```bash
npm install
```

4. Configura il database MySQL nel file `db.js`:

```js
const connection = mysql.createConnection({
  host: "localhost",
  user: "root",
  password: "YOUR_PASSWORD",
  database: "YOUR_DATABASE"
});
```

5. Avvia il server:

```bash
node server.js
```

---

## 📡 API

### GET /dataFood

Restituisce i dati alimentari dal database.

### Esempio risposta:

```json
[
  {
    "name": "Pizza",
    "calories": 266
  }
]
```

---

## ▶️ Utilizzo

Apri il browser e vai su:

```
http://localhost:3000
```

per visualizzare i dati restituiti dal server.

---

## ⚠️ Known Issues

* Errore se le credenziali MySQL non sono corrette
* Il server non parte se il database non è attivo

---

## 🔮 Future Improvements

* Implementazione CRUD completo
* Autenticazione utenti
* Migrazione a frontend con React
* Validazione input lato server

---

## 👤 Autore

* Simone
* GitHub: https://github.com/Simo10-11
