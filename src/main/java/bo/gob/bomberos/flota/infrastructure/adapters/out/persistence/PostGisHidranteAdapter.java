package bo.gob.bomberos.flota.infrastructure.adapters.out.persistence;

import bo.gob.bomberos.flota.domain.model.CoordenadaGeografica;
import bo.gob.bomberos.flota.domain.model.Hidrante;
import bo.gob.bomberos.flota.domain.ports.out.HidrantePostGisOutboundPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adaptador Secundario (Driven Adapter): Implementa la consulta geoespacial de hidrantes en PostGIS.
 */
@Component
public class PostGisHidranteAdapter implements HidrantePostGisOutboundPort {

    private final SpringDataHidranteRepository repository;

    public PostGisHidranteAdapter(SpringDataHidranteRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Hidrante> encontrarHidrantesCercanos(double lat, double lon, double radioMetros) {
        return repository.findHidrantesCercanos(lat, lon, radioMetros).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void guardar(Hidrante hidrante) {
        HidranteEntity entity = new HidranteEntity(
                hidrante.getId(),
                hidrante.getPosicion().getLatitud(),
                hidrante.getPosicion().getLongitud(),
                hidrante.getPresionPsi(),
                hidrante.getCaudalLitrosMinuto(),
                hidrante.isOperativo()
        );
        repository.save(entity);
    }

    @Override
    public List<Hidrante> listarTodos() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private Hidrante toDomain(HidranteEntity entity) {
        return new Hidrante(
                entity.getId(),
                new CoordenadaGeografica(entity.getLatitud(), entity.getLongitud()),
                entity.getPresionPsi(),
                entity.getCaudalLitrosMinuto(),
                entity.isOperativo()
        );
    }
}
