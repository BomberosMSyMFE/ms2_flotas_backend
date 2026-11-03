package bo.gob.bomberos.flota.infrastructure.config;

import bo.gob.bomberos.flota.application.services.GestionFlotaService;
import bo.gob.bomberos.flota.application.services.RutaOptimaService;
import bo.gob.bomberos.flota.application.services.TelemetriaService;
import bo.gob.bomberos.flota.domain.ports.out.EventosFlotaOutboundPort;
import bo.gob.bomberos.flota.domain.ports.out.FlotaPostGisOutboundPort;
import bo.gob.bomberos.flota.domain.ports.out.HidrantePostGisOutboundPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Inyección de Dependencias:
 * Conecta los Puertos con los Adaptadores respetando la regla estricta de Arquitectura Hexagonal
 * (el dominio y la aplicación no tienen dependencias de Spring).
 */
@Configuration
public class ApplicationBeansConfig {

    @Bean
    public GestionFlotaService gestionFlotaService(FlotaPostGisOutboundPort flotaRepository,
                                                   EventosFlotaOutboundPort eventPublisher) {
        return new GestionFlotaService(flotaRepository, eventPublisher);
    }

    @Bean
    public RutaOptimaService rutaOptimaService(HidrantePostGisOutboundPort hidranteRepository) {
        return new RutaOptimaService(hidranteRepository);
    }

    @Bean
    public TelemetriaService telemetriaService(FlotaPostGisOutboundPort flotaRepository,
                                              EventosFlotaOutboundPort eventPublisher) {
        return new TelemetriaService(flotaRepository, eventPublisher);
    }
}
