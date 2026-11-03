package bo.gob.bomberos.flota.application.services;

import bo.gob.bomberos.flota.domain.model.CamionUnidad;
import bo.gob.bomberos.flota.domain.model.CoordenadaGeografica;
import bo.gob.bomberos.flota.domain.model.EstadoUnidad;
import bo.gob.bomberos.flota.domain.ports.in.TelemetriaInboundPort;
import bo.gob.bomberos.flota.domain.ports.out.EventosFlotaOutboundPort;
import bo.gob.bomberos.flota.domain.ports.out.FlotaPostGisOutboundPort;

/**
 * Servicio de Aplicación: CU-05 Ingesta de Telemetría Vehicular.
 */
public class TelemetriaService implements TelemetriaInboundPort {

    private final FlotaPostGisOutboundPort flotaRepository;
    private final EventosFlotaOutboundPort eventPublisher;

    public TelemetriaService(FlotaPostGisOutboundPort flotaRepository, EventosFlotaOutboundPort eventPublisher) {
        this.flotaRepository = flotaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void procesarTelemetria(String camionId, double latitud, double longitud, double velocidadKmH,
                                   double rumboGrados, int litrosAguaActual, int combustiblePorcentaje,
                                   EstadoUnidad estadoOperativo) {
        CamionUnidad camion = flotaRepository.buscarPorId(camionId)
                .orElseThrow(() -> new IllegalArgumentException("Camión no encontrado: " + camionId));

        CoordenadaGeografica nuevaPos = new CoordenadaGeografica(latitud, longitud);
        camion.actualizarTelemetria(nuevaPos, velocidadKmH, rumboGrados, litrosAguaActual, combustiblePorcentaje, estadoOperativo);

        flotaRepository.guardar(camion);

        // Notificar en tiempo real hacia WebSockets de Comandos y Sala de Situación
        eventPublisher.publicarEvento("TelemetriaActualizadaEvent", camionId, camion);
    }
}
