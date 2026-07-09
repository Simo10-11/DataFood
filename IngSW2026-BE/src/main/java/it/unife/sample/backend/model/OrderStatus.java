package it.unife.sample.backend.model;

// Enum degli stati ordine usati sia nel database sia nell'interfaccia utente
public enum OrderStatus {

    IN_LAVORAZIONE("in_lavorazione", "In lavorazione"),
    COMPLETATO("completato", "Completato"),
    ANNULLATO("annullato", "Annullato");

    private final String dbValue;
    private final String label;

    OrderStatus(String dbValue, String label) {
        this.dbValue = dbValue;
        this.label = label;
    }

    // Valore salvato nel database
    public String getDbValue() {
        return dbValue;
    }

    // Etichetta leggibile mostrata all'utente
    public String getLabel() {
        return label;
    }

    // Converte il valore del database nello stato corrispondente
    public static OrderStatus fromDbValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Stato ordine non valido");
        }

        for (OrderStatus status : values()) {
            if (status.dbValue.equalsIgnoreCase(value)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Stato ordine non valido");
    }

    // Verifica se un valore rappresenta uno stato ordine valido
    public static boolean isValid(String value) {
        try {
            fromDbValue(value);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}