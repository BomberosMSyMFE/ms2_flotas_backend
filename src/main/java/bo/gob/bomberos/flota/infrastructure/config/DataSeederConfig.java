package bo.gob.bomberos.flota.infrastructure.config;

import bo.gob.bomberos.flota.infrastructure.adapters.out.persistence.CamionEntity;
import bo.gob.bomberos.flota.infrastructure.adapters.out.persistence.HidranteEntity;
import bo.gob.bomberos.flota.infrastructure.adapters.out.persistence.SpringDataCamionRepository;
import bo.gob.bomberos.flota.infrastructure.adapters.out.persistence.SpringDataHidranteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.List;

/**
 * Seeder de datos iniciales en PostGIS para Santa Cruz de la Sierra.
 */
@Configuration
public class DataSeederConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSeederConfig.class);

    @Bean
    public CommandLineRunner initDatabase(SpringDataCamionRepository camionRepo,
                                          SpringDataHidranteRepository hidranteRepo) {
        return args -> {
            if (camionRepo.count() == 0) {
                log.info("Sembrando flota inicial de camiones de bomberos en PostGIS...");
                List<CamionEntity> camiones = List.of(
                        new CamionEntity("B-01", "2190-ABC", "ATAQUE_RAPIDO", "DISPONIBLE",
                                -17.7862, -63.1812, 3000, 3000, 95, "Estación Central", null, 0.0, 0.0, Instant.now()),
                        new CamionEntity("B-04", "3482-LKP", "CISTERNA_PESADA", "DISPONIBLE",
                                -17.7780, -63.1795, 10000, 10000, 88, "Estación Pampa de la Isla", null, 0.0, 0.0, Instant.now()),
                        new CamionEntity("B-06", "1122-XZY", "ESCALA_TELESCOPICA", "DISPONIBLE",
                                -17.7650, -63.1900, 4000, 4000, 90, "Estación Equipetrol", null, 0.0, 0.0, Instant.now()),
                        new CamionEntity("B-08", "4490-MNO", "CISTERNA_PESADA", "DISPONIBLE",
                                -17.7550, -63.1600, 12000, 12000, 92, "Estación Parque Industrial", null, 0.0, 0.0, Instant.now()),
                        new CamionEntity("B-11", "5501-QWE", "RESCATE_LIVIANO", "DISPONIBLE",
                                -17.8100, -63.1500, 1500, 1500, 85, "Estación Plan 3000", null, 0.0, 0.0, Instant.now())
                );
                camionRepo.saveAll(camiones);
                log.info("5 unidades de flota registradas exitosamente.");
            }

            if (hidranteRepo.count() == 0) {
                log.info("Sembrando red geoespacial de hidrantes en PostGIS...");
                List<HidranteEntity> hidrantes = List.of(
                        new HidranteEntity("HID-014", -17.7820, -63.1810, 65.5, 1200, true),
                        new HidranteEntity("HID-028", -17.7845, -63.1830, 70.0, 1500, true),
                        new HidranteEntity("HID-035", -17.7580, -63.1620, 80.0, 2000, true),
                        new HidranteEntity("HID-042", -17.7700, -63.1750, 60.0, 1100, true)
                );
                hidranteRepo.saveAll(hidrantes);
                log.info("Red de hidrantes registrada exitosamente.");
            }
        };
    }
}
