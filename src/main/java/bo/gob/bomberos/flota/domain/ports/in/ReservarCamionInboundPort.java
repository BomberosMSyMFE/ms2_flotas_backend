package bo.gob.bomberos.flota.domain.ports.in;

import bo.gob.bomberos.flota.domain.model.ReservaSaga;

/**
 * Puerto de Entrada: Caso de uso de reserva de unidad para la transacción SAGA.
 */
public interface ReservarCamionInboundPort {
    ReservaSaga reservarCamionOptimo(String incidenteId, String tipoFuego, double lat, double lon, int litrosMinimos);
}
