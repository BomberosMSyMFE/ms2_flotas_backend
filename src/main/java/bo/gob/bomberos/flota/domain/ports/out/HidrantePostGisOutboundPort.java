package bo.gob.bomberos.flota.domain.ports.out;

import bo.gob.bomberos.flota.domain.model.Hidrante;
import java.util.List;

/**
 * Puerto de Salida: Contrato para la consulta geoespacial de hidrantes en PostGIS.
 */
public interface HidrantePostGisOutboundPort {
    List<Hidrante> encontrarHidrantesCercanos(double lat, double lon, double radioMetros);
    void guardar(Hidrante hidrante);
    List<Hidrante> listarTodos();
}
