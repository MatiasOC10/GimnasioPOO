package ec.edu.unl.gimnasio.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Socio del gimnasio. Hereda los datos personales de Persona y
 * crea y administra sus membresías (composición 1 -- 0..*).
 */
public class Socio extends Persona {

    private static int totalSocios = 0;

    private String correo;
    private final LocalDate fechaRegistro;
    private final List<Membresia> membresias;

    public Socio(String cedula, String nombre, String apellido, String telefono, String correo) {
        super(cedula, nombre, apellido, telefono);
        setCorreo(correo);
        this.fechaRegistro = LocalDate.now();
        this.membresias = new ArrayList<>();
        totalSocios++;
    }

    @Override
    public String getRol() {
        return "Socio";
    }

    /**
     * Contrata una membresía que empieza hoy.
     */
    public Membresia contratarMembresia(Plan plan, int meses) {
        return contratarMembresia(plan, meses, LocalDate.now());
    }

    /**
     * Crea la membresía (el socio controla su ciclo de vida). No se permite
     * contratar una nueva si el socio ya tiene una membresía vigente.
     */
    public Membresia contratarMembresia(Plan plan, int meses, LocalDate fechaInicio) {
        if (tieneMembresiaVigente()) {
            throw new IllegalStateException("El socio ya tiene una membresía vigente.");
        }
        Membresia membresia = new Membresia(plan, meses, fechaInicio);
        membresias.add(membresia);
        return membresia;
    }

    public boolean tieneMembresiaVigente() {
        for (Membresia membresia : membresias) {
            if (membresia.estaVigente()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Devuelve la última membresía contratada.
     */
    public Membresia getMembresiaActual() {
        if (membresias.isEmpty()) {
            throw new IllegalStateException("El socio " + getCedula() + " no tiene membresías.");
        }
        return membresias.get(membresias.size() - 1);
    }

    public static int getTotalSocios() {
        return totalSocios;
    }

    public String getCorreo() {
        return correo;
    }

    public final void setCorreo(String correo) {
        if (correo == null || !correo.trim().matches("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+")) {
            throw new IllegalArgumentException("El correo no es válido (ejemplo: nombre@correo.com).");
        }
        this.correo = correo.trim();
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Lista de solo lectura: las membresías solo se agregan con contratarMembresia().
     */
    public List<Membresia> getMembresias() {
        return Collections.unmodifiableList(membresias);
    }

    @Override
    public String toString() {
        return super.toString() + " | " + correo + " | registro: " + fechaRegistro;
    }
}
