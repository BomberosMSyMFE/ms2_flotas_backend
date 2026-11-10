package bo.gob.bomberos.flota.application.services;

import bo.gob.bomberos.flota.domain.model.*;
import bo.gob.bomberos.flota.domain.ports.in.ConsultarFlotaInboundPort;
import bo.gob.bomberos.flota.domain.ports.in.LiberarCamionInboundPort;
import bo.gob.bomberos.flota.domain.ports.in.ReservarCamionInboundPort;
import bo.gob.bomberos.flota.domain.ports.out.EventosFlotaOutboundPort;
import bo.gob.bomberos.flota.domain.ports.out.FlotaPostGisOutboundPort;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Servicio de Aplicación: Orquesta la reserva atómica SAGA, compensación y consulta de flota.
 */
public class GestionFlotaService implements ReservarCamionInboundPort, LiberarCamionInboundPort, ConsultarFlotaInboundPort {

    private final FlotaPostGisOutboundPort flotaRepository;
    private final EventosFlotaOutboundPort eventPublisher;
    private final Map<String, Map<String, Object>> incidentesAsignados = new ConcurrentHashMap<>();

    public GestionFlotaService(FlotaPostGisOutboundPort flotaRepository, EventosFlotaOutboundPort eventPublisher) {
        this.flotaRepository = flotaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ReservaSaga reservarCamionOptimo(String incidenteId, String tipoFuego, double lat, double lon, int litrosMinimos) {
        return reservarCamionOptimo(incidenteId, tipoFuego, lat, lon, litrosMinimos, null, null, null);
    }

    @Override
    public ReservaSaga reservarCamionOptimo(String incidenteId, String tipoFuego, double lat, double lon, int litrosMinimos, String camionIdSolicitado, String direccion, String cuadrante) {
        CoordenadaGeografica focoIncidente = new CoordenadaGeografica(lat, lon);

        // Si el orquestador solicita una unidad específica (ej. "B-01"), priorizarla si está DISPONIBLE
        Optional<CamionUnidad> camionEspecifico = Optional.empty();
        if (camionIdSolicitado != null && !camionIdSolicitado.isBlank()) {
            camionEspecifico = flotaRepository.buscarPorId(camionIdSolicitado.trim().toUpperCase())
                    .filter(u -> u.getEstado() == EstadoUnidad.DISPONIBLE);
        }

        // Buscar unidad idónea (solicitada o por proximidad espacial PostGIS)
        CamionUnidad candidatoOptimo = camionEspecifico.orElseGet(() -> {
            List<CamionUnidad> unidadesCercanas = flotaRepository.encontrarCamionesCercanos(lat, lon, 25.0);
            return unidadesCercanas.stream()
                    .filter(u -> u.getEstado() == EstadoUnidad.DISPONIBLE)
                    .filter(u -> u.getLitrosAguaActual() >= litrosMinimos)
                    .min(Comparator.comparingDouble(u -> u.getPosicion().distanciaKmHacia(focoIncidente)))
                    .or(() -> unidadesCercanas.stream()
                            .filter(u -> u.getEstado() == EstadoUnidad.DISPONIBLE)
                            .min(Comparator.comparingDouble(u -> u.getPosicion().distanciaKmHacia(focoIncidente)))
                    )
                    .orElseThrow(() -> new IllegalStateException(
                            String.format("No hay camiones en estado DISPONIBLE en un radio de 25 km cerca de [%.4f, %.4f]. Libere alguna unidad antes de reservar.", lat, lon)
                    ));
        });

        // 3. Ejecutar lógica de dominio en el Aggregate Root
        candidatoOptimo.bloquearParaSaga(incidenteId);
        flotaRepository.guardar(candidatoOptimo);

        // Registrar datos completos del incidente asignado por Nataly (SAGA MS-01)
        Map<String, Object> incInfo = new HashMap<>();
        incInfo.put("incidenteId", incidenteId);
        incInfo.put("camionId", candidatoOptimo.getId());
        incInfo.put("titulo", "Emergencia SAGA: " + (direccion != null ? direccion : (tipoFuego != null ? tipoFuego : "Incendio")));
        incInfo.put("tipoFuego", tipoFuego != null && !tipoFuego.isBlank() ? tipoFuego : "ESTRUCTURAL");
        incInfo.put("direccion", direccion != null ? direccion : "Santa Cruz de la Sierra");
        incInfo.put("ubicacion", direccion != null ? direccion : "Santa Cruz de la Sierra");
        incInfo.put("cuadrante", cuadrante != null ? cuadrante : "Cuadrante Norte");
        incInfo.put("latitud", lat);
        incInfo.put("longitud", lon);
        incInfo.put("litrosMinimos", litrosMinimos);
        incInfo.put("prioridad", "CODIGO_ROJO");
        incInfo.put("estado", "ASIGNADO");
        incInfo.put("camionSugeridoId", candidatoOptimo.getId());
        incInfo.put("fechaAsignacion", Instant.now().toString());

        incidentesAsignados.put(candidatoOptimo.getId(), incInfo);
        incidentesAsignados.put(incidenteId, incInfo);

        // 4. Calcular métricas operativas
        double distanciaKm = candidatoOptimo.getPosicion().distanciaKmHacia(focoIncidente);
        double velocidadPromedioKmH = 40.0;
        double tiempoEstimadoMinutos = (distanciaKm / velocidadPromedioKmH) * 60.0;
        String reservaId = "RES-FLOTA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        ReservaSaga reserva = new ReservaSaga(
                reservaId,
                incidenteId,
                candidatoOptimo.getId(),
                candidatoOptimo.getPlaca(),
                candidatoOptimo.getTipo(),
                candidatoOptimo.getCapacidadMaximaAgua(),
                candidatoOptimo.getEstado(),
                Math.round(distanciaKm * 100.0) / 100.0,
                Math.round(tiempoEstimadoMinutos * 10.0) / 10.0,
                candidatoOptimo.getEstacionOrigen(),
                Instant.now()
        );

        // 5. Emitir evento hacia el bus
        eventPublisher.publicarEvento("CamionReservadoSagaEvent", candidatoOptimo.getId(), reserva);

        return reserva;
    }

    @Override
    public CamionUnidad liberarCamionCompensacion(String incidenteId, String camionId, String motivoCompensacion) {
        CamionUnidad camion = flotaRepository.buscarPorId(camionId)
                .orElseThrow(() -> new IllegalArgumentException("Camión no encontrado: " + camionId));

        // Ejecutar acción compensatoria de la SAGA
        camion.liberarCompensacion();
        CamionUnidad guardado = flotaRepository.guardar(camion);

        incidentesAsignados.remove(camionId);
        if (incidenteId != null) {
            incidentesAsignados.remove(incidenteId);
        }

        eventPublisher.publicarEvento("CamionLiberadoSagaEvent", camionId, "Compensado por: " + motivoCompensacion);
        return guardado;
    }

    @Override
    public List<CamionUnidad> obtenerUnidadesActivas() {
        return flotaRepository.listarTodas();
    }

    @Override
    public List<Map<String, Object>> obtenerIncidentesActivos() {
        return incidentesAsignados.values().stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> obtenerIncidenteDeCamion(String camionId) {
        if (camionId == null) return null;
        return incidentesAsignados.get(camionId.trim().toUpperCase());
    }
}
