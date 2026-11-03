package bo.gob.bomberos.flota.infrastructure.adapters.out.persistence;

import bo.gob.bomberos.flota.domain.model.CamionUnidad;
import bo.gob.bomberos.flota.domain.model.CoordenadaGeografica;
import bo.gob.bomberos.flota.domain.model.EstadoUnidad;
import bo.gob.bomberos.flota.domain.model.TipoUnidad;
import bo.gob.bomberos.flota.domain.ports.out.FlotaPostGisOutboundPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador Secundario (Driven Adapter): Implementa la persistencia geoespacial de camiones en PostGIS.
 */
@Component
public class PostGisFlotaAdapter implements FlotaPostGisOutboundPort {

    private final SpringDataCamionRepository repository;

    public PostGisFlotaAdapter(SpringDataCamionRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CamionUnidad> encontrarCamionesCercanos(double lat, double lon, double radioKms) {
        return repository.findCamionesCercanos(lat, lon, radioKms).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CamionUnidad> buscarPorId(String camionId) {
        return repository.findById(camionId).map(this::toDomain);
    }

    @Override
    public CamionUnidad guardar(CamionUnidad camion) {
        CamionEntity entity = toEntity(camion);
        CamionEntity guardado = repository.save(entity);
        return toDomain(guardado);
    }

    @Override
    public List<CamionUnidad> listarTodas() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private CamionUnidad toDomain(CamionEntity entity) {
        CoordenadaGeografica pos = new CoordenadaGeografica(entity.getLatitud(), entity.getLongitud());
        return new CamionUnidad(
                entity.getId(),
                entity.getPlaca(),
                TipoUnidad.valueOf(entity.getTipo()),
                EstadoUnidad.valueOf(entity.getEstado()),
                pos,
                entity.getLitrosAguaActual(),
                entity.getCapacidadMaximaAgua(),
                entity.getCombustiblePorcentaje(),
                entity.getEstacionOrigen(),
                entity.getIncidenteAsignadoId(),
                entity.getVelocidadKmH(),
                entity.getRumboGrados(),
                entity.getUltimaActualizacion()
        );
    }

    private CamionEntity toEntity(CamionUnidad model) {
        return new CamionEntity(
                model.getId(),
                model.getPlaca(),
                model.getTipo().name(),
                model.getEstado().name(),
                model.getPosicion().getLatitud(),
                model.getPosicion().getLongitud(),
                model.getLitrosAguaActual(),
                model.getCapacidadMaximaAgua(),
                model.getCombustiblePorcentaje(),
                model.getEstacionOrigen(),
                model.getIncidenteAsignadoId(),
                model.getVelocidadKmH(),
                model.getRumboGrados(),
                model.getUltimaActualizacion()
        );
    }
}
