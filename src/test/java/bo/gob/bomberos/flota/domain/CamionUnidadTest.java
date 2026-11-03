package bo.gob.bomberos.flota.domain;

import bo.gob.bomberos.flota.domain.model.CamionUnidad;
import bo.gob.bomberos.flota.domain.model.CoordenadaGeografica;
import bo.gob.bomberos.flota.domain.model.EstadoUnidad;
import bo.gob.bomberos.flota.domain.model.TipoUnidad;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CamionUnidadTest {

    @Test
    @DisplayName("Debe bloquear la unidad para la SAGA cuando está disponible")
    void debeBloquearUnidadParaSaga() {
        CamionUnidad camion = new CamionUnidad(
                "B-04", "3482-LKP", TipoUnidad.CISTERNA_PESADA, EstadoUnidad.DISPONIBLE,
                new CoordenadaGeografica(-17.7780, -63.1795), 10000, 10000, 90, "Estación Norte"
        );

        camion.bloquearParaSaga("INC-2026-7890");

        assertEquals(EstadoUnidad.BLOQUEADO_SAGA, camion.getEstado());
        assertEquals("INC-2026-7890", camion.getIncidenteAsignadoId());
    }

    @Test
    @DisplayName("Debe lanzar excepción si se intenta bloquear una unidad no disponible")
    void debeFallarBloqueoSiNoEstaDisponible() {
        CamionUnidad camion = new CamionUnidad(
                "B-04", "3482-LKP", TipoUnidad.CISTERNA_PESADA, EstadoUnidad.EN_ESCENA,
                new CoordenadaGeografica(-17.7780, -63.1795), 10000, 10000, 90, "Estación Norte"
        );

        assertThrows(IllegalStateException.class, () -> camion.bloquearParaSaga("INC-9999"));
    }

    @Test
    @DisplayName("Debe liberar la unidad y volver a DISPONIBLE ante compensación SAGA")
    void debeLiberarUnidadPorCompensacion() {
        CamionUnidad camion = new CamionUnidad(
                "B-04", "3482-LKP", TipoUnidad.CISTERNA_PESADA, EstadoUnidad.DISPONIBLE,
                new CoordenadaGeografica(-17.7780, -63.1795), 10000, 10000, 90, "Estación Norte"
        );
        camion.bloquearParaSaga("INC-2026-7890");

        camion.liberarCompensacion();

        assertEquals(EstadoUnidad.DISPONIBLE, camion.getEstado());
        assertNull(camion.getIncidenteAsignadoId());
    }

    @Test
    @DisplayName("Debe calcular distancia geográfica correctamente con Haversine")
    void debeCalcularDistanciaHaversine() {
        CoordenadaGeografica c1 = new CoordenadaGeografica(-17.7833, -63.1821);
        CoordenadaGeografica c2 = new CoordenadaGeografica(-17.7712, -63.1754);

        double dist = c1.distanciaKmHacia(c2);

        assertTrue(dist > 1.0 && dist < 3.0, "La distancia debe estar entre 1 y 3 km");
    }
}
