package bo.gob.bomberos.flota.domain.ports.in;

import bo.gob.bomberos.flota.domain.model.RutaCalculada;

/**
 * Puerto de Entrada: Caso de uso CU-04 Navegar Ruta Óptima de Intervención.
 */
public interface CalcularRutaInboundPort {
    RutaCalculada calcularRutaOptima(double origenLat, double origenLon, double destinoLat, double destinoLon);
}
