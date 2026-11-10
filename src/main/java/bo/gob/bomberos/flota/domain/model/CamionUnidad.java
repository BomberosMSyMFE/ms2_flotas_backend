package bo.gob.bomberos.flota.domain.model;

import java.time.Instant;

/**
 * Raíz de Agregado (Aggregate Root) del Bounded Context de Flotas.
 * Modela el estado operativo, telemetría y reglas de asignación del vehículo de emergencia.
 */
public class CamionUnidad {
    private final String id;
    private final String placa;
    private final TipoUnidad tipo;
    private EstadoUnidad estado;
    private CoordenadaGeografica posicion;
    private int litrosAguaActual;
    private final int capacidadMaximaAgua;
    private int combustiblePorcentaje;
    private double velocidadKmH;
    private double rumboGrados;
    private String incidenteAsignadoId;
    private String estacionOrigen;
    private Instant ultimaActualizacion;

    public CamionUnidad(String id, String placa, TipoUnidad tipo, EstadoUnidad estado,
                        CoordenadaGeografica posicion, int litrosAguaActual, int capacidadMaximaAgua,
                        int combustiblePorcentaje, String estacionOrigen) {
        this(id, placa, tipo, estado, posicion, litrosAguaActual, capacidadMaximaAgua,
             combustiblePorcentaje, estacionOrigen, null, 0.0, 0.0, Instant.now());
    }

    public CamionUnidad(String id, String placa, TipoUnidad tipo, EstadoUnidad estado,
                        CoordenadaGeografica posicion, int litrosAguaActual, int capacidadMaximaAgua,
                        int combustiblePorcentaje, String estacionOrigen, String incidenteAsignadoId,
                        double velocidadKmH, double rumboGrados, Instant ultimaActualizacion) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("El ID del camión no puede ser nulo");
        if (placa == null || placa.isBlank()) throw new IllegalArgumentException("La placa no puede ser nula");
        this.id = id;
        this.placa = placa;
        this.tipo = tipo;
        this.estado = estado != null ? estado : EstadoUnidad.DISPONIBLE;
        this.posicion = posicion;
        this.litrosAguaActual = litrosAguaActual;
        this.capacidadMaximaAgua = capacidadMaximaAgua;
        this.combustiblePorcentaje = combustiblePorcentaje;
        this.estacionOrigen = estacionOrigen != null ? estacionOrigen : "Estación Central";
        this.incidenteAsignadoId = incidenteAsignadoId;
        this.velocidadKmH = velocidadKmH;
        this.rumboGrados = rumboGrados;
        this.ultimaActualizacion = ultimaActualizacion != null ? ultimaActualizacion : Instant.now();
    }

    /**
     * Regla de Negocio: Bloquea la unidad para la transacción SAGA si está disponible.
     */
    public void bloquearParaSaga(String incidenteId) {
        if (this.estado != EstadoUnidad.DISPONIBLE) {
            throw new IllegalStateException("El camión " + id + " no está disponible. Estado actual: " + this.estado);
        }
        this.estado = EstadoUnidad.BLOQUEADO_SAGA;
        this.incidenteAsignadoId = incidenteId;
        this.ultimaActualizacion = Instant.now();
    }

    /**
     * Regla de Negocio: Acción compensatoria de la SAGA si otro microservicio falla.
     */
    public void liberarCompensacion() {
        this.estado = EstadoUnidad.DISPONIBLE;
        this.incidenteAsignadoId = null;
        this.ultimaActualizacion = Instant.now();
    }

    /**
     * Regla de Negocio: Culminación exitosa de la SAGA. El despacho queda confirmado y la unidad pasa a EN_TRANSITO.
     */
    public void confirmarDespacho() {
        if (this.estado == EstadoUnidad.BLOQUEADO_SAGA) {
            this.estado = EstadoUnidad.EN_TRANSITO;
            this.ultimaActualizacion = Instant.now();
        }
    }

    /**
     * Regla de Negocio: Conclusión de la atención del siniestro. La unidad queda disponible nuevamente.
     */
    public void concluirMision() {
        this.estado = EstadoUnidad.DISPONIBLE;
        this.incidenteAsignadoId = null;
        this.velocidadKmH = 0.0;
        this.ultimaActualizacion = Instant.now();
    }

    /**
     * Regla de Negocio: Ingesta periódica de telemetría desde sensores o cabina.
     */
    public void actualizarTelemetria(CoordenadaGeografica nuevaPosicion, double velocidad, double rumbo,
                                     int litros, int combustible, EstadoUnidad nuevoEstado) {
        this.posicion = nuevaPosicion;
        this.velocidadKmH = velocidad;
        this.rumboGrados = rumbo;
        this.litrosAguaActual = Math.min(litros, this.capacidadMaximaAgua);
        this.combustiblePorcentaje = Math.max(0, Math.min(100, combustible));

        // Si la unidad estaba en reserva transaccional (BLOQUEADO_SAGA) y la telemetría reporta avance o misión,
        // evoluciona de forma natural a la fase operativa (EN_TRANSITO o EN_ESCENA)
        if (this.estado == EstadoUnidad.BLOQUEADO_SAGA) {
            if (nuevoEstado == EstadoUnidad.EN_TRANSITO || nuevoEstado == EstadoUnidad.EN_ESCENA || velocidad > 0) {
                this.estado = (nuevoEstado != null && nuevoEstado != EstadoUnidad.DISPONIBLE) ? nuevoEstado : EstadoUnidad.EN_TRANSITO;
            }
        } else if (nuevoEstado != null) {
            this.estado = nuevoEstado;
        }
        this.ultimaActualizacion = Instant.now();
    }

    // Getters
    public String getId() { return id; }
    public String getPlaca() { return placa; }
    public TipoUnidad getTipo() { return tipo; }
    public EstadoUnidad getEstado() { return estado; }
    public CoordenadaGeografica getPosicion() { return posicion; }
    public int getLitrosAguaActual() { return litrosAguaActual; }
    public int getCapacidadMaximaAgua() { return capacidadMaximaAgua; }
    public int getCombustiblePorcentaje() { return combustiblePorcentaje; }
    public double getVelocidadKmH() { return velocidadKmH; }
    public double getRumboGrados() { return rumboGrados; }
    public String getIncidenteAsignadoId() { return incidenteAsignadoId; }
    public String getEstacionOrigen() { return estacionOrigen; }
    public Instant getUltimaActualizacion() { return ultimaActualizacion; }
}
