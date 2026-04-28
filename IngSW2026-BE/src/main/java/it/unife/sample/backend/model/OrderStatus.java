package it.unife.sample.backend.model;

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

    public String getDbValue() {
        return dbValue;
    }

    public String getLabel() {
        return label;
    }

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

    public static boolean isValid(String value) {
        try {
            fromDbValue(value);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}