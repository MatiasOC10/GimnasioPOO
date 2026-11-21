package ec.edu.unl.gimnasio.modelo;

/**
 * Superclase abstracta con los datos comunes de las personas del gimnasio
 * (socios y entrenadores). Evita repetir atributos y validaciones en las subclases.
 */
public abstract class Persona {

    public static final int LONGITUD_CEDULA = 10;
    public static final int LONGITUD_TELEFONO = 10;

    private final String cedula;
    private String nombre;
    private String apellido;
    private String telefono;

    protected Persona(String cedula, String nombre, String apellido, String telefono) {
        validarCedula(cedula);
        this.cedula = cedula.trim();
        setNombre(nombre);
        setApellido(apellido);
        setTelefono(telefono);
    }

    /**
     * Cada subclase indica qué rol cumple dentro del gimnasio (polimorfismo).
     */
    public abstract String getRol();

    // ---------- Validaciones ----------

    private static void validarCedula(String cedula) {
        if (cedula == null || !cedula.trim().matches("\\d{" + LONGITUD_CEDULA + "}")) {
            throw new IllegalArgumentException("La cédula debe tener " + LONGITUD_CEDULA + " dígitos numéricos.");
        }
    }

    protected static String validarTextoObligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio.");
        }
        return valor.trim();
    }

    // ---------- Getters y setters validados ----------

    public String getCedula() {
        return cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public final void setNombre(String nombre) {
        this.nombre = validarTextoObligatorio(nombre, "nombre");
    }

    public String getApellido() {
        return apellido;
    }

    public final void setApellido(String apellido) {
        this.apellido = validarTextoObligatorio(apellido, "apellido");
    }

    public String getTelefono() {
        return telefono;
    }

    public final void setTelefono(String telefono) {
        if (telefono == null || !telefono.trim().matches("\\d{" + LONGITUD_TELEFONO + "}")) {
            throw new IllegalArgumentException("El teléfono debe tener " + LONGITUD_TELEFONO + " dígitos numéricos.");
        }
        this.telefono = telefono.trim();
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    @Override
    public String toString() {
        return "[" + getRol() + "] " + cedula + " - " + getNombreCompleto() + " | Tel: " + telefono;
    }
}
