package bo.gob.bomberos.flota.infrastructure.adapters.out.events;

import bo.gob.bomberos.flota.domain.ports.out.EventosFlotaOutboundPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adaptador Secundario (Driven Adapter): Publicación de eventos de dominio hacia logging / cola RabbitMQ.
 */
@Component
public class LoggingEventPublisherAdapter implements EventosFlotaOutboundPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingEventPublisherAdapter.class);

    @Override
    public void publicarEvento(String tipoEvento, String claveAgregado, Object payload) {
        log.info("[EVENT_BUS_PUBLISH] Tipo: {} | Clave: {} | Detalle: {}", tipoEvento, claveAgregado, payload);
    }
}
