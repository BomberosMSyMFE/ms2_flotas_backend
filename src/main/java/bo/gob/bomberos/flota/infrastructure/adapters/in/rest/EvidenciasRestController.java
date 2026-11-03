package bo.gob.bomberos.flota.infrastructure.adapters.in.rest;

import bo.gob.bomberos.flota.infrastructure.adapters.in.rest.dto.EvidenciaRequestDto;
import bo.gob.bomberos.flota.infrastructure.adapters.in.rest.dto.EvidenciaResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adaptador Primario (Driving Adapter): CU-08 Evidencias de Campo Offline con idempotencia.
 */
@RestController
@RequestMapping("/api/v1/flotas")
@CrossOrigin(origins = "*")
public class EvidenciasRestController {

    // Caché en memoria para la máquina de estados de idempotencia
    private final Map<String, EvidenciaResponseDto> idempotencyStore = new ConcurrentHashMap<>();

    @PostMapping("/evidencias")
    public ResponseEntity<EvidenciaResponseDto> sincronizarEvidencia(
            @RequestHeader(value = "X-Idempotency-Key", required = true) String idempotencyKey,
            @Valid @RequestBody EvidenciaRequestDto request) {

        // 1. Verificar si la clave de idempotencia ya fue procesada exitosamente
        if (idempotencyStore.containsKey(idempotencyKey)) {
            // Respuesta idempotente (HTTP 200 OK sin duplicar registro)
            return ResponseEntity.ok(idempotencyStore.get(idempotencyKey));
        }

        // 2. Procesar el nuevo registro de evidencia
        String evidenciaId = "EVI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        EvidenciaResponseDto respuesta = new EvidenciaResponseDto(
                evidenciaId,
                idempotencyKey,
                "CONFIRMADO_EN_SERVIDOR",
                "Evidencia de campo registrada y vinculada a la orden de despacho.",
                Instant.now()
        );

        // 3. Almacenar en la máquina de estados de idempotencia
        idempotencyStore.put(idempotencyKey, respuesta);

        // HTTP 201 Created para la primera ejecución
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
}
