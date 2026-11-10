package bo.gob.bomberos.flota.domain.ports.in;

import bo.gob.bomberos.flota.domain.model.ReservaSaga;

/**
 * Puerto de Entrada: Caso de uso de reserva de unidad para la transacción SAGA.
 */
public interface ReservarCamionInboundPort {
    default ReservaSaga reservarCamionOptimo(String incidenteId, String tipoFuego, double lat, double lon, int litrosMinimos) {
        return reservarCamionOptimo(incidenteId, tipoFuego, lat, lon, litrosMinimos, null);
    }

    default ReservaSaga reservarCamionOptimo(String incidenteId, String tipoFuego, double lat, double lon, int litrosMinimos, String camionIdSolicitado) {
        return reservarCamionOptimo(incidenteId, tipoFuego, lat, lon, litrosMinimos, camionIdSolicitado, null, null);
    }

    ReservaSaga reservarCamionOptimo(String incidenteId, String tipoFuego, double lat, double lon, int litrosMinimos, String camionIdSolicitado, String direccion, String cuadrante);
}
