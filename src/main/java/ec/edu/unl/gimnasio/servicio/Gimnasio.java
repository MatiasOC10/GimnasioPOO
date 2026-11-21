package ec.edu.unl.gimnasio.servicio;

import ec.edu.unl.gimnasio.excepciones.AccesoDenegadoException;
import ec.edu.unl.gimnasio.modelo.ClaseGrupal;
import ec.edu.unl.gimnasio.modelo.Entrenador;
import ec.edu.unl.gimnasio.modelo.Membresia;
import ec.edu.unl.gimnasio.modelo.Plan;
import ec.edu.unl.gimnasio.modelo.Socio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clase coordinadora del sistema: registra socios, entrenadores y planes
 * (agregación) y crea las clases grupales (composición).
 */
public class Gimnasio {

    private final String nombre;
    private final List<Socio> socios;
    private final List<Entrenador> entrenadores;
    private final List<Plan> planes;
    private final List<ClaseGrupal> clases;

    public Gimnasio(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del gimnasio es obligatorio.");
        }
        this.nombre = nombre.trim();
        this.socios = new ArrayList<>();
        this.entrenadores = new ArrayList<>();
        this.planes = new ArrayList<>();
        this.clases = new ArrayList<>();
    }

    // ---------- Registro ----------

    public void registrarSocio(Socio socio) {
        if (socio == null) {
            throw new IllegalArgumentException("El socio no puede ser nulo.");
        }
        if (existeSocio(socio.getCedula())) {
            throw new IllegalArgumentException("Ya existe un socio con la cédula " + socio.getCedula() + ".");
        }
        socios.add(socio);
    }

    public void registrarEntrenador(Entrenador entrenador) {
        if (entrenador == null) {
            throw new IllegalArgumentException("El entrenador no puede ser nulo.");
        }
        if (existeEntrenador(entrenador.getCedula())) {
            throw new IllegalArgumentException("Ya existe un entrenador con la cédula " + entrenador.getCedula() + ".");
        }
        entrenadores.add(entrenador);
    }

    public void agregarPlan(Plan plan) {
        if (plan == null) {
            throw new IllegalArgumentException("El plan no puede ser nulo.");
        }
        for (Plan existente : planes) {
            if (existente.getTipo().equals(plan.getTipo())) {
                throw new IllegalArgumentException("El plan " + plan.getTipo() + " ya está registrado.");
            }
        }
        planes.add(plan);
    }

    /**
     * El gimnasio crea la clase grupal; el entrenador debe estar registrado.
     */
    public ClaseGrupal crearClase(String nombreClase, String horario, int cupo, String cedulaEntrenador) {
        Entrenador entrenador = buscarEntrenador(cedulaEntrenador);
        ClaseGrupal clase = new ClaseGrupal(nombreClase, horario, cupo, entrenador);
        clases.add(clase);
        return clase;
    }

    // ---------- Búsquedas ----------

    public Socio buscarSocio(String cedula) {
        for (Socio socio : socios) {
            if (socio.getCedula().equals(cedula)) {
                return socio;
            }
        }
        throw new IllegalArgumentException("No existe un socio con la cédula " + cedula + ".");
    }

    public Entrenador buscarEntrenador(String cedula) {
        for (Entrenador entrenador : entrenadores) {
            if (entrenador.getCedula().equals(cedula)) {
                return entrenador;
            }
        }
        throw new IllegalArgumentException("No existe un entrenador con la cédula " + cedula + ".");
    }

    private boolean existeSocio(String cedula) {
        for (Socio socio : socios) {
            if (socio.getCedula().equals(cedula)) {
                return true;
            }
        }
        return false;
    }

    private boolean existeEntrenador(String cedula) {
        for (Entrenador entrenador : entrenadores) {
            if (entrenador.getCedula().equals(cedula)) {
                return true;
            }
        }
        return false;
    }

    // ---------- Operaciones del negocio ----------

    /**
     * Permite el ingreso solo si el socio tiene una membresía vigente.
     *
     * @throws AccesoDenegadoException si el socio no puede entrar
     */
    public void registrarAcceso(String cedula) throws AccesoDenegadoException {
        Socio socio = buscarSocio(cedula);
        if (socio.getMembresias().isEmpty()) {
            throw new AccesoDenegadoException("El socio " + socio.getNombreCompleto() + " no tiene membresías.");
        }
        if (!socio.tieneMembresiaVigente()) {
            throw new AccesoDenegadoException("La membresía de " + socio.getNombreCompleto()
                    + " está " + socio.getMembresiaActual().getEstado() + ".");
        }
    }

    /**
     * Suma todos los pagos de todas las membresías de todos los socios.
     */
    public double calcularIngresos() {
        double total = 0;
        for (Socio socio : socios) {
            for (Membresia membresia : socio.getMembresias()) {
                total += membresia.getTotalPagado();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    /**
     * Socios cuya membresía vence en Membresia.DIAS_AVISO_VENCIMIENTO días o menos.
     */
    public List<Socio> listarMembresiasPorVencer() {
        List<Socio> resultado = new ArrayList<>();
        for (Socio socio : socios) {
            for (Membresia membresia : socio.getMembresias()) {
                if (membresia.estaPorVencer()) {
                    resultado.add(socio);
                    break;
                }
            }
        }
        return resultado;
    }

    // ---------- Getters (listas de solo lectura) ----------

    public String getNombre() {
        return nombre;
    }

    public List<Socio> getSocios() {
        return Collections.unmodifiableList(socios);
    }

    public List<Entrenador> getEntrenadores() {
        return Collections.unmodifiableList(entrenadores);
    }

    public List<Plan> getPlanes() {
        return Collections.unmodifiableList(planes);
    }

    public List<ClaseGrupal> getClases() {
        return Collections.unmodifiableList(clases);
    }
}
