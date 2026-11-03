package bo.gob.bomberos.flota.domain.ports.in;

import bo.gob.bomberos.flota.domain.model.CamionUnidad;
import java.util.List;

/**
 * Puerto de Entrada: Consulta de unidades activas para el visor cartográfico (React Leaflet).
 */
public interface ConsultarFlotaInboundPort {
    List<CamionUnidad> obtenerUnidadesActivas();
}
