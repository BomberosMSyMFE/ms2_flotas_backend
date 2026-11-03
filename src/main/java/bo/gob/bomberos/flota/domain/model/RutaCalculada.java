package bo.gob.bomberos.flota.domain.model;

import java.util.List;

/**
 * Value Object con los resultados del cálculo de trayectoria espacial e hidrantes circundantes.
 */
public class RutaCalculada {
    private final int distanciaTotalMetros;
    private final int duracionEstimadaSegundos;
    private final double velocidadMediaKmH;
    private final List<CoordenadaGeografica> waypoints;
    private final List<Hidrante> hidrantesCercanos;

    public RutaCalculada(int distanciaTotalMetros, int duracionEstimadaSegundos, double velocidadMediaKmH,
                          List<CoordenadaGeografica> waypoints, List<Hidrante> hidrantesCercanos) {
        this.distanciaTotalMetros = distanciaTotalMetros;
        this.duracionEstimadaSegundos = duracionEstimadaSegundos;
        this.velocidadMediaKmH = velocidadMediaKmH;
        this.waypoints = waypoints;
        this.hidrantesCercanos = hidrantesCercanos;
    }

    public int getDistanciaTotalMetros() {
        return distanciaTotalMetros;
    }

    public int getDuracionEstimadaSegundos() {
        return duracionEstimadaSegundos;
    }

    public double getVelocidadMediaKmH() {
        return velocidadMediaKmH;
    }

    public List<CoordenadaGeografica> getWaypoints() {
        return waypoints;
    }

    public List<Hidrante> getHidrantesCercanos() {
        return hidrantesCercanos;
    }
}
