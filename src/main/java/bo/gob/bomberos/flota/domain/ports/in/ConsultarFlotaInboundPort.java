package bo.gob.bomberos.flota.domain.ports.in;

import bo.gob.bomberos.flota.domain.model.CamionUnidad;
import java.util.List;
import java.util.Map;

/**
 * Puerto de Entrada: Consulta de unidades activas e incidentes asignados para el visor cartográfico y cabina PWA.
 */
public interface ConsultarFlotaInboundPort {
    List<CamionUnidad> obtenerUnidadesActivas();
    List<Map<String, Object>> obtenerIncidentesActivos();
    Map<String, Object> obtenerIncidenteDeCamion(String camionId);
}
