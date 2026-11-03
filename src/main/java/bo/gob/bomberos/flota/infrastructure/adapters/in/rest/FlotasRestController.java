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

    public FlotasRestController(ReservarCamionInboundPort reservarCamionPort,
                                LiberarCamionInboundPort liberarCamionPort,
                                CalcularRutaInboundPort calcularRutaPort,
                                ConsultarFlotaInboundPort consultarFlotaPort) {
        this.reservarCamionPort = reservarCamionPort;
        this.liberarCamionPort = liberarCamionPort;
        this.calcularRutaPort = calcularRutaPort;
        this.consultarFlotaPort = consultarFlotaPort;
    }

    @PostMapping("/reservar-camion")
    public ResponseEntity<ReservaCamionResponseDto> reservarCamion(@Valid @RequestBody ReservaCamionRequestDto request) {
        ReservaSaga reserva = reservarCamionPort.reservarCamionOptimo(
                request.incidenteId(),
                request.tipoFuego(),
                request.getLatitudOrDefault(),
                request.getLongitudOrDefault(),
                request.getLitrosMinimosOrDefault()
        );
        return ResponseEntity.ok(ReservaCamionResponseDto.fromDomain(reserva));
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
}
