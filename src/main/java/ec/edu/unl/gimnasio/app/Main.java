package ec.edu.unl.gimnasio.app;

import ec.edu.unl.gimnasio.modelo.ClaseGrupal;
import ec.edu.unl.gimnasio.modelo.Entrenador;
import ec.edu.unl.gimnasio.modelo.Plan;
import ec.edu.unl.gimnasio.servicio.Gimnasio;
import ec.edu.unl.gimnasio.vista.MenuConsola;

/**
 * Punto de entrada del programa (menú interactivo).
 * Para ver todos los casos de prueba ejecutar PruebasDominio.
 */
public class Main {

    public static void main(String[] args) {
        Gimnasio gimnasio = new Gimnasio("You vs You");
        cargarDatosIniciales(gimnasio);

        MenuConsola menu = new MenuConsola(gimnasio);
        menu.iniciar();
    }

    /**
     * Carga planes, entrenadores y clases para no empezar con el sistema vacío.
     */
    static void cargarDatosIniciales(Gimnasio gimnasio) {
        gimnasio.agregarPlan(new Plan(Plan.ESTUDIANTIL, 20));
        gimnasio.agregarPlan(new Plan(Plan.NORMAL, 30));

        gimnasio.registrarEntrenador(new Entrenador("1100000001", "Carlos", "Jaramillo", "0991111111", "Spinning"));
        gimnasio.registrarEntrenador(new Entrenador("1100000002", "Ana", "Torres", "0992222222", "Yoga"));

        gimnasio.crearClase("Spinning", "Lunes y miércoles 18:00", 20, "1100000001");
        gimnasio.crearClase("Yoga", "Martes y jueves 07:00", ClaseGrupal.CUPO_MAXIMO_PERMITIDO, "1100000002");
    }
}
