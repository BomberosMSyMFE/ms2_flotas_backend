package bo.gob.bomberos.flota.infrastructure.adapters.in.rest;

import bo.gob.bomberos.flota.domain.model.CamionUnidad;
import bo.gob.bomberos.flota.domain.model.ReservaSaga;
import bo.gob.bomberos.flota.domain.model.RutaCalculada;
import bo.gob.bomberos.flota.domain.ports.in.CalcularRutaInboundPort;
import bo.gob.bomberos.flota.domain.ports.in.ConsultarFlotaInboundPort;
import bo.gob.bomberos.flota.domain.ports.in.LiberarCamionInboundPort;
import bo.gob.bomberos.flota.domain.ports.in.ReservarCamionInboundPort;
import bo.gob.bomberos.flota.infrastructure.adapters.in.rest.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import bo.gob.bomberos.flota.application.services.FirebasePushNotificationService;
import java.util.Map;

/**
 * Adaptador Primario (Driving Adapter): Controlador REST para la SAGA de Despacho,
 * Navegación y consulta de flota activa para el mapa Leaflet.
 */
@RestController
@RequestMapping("/api/v1/flotas")
@CrossOrigin(origins = "*")
public class FlotasRestController {

    private final ReservarCamionInboundPort reservarCamionPort;
    private final LiberarCamionInboundPort liberarCamionPort;
    private final CalcularRutaInboundPort calcularRutaPort;
    private final ConsultarFlotaInboundPort consultarFlotaPort;
    private final FirebasePushNotificationService pushNotificationService;

    public FlotasRestController(ReservarCamionInboundPort reservarCamionPort,
                                LiberarCamionInboundPort liberarCamionPort,
                                CalcularRutaInboundPort calcularRutaPort,
                                ConsultarFlotaInboundPort consultarFlotaPort,
                                FirebasePushNotificationService pushNotificationService) {
        this.reservarCamionPort = reservarCamionPort;
        this.liberarCamionPort = liberarCamionPort;
        this.calcularRutaPort = calcularRutaPort;
        this.consultarFlotaPort = consultarFlotaPort;
        this.pushNotificationService = pushNotificationService;
    }

    @PostMapping("/reservar-camion")
    public ResponseEntity<ReservaCamionResponseDto> reservarCamion(@Valid @RequestBody ReservaCamionRequestDto request) {
        ReservaSaga reserva = reservarCamionPort.reservarCamionOptimo(
                request.incidenteId(),
                request.getTipoFuegoOrDefault(),
                request.getLatitudOrDefault(),
                request.getLongitudOrDefault(),
                request.getLitrosMinimosOrDefault(),
                request.camionId(),
                request.direccion(),
                request.cuadrante()
        );
        return ResponseEntity.ok(ReservaCamionResponseDto.fromDomain(reserva));
    }

    @GetMapping("/incidentes/activos")
    public ResponseEntity<List<Map<String, Object>>> listarIncidentesActivos() {
        return ResponseEntity.ok(consultarFlotaPort.obtenerIncidentesActivos());
    }

    @GetMapping("/camiones/{camionId}/incidente")
    public ResponseEntity<Map<String, Object>> obtenerIncidenteCamion(@PathVariable String camionId) {
        Map<String, Object> inc = consultarFlotaPort.obtenerIncidenteDeCamion(camionId);
        if (inc == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(inc);
    }

    @PostMapping("/liberar-camion")
    public ResponseEntity<LiberarCamionResponseDto> liberarCamion(@Valid @RequestBody LiberarCamionRequestDto request) {
        CamionUnidad camion = liberarCamionPort.liberarCamionCompensacion(
                request.incidenteId(),
                request.camionId(),
                request.motivoCompensacion()
        );
        return ResponseEntity.ok(LiberarCamionResponseDto.fromDomain(camion, "La reserva fue revertida con éxito por compensación SAGA."));
    }

    @GetMapping("/ruta-optima")
    public ResponseEntity<RutaOptimaResponseDto> calcularRuta(
            @RequestParam double origenLat,
            @RequestParam double origenLon,
            @RequestParam double destinoLat,
            @RequestParam double destinoLon) {
        RutaCalculada ruta = calcularRutaPort.calcularRutaOptima(origenLat, origenLon, destinoLat, destinoLon);
        return ResponseEntity.ok(RutaOptimaResponseDto.fromDomain(ruta));
    }

    @GetMapping({"/unidades/activas", "/unidades"})
    public ResponseEntity<List<CamionUnidadResponseDto>> listarUnidadesActivas() {
        List<CamionUnidadResponseDto> dtos = consultarFlotaPort.obtenerUnidadesActivas().stream()
                .map(CamionUnidadResponseDto::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/push-token")
    public ResponseEntity<Map<String, Object>> registrarTokenPush(@Valid @RequestBody PushTokenRequestDto request) {
        pushNotificationService.registrarToken(request.unidadId(), request.token());
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "mensaje", "Token FCM registrado exitosamente para unidad " + request.unidadId()
        ));
    }

    @PostMapping("/despacho-notificar")
    public ResponseEntity<Map<String, Object>> notificarDespacho(@RequestBody DespachoNotificacionRequestDto request) {
        Map<String, Object> resultado = pushNotificationService.enviarAlertaDespacho(request);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/push-tokens")
    public ResponseEntity<Map<String, String>> listarTokens() {
        return ResponseEntity.ok(pushNotificationService.listarTokensRegistrados());
    }
}

