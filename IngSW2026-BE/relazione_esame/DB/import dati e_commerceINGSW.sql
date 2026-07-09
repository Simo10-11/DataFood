-- ============================================================
-- Dati di test per il database `e_commerceingsw`
-- Ecommerce di PRODOTTI ALIMENTARI
-- Da eseguire DOPO aver creato lo schema (dump fornito)
--


USE e_commerceINGSW;
-- ------------------------------------------------------------
-- CATEGORIA
-- ------------------------------------------------------------
INSERT INTO `categoria` (`id`, `nome`, `descrizione`) VALUES
(1, 'Frutta e Verdura', 'Prodotti ortofrutticoli freschi di stagione'),
(2, 'Carne e Pesce', 'Carni fresche e pesce selezionato'),
(3, 'Latticini e Formaggi', 'Latte, yogurt, formaggi e derivati'),
(4, 'Pane e Pasticceria', 'Pane fresco, dolci e prodotti da forno'),
(5, 'Pasta e Cereali', 'Pasta, riso e cereali'),
(6, 'Bevande', 'Acqua, succhi, vino e bibite'),
(7, 'Condimenti e Conserve', 'Olio, sughi, scatolame e dispensa');

-- ------------------------------------------------------------
-- PRODOTTO
-- ------------------------------------------------------------
INSERT INTO `prodotto` (`id`, `nome`, `descrizione`, `immagine_url`, `prezzo`, `quantita_disponibile`, `id_categoria`) VALUES
(1,  'Mele Golden (kg)', 'Mele Golden Delicious, raccolta italiana', NULL, 1.99, 200, 1),
(2,  'Banane (kg)', 'Banane fresche provenienza equosolidale', NULL, 1.49, 150, 1),
(3,  'Pomodori San Marzano (kg)', 'Pomodori San Marzano IGP', NULL, 2.49, 100, 1),
(4,  'Insalata Mista (busta 200g)', 'Insalata mista lavata e pronta al consumo', NULL, 1.79, 80, 1),
(5,  'Petto di Pollo (kg)', 'Petto di pollo fresco allevato a terra', NULL, 8.90, 50, 2),
(6,  'Salmone Fresco (kg)', 'Filetto di salmone norvegese fresco', NULL, 19.90, 30, 2),
(7,  'Macinato di Manzo (500g)', 'Carne macinata di manzo selezionata', NULL, 6.50, 60, 2),
(8,  'Prosciutto Crudo San Daniele (100g)', 'Prosciutto crudo DOP affettato sottile', NULL, 4.20, 70, 2),
(9,  'Latte Intero (1L)', 'Latte fresco intero pastorizzato', NULL, 1.39, 300, 3),
(10, 'Mozzarella di Bufala (250g)', 'Mozzarella di bufala campana DOP', NULL, 3.20, 90, 3),
(11, 'Parmigiano Reggiano DOP (kg)', 'Parmigiano Reggiano stagionato 24 mesi', NULL, 22.00, 40, 3),
(12, 'Yogurt Bianco (4x125g)', 'Yogurt bianco intero confezione da 4', NULL, 2.10, 150, 3),
(13, 'Pane Casereccio (kg)', 'Pane casereccio cotto a legna', NULL, 3.50, 60, 4),
(14, 'Cornetti Sfogliati (6 pz)', 'Cornetti sfogliati confezione da 6', NULL, 4.90, 40, 4),
(15, 'Spaghetti (500g)', 'Pasta di semola di grano duro trafilata al bronzo', NULL, 1.29, 250, 5),
(16, 'Riso Carnaroli (1kg)', 'Riso Carnaroli superfino per risotti', NULL, 2.99, 120, 5),
(17, 'Acqua Naturale (6x1.5L)', 'Acqua minerale naturale, confezione da 6', NULL, 2.49, 200, 6),
(18, 'Vino Chianti DOCG (750ml)', 'Vino rosso Chianti DOCG annata corrente', NULL, 9.90, 80, 6),
(19, 'Olio Extravergine d''Oliva (1L)', 'Olio EVO spremuto a freddo, prima qualità', NULL, 8.50, 100, 7),
(20, 'Passata di Pomodoro (700ml)', 'Passata di pomodoro 100% italiana', NULL, 1.99, 150, 7);

-- ------------------------------------------------------------
-- UTENTE
-- Password per tutti gli utenti di test: "password123"
-- Hash BCrypt (cost 10) generato con l'algoritmo bcrypt reale,
-- verificabile da Spring Security BCryptPasswordEncoder.
-- ------------------------------------------------------------
INSERT INTO `utente`
(`id`, `nome`, `cognome`, `email`, `password`, `via`, `numero_civico`, `citta`, `cap`, `numero_telefono`,
 `punti_disponibili`, `ruolo`) VALUES
