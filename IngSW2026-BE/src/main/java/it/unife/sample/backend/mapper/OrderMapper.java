package it.unife.sample.backend.mapper;

import it.unife.sample.backend.dto.OrderDTO;
import it.unife.sample.backend.dto.OrderItemDTO;
import it.unife.sample.backend.model.Ordine;
import it.unife.sample.backend.model.OrdineProdotto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "nomeCliente", expression = "java(buildCustomerName(entity))")
    @Mapping(target = "data", expression = "java(entity.getData() != null ? entity.getData().toString() : null)")
    @Mapping(target = "totale", expression = "java(calculateTotal(entity))")
    OrderDTO toDTO(Ordine entity);

    @Mapping(target = "productId", source = "prodotto.id")
    @Mapping(target = "nome", source = "prodotto.nome")
    @Mapping(target = "prezzo", source = "prezzoUnitario")
    OrderItemDTO toDTO(OrdineProdotto entity);

    default double map(BigDecimal value) {
        return value != null ? value.doubleValue() : 0d;
    }

    default double calculateTotal(Ordine entity) {
        if (entity == null || entity.getItems() == null) {
            return 0d;
        }

        return entity.getItems().stream()
                .map(item -> item.getPrezzoUnitario().multiply(BigDecimal.valueOf(item.getQuantita())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();
    }

    default String buildCustomerName(Ordine entity) {
        if (entity == null || entity.getUtente() == null) {
            return "";
        }

        String nome = entity.getUtente().getNome() != null ? entity.getUtente().getNome() : "";
        String cognome = entity.getUtente().getCognome() != null ? entity.getUtente().getCognome() : "";
        return (nome + " " + cognome).trim();
    }
}