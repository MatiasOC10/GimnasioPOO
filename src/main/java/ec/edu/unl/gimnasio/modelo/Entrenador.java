package ec.edu.unl.gimnasio.modelo;

/**
 * Entrenador del gimnasio. Hereda los datos personales de Persona
 * y agrega su especialidad.
 */
public class Entrenador extends Persona {

    private String especialidad;

    public Entrenador(String cedula, String nombre, String apellido, String telefono, String especialidad) {
        super(cedula, nombre, apellido, telefono);
        setEspecialidad(especialidad);
    }

    @Override
    public String getRol() {
        return "Entrenador";
    }

    public void mostrarDatos() {
        System.out.println(this);
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public final void setEspecialidad(String especialidad) {
        this.especialidad = validarTextoObligatorio(especialidad, "especialidad");
    }

    @Override
    public String toString() {
        return super.toString() + " | Especialidad: " + especialidad;
    }
}