(1, 'Admin', 'Sistema', 'admin@example.com', '$2b$10$8SMzmDsf6O/S32QAhKBvUeR3rqKWC73Lnxyv7T99VWq7OTsgJSaku', 'Via Roma', '1', 'Siena', '53100', '0577123456', 0, 'admin'),
(2, 'Marco', 'Rossi', 'marco.rossi@example.com', '$2b$10$8SMzmDsf6O/S32QAhKBvUeR3rqKWC73Lnxyv7T99VWq7OTsgJSaku', 'Via Garibaldi', '12', 'Firenze', '50100', '3331234567', 150, 'cliente'),
(3, 'Giulia', 'Bianchi', 'giulia.bianchi@example.com', '$2b$10$8SMzmDsf6O/S32QAhKBvUeR3rqKWC73Lnxyv7T99VWq7OTsgJSaku', 'Corso Italia', '45', 'Pisa', '56100', '3349876543', 80, 'cliente'),
(4, 'Luca', 'Verdi', 'luca.verdi@example.com', '$2b$10$8SMzmDsf6O/S32QAhKBvUeR3rqKWC73Lnxyv7T99VWq7OTsgJSaku', 'Via Dante', '7', 'Colle di Val d''Elsa', '53034', '3201112233', 0, 'cliente'),
(5, 'Sara', 'Russo', 'sara.russo@example.com', '$2b$10$8SMzmDsf6O/S32QAhKBvUeR3rqKWC73Lnxyv7T99VWq7OTsgJSaku', 'Via Mazzini', '23', 'Arezzo', '52100', '3389998877', 220, 'cliente'),
(6, 'Davide', 'Ferrari', 'davide.ferrari@example.com', '$2b$10$8SMzmDsf6O/S32QAhKBvUeR3rqKWC73Lnxyv7T99VWq7OTsgJSaku', 'Piazza del Campo', '3', 'Siena', '53100', '3471234567', 30, 'cliente');

-- ------------------------------------------------------------
-- ORDINE
-- status allineato ai dbValue dell'enum OrderStatus:
--   in_lavorazione | completato | annullato
-- (totale_pagato calcolato come somma delle righe in ordine_prodotto)
-- ------------------------------------------------------------
INSERT INTO `ordine` (`id`, `data`, `status`, `totale_pagato`, `id_utente`) VALUES
(1, '2026-04-10 10:15:00', 'completato', 13.91, 2),
(2, '2026-04-15 14:30:00', 'completato', 28.40, 3),
(3, '2026-05-02 09:00:00', 'in_lavorazione', 41.80, 2),
(4, '2026-05-20 18:45:00', 'in_lavorazione', 16.07, 5),
(5, '2026-06-01 11:20:00', 'annullato', 13.00, 4),
(6, '2026-06-12 16:05:00', 'completato', 31.80, 6),
(7, '2026-06-18 08:40:00', 'in_lavorazione', 10.94, 5);

-- ------------------------------------------------------------
-- ORDINE_PRODOTTO (righe ordine)
-- ------------------------------------------------------------
INSERT INTO `ordine_prodotto` (`ID_ORDINE`, `ID_PRODOTTO`, `prezzo_unitario`, `quantita`) VALUES
-- ordine 1 (Marco): spesa base
(1, 1, 1.99, 3),   -- Mele
(1, 9, 1.39, 2),   -- Latte
(1, 15, 1.29, 4),  -- Spaghetti
-- ordine 2 (Giulia): pesce + olio
(2, 6, 19.90, 1),  -- Salmone
(2, 19, 8.50, 1),  -- Olio EVO
-- ordine 3 (Marco): formaggio + vino
(3, 11, 22.00, 1), -- Parmigiano
(3, 18, 9.90, 2),  -- Vino
-- ordine 4 (Sara): colazione
(4, 13, 3.50, 2),  -- Pane
(4, 14, 4.90, 1),  -- Cornetti
(4, 9, 1.39, 3),   -- Latte
-- ordine 5 (Luca, annullato)
(5, 7, 6.50, 2),   -- Macinato di manzo
-- ordine 6 (Davide): dispensa
(6, 17, 2.49, 3),  -- Acqua
(6, 20, 1.99, 5),  -- Passata
(6, 16, 2.99, 2),  -- Riso
(6, 8, 4.20, 2),   -- Prosciutto crudo
-- ordine 7 (Sara): frutta e verdura
(7, 2, 1.49, 4),   -- Banane
(7, 3, 2.49, 2);   -- Pomodori

-- ------------------------------------------------------------
-- WISHLIST (una per utente, tranne admin)
-- ------------------------------------------------------------
INSERT INTO `wishlist` (`id`, `id_utente`) VALUES
(1, 2),
(2, 3),
(3, 4),
(4, 5),
(5, 6);

-- ------------------------------------------------------------
-- WISHLIST_PRODOTTO
-- ------------------------------------------------------------
INSERT INTO `wishlist_prodotto` (`id_wishlist`, `id_prodotto`) VALUES
(1, 6),   -- Marco vuole il Salmone
(1, 11),  -- Marco vuole il Parmigiano
(2, 18),  -- Giulia vuole il Vino
(3, 19),  -- Luca vuole l'Olio EVO
(4, 14),  -- Sara vuole i Cornetti
(4, 10),  -- Sara vuole la Mozzarella
(5, 8);   -- Davide vuole il Prosciutto crudo
