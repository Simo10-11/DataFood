package it.unife.sample.backend.mapper;

import it.unife.sample.backend.dto.OrderDTO;
import it.unife.sample.backend.dto.OrderItemDTO;
import it.unife.sample.backend.model.Ordine;
import it.unife.sample.backend.model.OrdineProdotto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
// Converte gli ordini in DTO leggibili dall API
public interface OrderMapper {

    // Mappa l ordine completo nel DTO principale
    @Mapping(target = "nomeCliente", expression = "java(buildCustomerName(entity))")
    @Mapping(target = "data", expression = "java(entity.getData() != null ? entity.getData().toString() : null)")
    @Mapping(target = "totale", expression = "java(calculateTotal(entity))")
    @Mapping(target = "puntiGuadagnati", ignore = true)
    @Mapping(target = "puntiUtilizzati", ignore = true)
    @Mapping(target = "scontoApplicato", ignore = true)
    @Mapping(target = "utenteAggiornato", ignore = true)
    OrderDTO toDTO(Ordine entity);

    // Mappa il singolo articolo dell ordine nel relativo DTO
    @Mapping(target = "productId", source = "prodotto.id")
    @Mapping(target = "nome", source = "prodotto.nome")
    @Mapping(target = "prezzo", source = "prezzoUnitario")
    OrderItemDTO toDTO(OrdineProdotto entity);

    // Converte un valore monetario in double per l output API
    default double map(BigDecimal value) {
        return value != null ? value.doubleValue() : 0d;
    }

    // Calcola il totale ordine usando il totale salvato o gli articoli
    default double calculateTotal(Ordine entity) {
        if (entity == null) {
            return 0d;
        }

        if (entity.getTotalePagato() != null) {
            return entity.getTotalePagato().doubleValue();
        }

        if (entity.getItems() == null) {
            return 0d;
        }

        return entity.getItems().stream()
                .map(item -> item.getPrezzoUnitario().multiply(BigDecimal.valueOf(item.getQuantita())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();
    }

    // Compone il nome cliente a partire da nome e cognome
    default String buildCustomerName(Ordine entity) {
        if (entity == null || entity.getUtente() == null) {
            return "";
        }

        String nome = entity.getUtente().getNome() != null ? entity.getUtente().getNome() : "";
        String cognome = entity.getUtente().getCognome() != null ? entity.getUtente().getCognome() : "";
        return (nome + " " + cognome).trim();
    }
}