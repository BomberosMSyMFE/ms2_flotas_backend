package bo.gob.bomberos.flota.domain.ports.in;

import bo.gob.bomberos.flota.domain.model.EstadoUnidad;

/**
 * Puerto de Entrada: Caso de uso CU-05 Ingesta de Telemetría Vehicular.
 */
public interface TelemetriaInboundPort {
    void procesarTelemetria(String camionId, double latitud, double longitud, double velocidadKmH,
                           double rumboGrados, int litrosAguaActual, int combustiblePorcentaje,
                           EstadoUnidad estadoOperativo);
}
