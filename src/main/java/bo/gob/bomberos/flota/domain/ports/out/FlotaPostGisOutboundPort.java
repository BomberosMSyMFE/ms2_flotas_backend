package bo.gob.bomberos.flota.domain.ports.out;

import bo.gob.bomberos.flota.domain.model.CamionUnidad;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de Salida: Contrato para la persistencia geoespacial de camiones en PostgreSQL + PostGIS.
 */
public interface FlotaPostGisOutboundPort {
    List<CamionUnidad> encontrarCamionesCercanos(double lat, double lon, double radioKms);
    Optional<CamionUnidad> buscarPorId(String camionId);
    CamionUnidad guardar(CamionUnidad camion);
    List<CamionUnidad> listarTodas();
}
