package bo.gob.bomberos.flota.domain.ports.out;

/**
 * Puerto de Salida: Contrato para la publicación de eventos hacia el Broker (RabbitMQ) o WebSockets.
 */
public interface EventosFlotaOutboundPort {
    void publicarEvento(String tipoEvento, String claveAgregado, Object payload);
}
