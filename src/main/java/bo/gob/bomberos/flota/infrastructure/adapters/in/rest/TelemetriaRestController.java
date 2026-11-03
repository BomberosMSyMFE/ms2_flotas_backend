package bo.gob.bomberos.flota.infrastructure.adapters.in.rest;

import bo.gob.bomberos.flota.domain.ports.in.TelemetriaInboundPort;
import bo.gob.bomberos.flota.infrastructure.adapters.in.rest.dto.TelemetriaRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

/**
 * Adaptador Primario (Driving Adapter): Ingesta ultrarrápida de telemetría vehicular.
 */
@RestController
@RequestMapping("/api/v1/flotas")
@CrossOrigin(origins = "*")
public class TelemetriaRestController {

    private final TelemetriaInboundPort telemetriaPort;

    public TelemetriaRestController(TelemetriaInboundPort telemetriaPort) {
        this.telemetriaPort = telemetriaPort;
    }

    @PostMapping("/telemetria")
    public ResponseEntity<Map<String, Object>> registrarTelemetria(@Valid @RequestBody TelemetriaRequestDto request) {
        telemetriaPort.procesarTelemetria(
                request.camionId(),
                request.latitud(),
                request.longitud(),
                request.velocidadKmH() != null ? request.velocidadKmH() : 0.0,
                request.rumboGrados() != null ? request.rumboGrados() : 0.0,
                request.litrosAguaActual() != null ? request.litrosAguaActual() : 0,
                request.combustiblePorcentaje() != null ? request.combustiblePorcentaje() : 100,
                request.estadoOperativo()
        );

        return ResponseEntity.accepted().body(Map.of(
                "ack", true,
                "timestamp", Instant.now()
        ));
    }

    @PatchMapping("/camiones/{camionId}/telemetria")
    public ResponseEntity<Map<String, Object>> actualizarTelemetriaCamion(
            @PathVariable String camionId,
            @RequestBody Map<String, Object> body) {

        Double latitud = body.get("latitud") != null ? Double.valueOf(body.get("latitud").toString()) : -17.7833;
        Double longitud = body.get("longitud") != null ? Double.valueOf(body.get("longitud").toString()) : -63.1821;
        Double velocidad = body.get("velocidadKmh") != null ? Double.valueOf(body.get("velocidadKmh").toString()) : 0.0;
        Integer litrosAgua = body.get("nivelAguaLitros") != null ? Integer.valueOf(body.get("nivelAguaLitros").toString()) : 3000;
        Integer combustible = body.get("combustiblePorcentaje") != null ? Integer.valueOf(body.get("combustiblePorcentaje").toString()) : 85;

        bo.gob.bomberos.flota.domain.model.EstadoUnidad estado = bo.gob.bomberos.flota.domain.model.EstadoUnidad.DISPONIBLE;
        if (body.get("estadoOperativo") != null) {
            try {
                estado = bo.gob.bomberos.flota.domain.model.EstadoUnidad.valueOf(body.get("estadoOperativo").toString());
            } catch (Exception e) {
                estado = bo.gob.bomberos.flota.domain.model.EstadoUnidad.EN_SERVICIO;
            }
        }

        telemetriaPort.procesarTelemetria(
                camionId,
                latitud,
                longitud,
                velocidad,
                0.0,
                litrosAgua,
                combustible,
                estado
        );

        return ResponseEntity.ok(Map.of(
                "ack", true,
                "camionId", camionId,
                "nuevoEstado", estado,
                "timestamp", Instant.now()
        ));
    }
}
