package bo.gob.bomberos.flota.application.services;

import bo.gob.bomberos.flota.infrastructure.adapters.in.rest.dto.DespachoNotificacionRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FirebasePushNotificationService {

    private static final Logger log = LoggerFactory.getLogger(FirebasePushNotificationService.class);
    private static final String FCM_ENDPOINT = "https://fcm.googleapis.com/v1/projects/bomberos-scz-flotas/messages:send";
    private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private GoogleCredentials credentials;

    // Tokens registrados en memoria por unidad (ej. "B-04" -> "fcm_token_...")
    private final Map<String, String> tokensPorUnidad = new ConcurrentHashMap<>();
    private static final String TOKENS_FILE = "fcm_tokens_cache.json";
    private static final String DEFAULT_BOMBERO_TOKEN = "eHnP5Qf5WEopa6qMb-3tT0:APA91bGyfoIXF1AfwLJDWQSSGDUQABb-GMIBViqsAJOSxld-3LD0JPvJY-96XcFFcki3eFSE_R-LBmfpmCSMfj0BjOGvsnR9HduxlzPKYQrG5EkAhSktAiI";

    public FirebasePushNotificationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @PostConstruct
    public void inicializarFirebaseCredentials() {
        try {
            ClassPathResource resource = new ClassPathResource("firebase-adminsdk.json");
            if (resource.exists()) {
                try (InputStream is = resource.getInputStream()) {
                    this.credentials = GoogleCredentials.fromStream(is)
                            .createScoped(Collections.singletonList(MESSAGING_SCOPE));
                    log.info("✅ Firebase GoogleCredentials inicializado correctamente para bomberos-scz-flotas");
                }
            } else {
                log.warn("⚠️ Archivo firebase-adminsdk.json no encontrado en classpath.");
            }
        } catch (Exception e) {
            log.error("❌ Error inicializando GoogleCredentials para FCM: {}", e.getMessage(), e);
        }

        // Cargar tokens persistidos en disco o pre-cargar token activo del bombero
        cargarTokensDeDisco();
    }

    private synchronized void cargarTokensDeDisco() {
        try {
            java.io.File file = new java.io.File(TOKENS_FILE);
            if (file.exists()) {
                Map<String, String> loaded = objectMapper.readValue(file, Map.class);
                if (loaded != null) {
                    tokensPorUnidad.putAll(loaded);
                    log.info("📂 {} tokens FCM cargados desde {}", tokensPorUnidad.size(), TOKENS_FILE);
                }
            }
        } catch (Exception e) {
            log.warn("No se pudo cargar tokens desde disco: {}", e.getMessage());
        }

        // Garantizar que el dispositivo móvil activo del bombero siempre esté pre-registrado
        if (tokensPorUnidad.isEmpty()) {
            tokensPorUnidad.put("B-04", DEFAULT_BOMBERO_TOKEN);
            tokensPorUnidad.put("B-01", DEFAULT_BOMBERO_TOKEN);
            guardarTokensEnDisco();
            log.info("📲 Token móvil de bombero activo pre-configurado para B-04 y B-01");
        }
    }

    private synchronized void guardarTokensEnDisco() {
        try {
            java.io.File file = new java.io.File(TOKENS_FILE);
            objectMapper.writeValue(file, tokensPorUnidad);
        } catch (Exception e) {
            log.warn("No se pudo guardar tokens en disco: {}", e.getMessage());
        }
    }

    public void registrarToken(String unidadId, String token) {
        if (unidadId != null && token != null && token.length() > 50) {
            tokensPorUnidad.put(unidadId.toUpperCase(), token);
            guardarTokensEnDisco();
            log.info("📲 Token Push registrado y persistido para Unidad [{}]: {}...", unidadId, token.substring(0, Math.min(token.length(), 25)));
        }
    }

    public Map<String, String> listarTokensRegistrados() {
        return Collections.unmodifiableMap(tokensPorUnidad);
    }

    public Map<String, Object> enviarAlertaDespacho(DespachoNotificacionRequestDto request) {
        String unidadId = request.getUnidadIdOrDefault().toUpperCase();
        String targetToken = tokensPorUnidad.get(unidadId);

        // Recopilar todos los tokens válidos registrados (longitud > 50 para descartar tokens dummy)
        List<String> tokensAEnviar = new ArrayList<>();
        if (targetToken != null && targetToken.length() > 50) {
            tokensAEnviar.add(targetToken);
        }
        for (String t : tokensPorUnidad.values()) {
            if (t != null && t.length() > 50 && !tokensAEnviar.contains(t)) {
                tokensAEnviar.add(t);
            }
        }

        if (tokensAEnviar.isEmpty()) {
            log.warn("⚠️ No hay ningún token FCM válido registrado todavía.");
            return Map.of(
                    "status", "WARNING",
                    "mensaje", "No hay ningún celular con notificaciones activadas todavía. Abre la app en el celular y pulsa [Activar Notificaciones].",
                    "unidadId", unidadId
            );
        }

        try {
            if (credentials == null) {
                inicializarFirebaseCredentials();
            }
            if (credentials != null) {
                credentials.refreshIfExpired();
            }
            String accessToken = (credentials != null && credentials.getAccessToken() != null)
                    ? credentials.getAccessToken().getTokenValue()
                    : null;

            if (accessToken == null) {
                throw new IllegalStateException("No se pudo obtener el OAuth2 Access Token de Firebase");
            }

            String titulo = "🚨 ORDEN DE DESPACHO TÁCTICO: " + request.getTipoIncidenteOrDefault();
            String cuerpo = "Unidad " + unidadId + " despachada a " + request.getUbicacionOrDefault() + " [" + request.getPrioridadOrDefault() + "]";

            Map<String, Object> data = new HashMap<>();
            data.put("title", titulo);
            data.put("body", cuerpo);
            data.put("incidenteId", request.getIncidenteIdOrDefault());
            data.put("tipoIncidente", request.getTipoIncidenteOrDefault());
            data.put("ubicacion", request.getUbicacionOrDefault());
            data.put("latitud", String.valueOf(request.getLatitudOrDefault()));
            data.put("longitud", String.valueOf(request.getLongitudOrDefault()));
            data.put("prioridad", request.getPrioridadOrDefault());
            data.put("url", "https://34.28.212.45.sslip.io/?despacho=" + request.getIncidenteIdOrDefault());

            Map<String, Object> notification = Map.of(
                    "title", titulo,
                    "body", cuerpo
            );

            Map<String, Object> androidConfig = Map.of(
                    "priority", "HIGH",
                    "notification", Map.of(
                            "channel_id", "emergencias_bomberos",
                            "notification_priority", "PRIORITY_MAX",
                            "default_vibrate_timings", true,
                            "sound", "default"
                    )
            );

            Map<String, Object> webpushConfig = Map.of(
                    "headers", Map.of("Urgency", "high"),
                    "notification", Map.of(
                            "icon", "/icon-192.png",
                            "badge", "/icon-192.png",
                            "vibrate", List.of(500, 250, 500, 250, 1000),
                            "renotify", true,
                            "tag", "despacho-" + System.currentTimeMillis(),
                            "requireInteraction", true
                    )
            );

            int exitos = 0;
            List<String> respuestas = new ArrayList<>();

            for (String tok : tokensAEnviar) {
                Map<String, Object> message = new HashMap<>();
                message.put("token", tok);
                message.put("notification", notification);
                message.put("data", data);
                message.put("android", androidConfig);
                message.put("webpush", webpushConfig);

                Map<String, Object> rootPayload = Map.of("message", message);
                String jsonPayload = objectMapper.writeValueAsString(rootPayload);

                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(FCM_ENDPOINT))
                        .header("Authorization", "Bearer " + accessToken)
                        .header("Content-Type", "application/json; UTF-8")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .build();

                HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                log.info("📡 Respuesta FCM v1 para token {}...: Status={}", tok.substring(0, Math.min(tok.length(), 20)), httpResponse.statusCode());
                if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
                    exitos++;
                    respuestas.add(httpResponse.body());
                }
            }

            if (exitos > 0) {
                return Map.of(
                        "status", "SUCCESS",
                        "mensaje", "Notificación Push de Despacho Táctico enviada a " + exitos + " dispositivo(s).",
                        "unidadId", unidadId,
                        "incidenteId", request.getIncidenteIdOrDefault(),
                        "enviados", exitos,
                        "fcmResponses", respuestas
                );
            } else {
                return Map.of(
                        "status", "ERROR_FCM",
                        "mensaje", "No se pudo entregar la notificación a los tokens registrados."
                );
            }

        } catch (Exception e) {
            log.error("❌ Error enviando notificación push de despacho: {}", e.getMessage(), e);
            return Map.of(
                    "status", "ERROR",
                    "mensaje", "Excepción al enviar notificación: " + e.getMessage()
            );
        }
    }
}
