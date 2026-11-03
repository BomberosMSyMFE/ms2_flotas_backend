package bo.gob.bomberos.flota.domain.ports.in;

import bo.gob.bomberos.flota.domain.model.CamionUnidad;

/**
 * Puerto de Entrada: Acción compensatoria de la SAGA si el despacho se aborta.
 */
public interface LiberarCamionInboundPort {
    CamionUnidad liberarCamionCompensacion(String incidenteId, String camionId, String motivoCompensacion);
}
