package ec.edu.unl.gimnasio.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clase grupal impartida por un entrenador (asociación *..1) a la que
 * se inscriben socios (agregación: los socios existen sin la clase).
 */
public class ClaseGrupal {

    public static final int CUPO_MAXIMO_PERMITIDO = 30;

    private String nombre;
    private String horario;
    private final int cupoMaximo;
    private final Entrenador entrenador;
    private final List<Socio> inscritos;

    public ClaseGrupal(String nombre, String horario, int cupoMaximo, Entrenador entrenador) {
        setNombre(nombre);
        setHorario(horario);
        validarCupo(cupoMaximo);
        if (entrenador == null) {
            throw new IllegalArgumentException("La clase debe tener un entrenador asignado.");
        }
        this.cupoMaximo = cupoMaximo;
        this.entrenador = entrenador;
        this.inscritos = new ArrayList<>();
    }

    private static void validarCupo(int cupo) {
        if (cupo < 1 || cupo > CUPO_MAXIMO_PERMITIDO) {
            throw new IllegalArgumentException("El cupo de la clase debe estar entre 1 y " + CUPO_MAXIMO_PERMITIDO + ".");
        }
    }

    public void inscribirSocio(Socio socio) {
        if (socio == null) {
            throw new IllegalArgumentException("Debe indicar el socio a inscribir.");
        }
        if (inscritos.contains(socio)) {
            throw new IllegalStateException("El socio ya está inscrito en " + nombre + ".");
        }
        if (!socio.tieneMembresiaVigente()) {
            throw new IllegalStateException("El socio no tiene una membresía vigente.");
        }
        if (!hayCupo()) {
            throw new IllegalStateException("La clase " + nombre + " ya está llena (cupo máximo: " + cupoMaximo + ").");
        }
        inscritos.add(socio);
    }

    public boolean hayCupo() {
        return inscritos.size() < cupoMaximo;
    }

    public int getCuposDisponibles() {
        return cupoMaximo - inscritos.size();
    }

    public String getNombre() {
        return nombre;
    }

    public final void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la clase es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    public String getHorario() {
        return horario;
    }

    public final void setHorario(String horario) {
        if (horario == null || horario.isBlank()) {
            throw new IllegalArgumentException("El horario de la clase es obligatorio.");
        }
        this.horario = horario.trim();
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public Entrenador getEntrenador() {
        return entrenador;
    }

    public List<Socio> getInscritos() {
        return Collections.unmodifiableList(inscritos);
    }

    @Override
    public String toString() {
        return nombre + " | " + horario + " | Entrenador: " + entrenador.getNombreCompleto()
                + " | Inscritos: " + inscritos.size() + "/" + cupoMaximo;
    }
}
