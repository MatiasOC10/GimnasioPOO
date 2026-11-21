package ec.edu.unl.gimnasio.vista;

import ec.edu.unl.gimnasio.excepciones.AccesoDenegadoException;
import ec.edu.unl.gimnasio.modelo.ClaseGrupal;
import ec.edu.unl.gimnasio.modelo.Entrenador;
import ec.edu.unl.gimnasio.modelo.Membresia;
import ec.edu.unl.gimnasio.modelo.Pago;
import ec.edu.unl.gimnasio.modelo.Plan;
import ec.edu.unl.gimnasio.modelo.Socio;
import ec.edu.unl.gimnasio.servicio.Gimnasio;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Menú por consola. Solo pide datos y muestra resultados; las reglas del
 * negocio están en el modelo y en Gimnasio. Aquí se capturan las excepciones
 * para que el programa nunca termine abruptamente.
 */
public class MenuConsola {

    private static final int OPCION_SALIR = 0;

    private final Gimnasio gimnasio;
    private final Scanner scanner;

    public MenuConsola(Gimnasio gimnasio) {
        this.gimnasio = gimnasio;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        int opcion = -1;
        try {
            while (opcion != OPCION_SALIR) {
                mostrarOpciones();
                try {
                    opcion = leerEntero("Opción: ");
                    ejecutarOpcion(opcion);
                } catch (NumberFormatException e) {
                    System.out.println("Error: debe ingresar un número válido.");
                } catch (DateTimeParseException e) {
                    System.out.println("Error: la fecha debe tener el formato AAAA-MM-DD.");
                } catch (IllegalArgumentException | IllegalStateException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        } finally {
            scanner.close();
        }
    }

    private void mostrarOpciones() {
        System.out.println("\n===== Gimnasio " + gimnasio.getNombre() + " =====");
        System.out.println("1. Registrar socio");
        System.out.println("2. Registrar entrenador");
        System.out.println("3. Contratar membresía");
        System.out.println("4. Registrar pago");
        System.out.println("5. Registrar acceso al gimnasio");
        System.out.println("6. Cambiar estado de membresía");
        System.out.println("7. Crear clase grupal");
        System.out.println("8. Inscribir socio en clase grupal");
        System.out.println("9. Listar socios");
        System.out.println("10. Listar entrenadores");
        System.out.println("11. Listar clases grupales");
        System.out.println("12. Membresías por vencer (" + Membresia.DIAS_AVISO_VENCIMIENTO + " días o menos)");
        System.out.println("13. Ver ingresos totales");
        System.out.println("0. Salir");
    }

    private void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> registrarSocio();
            case 2 -> registrarEntrenador();
            case 3 -> contratarMembresia();
            case 4 -> registrarPago();
            case 5 -> registrarAcceso();
            case 6 -> cambiarEstadoMembresia();
            case 7 -> crearClase();
            case 8 -> inscribirEnClase();
            case 9 -> listarSocios();
            case 10 -> mostrarListaNumerada(gimnasio.getEntrenadores());
            case 11 -> mostrarListaNumerada(gimnasio.getClases());
            case 12 -> listarPorVencer();
            case 13 -> System.out.printf("Ingresos totales: $%.2f%n", gimnasio.calcularIngresos());
            case OPCION_SALIR -> System.out.println("Saliendo del sistema...");
            default -> System.out.println("Opción no válida.");
        }
    }

    // ---------- Opciones del menú ----------

    private void registrarSocio() {
        String cedula = leerTexto("Cédula: ");
        String nombre = leerTexto("Nombre: ");
        String apellido = leerTexto("Apellido: ");
        String telefono = leerTexto("Teléfono: ");
        String correo = leerTexto("Correo: ");

        gimnasio.registrarSocio(new Socio(cedula, nombre, apellido, telefono, correo));
        System.out.println("Socio registrado. Socios en el gimnasio: " + gimnasio.getSocios().size());
    }

    private void registrarEntrenador() {
        String cedula = leerTexto("Cédula: ");
        String nombre = leerTexto("Nombre: ");
        String apellido = leerTexto("Apellido: ");
        String telefono = leerTexto("Teléfono: ");
        String especialidad = leerTexto("Especialidad: ");

        gimnasio.registrarEntrenador(new Entrenador(cedula, nombre, apellido, telefono, especialidad));
        System.out.println("Entrenador registrado.");
    }

    private void contratarMembresia() {
        Socio socio = pedirSocio();
        Plan plan = elegirDeLista("Planes disponibles:", gimnasio.getPlanes());
        int meses = leerEntero("Meses (" + Membresia.MESES_MINIMOS + " a " + Membresia.MESES_MAXIMOS + "): ");
        String fecha = leerTexto("Fecha de inicio AAAA-MM-DD (Enter = hoy): ");
        LocalDate fechaInicio = fecha.isEmpty() ? LocalDate.now() : LocalDate.parse(fecha);

        Membresia membresia = socio.contratarMembresia(plan, meses, fechaInicio);
        System.out.printf("Membresía contratada. Precio final: $%.2f (vence el %s)%n",
                membresia.calcularPrecio(), membresia.getFechaFin());
    }

    private void registrarPago() {
        Membresia membresia = pedirSocio().getMembresiaActual();
        System.out.printf("Saldo pendiente: $%.2f%n", membresia.getSaldoPendiente());
        double monto = leerDecimal("Monto a pagar: ");
        String metodo = leerTexto("Método (" + Pago.EFECTIVO + "/" + Pago.TARJETA + "/" + Pago.TRANSFERENCIA + "): ");

        Pago pago = membresia.registrarPago(monto, metodo);
        System.out.println("Pago registrado: " + pago);
        System.out.printf("Nuevo saldo: $%.2f%n", membresia.getSaldoPendiente());
    }

    private void registrarAcceso() {
        String cedula = leerTexto("Cédula del socio: ");
        try {
            gimnasio.registrarAcceso(cedula);
            System.out.println("Acceso PERMITIDO. ¡Buen entrenamiento!");
        } catch (AccesoDenegadoException e) {
            System.out.println("Acceso DENEGADO: " + e.getMessage());
        }
    }

    private void cambiarEstadoMembresia() {
        Membresia membresia = pedirSocio().getMembresiaActual();
        System.out.println("Estado actual: " + membresia.getEstado());
        String nuevoEstado = leerTexto("Nuevo estado (" + Membresia.VIGENTE + "/"
                + Membresia.SUSPENDIDA + "/" + Membresia.CANCELADA + "): ");
        membresia.cambiarEstado(nuevoEstado);
        System.out.println("Estado actualizado a " + membresia.getEstado());
    }

    private void crearClase() {
        String nombre = leerTexto("Nombre de la clase: ");
        String horario = leerTexto("Horario: ");
        int cupo = leerEntero("Cupo máximo (1 a " + ClaseGrupal.CUPO_MAXIMO_PERMITIDO + "): ");
        String cedulaEntrenador = leerTexto("Cédula del entrenador: ");

        ClaseGrupal clase = gimnasio.crearClase(nombre, horario, cupo, cedulaEntrenador);
        System.out.println("Clase creada: " + clase);
    }

    private void inscribirEnClase() {
        Socio socio = pedirSocio();
        ClaseGrupal clase = elegirDeLista("Clases disponibles:", gimnasio.getClases());
        clase.inscribirSocio(socio);
        System.out.println("Socio inscrito en " + clase.getNombre() + ". Cupos libres: " + clase.getCuposDisponibles());
    }

    private void listarSocios() {
        if (gimnasio.getSocios().isEmpty()) {
            System.out.println("No hay socios registrados.");
            return;
        }
        for (Socio socio : gimnasio.getSocios()) {
            System.out.println(socio);
            for (Membresia membresia : socio.getMembresias()) {
                System.out.println("    -> " + membresia);
            }
        }
    }

    private void listarPorVencer() {
        List<Socio> porVencer = gimnasio.listarMembresiasPorVencer();
        if (porVencer.isEmpty()) {
            System.out.println("No hay membresías por vencer.");
            return;
        }
        for (Socio socio : porVencer) {
            Membresia m = socio.getMembresiaActual();
            System.out.println("AVISO: " + socio.getNombreCompleto() + " - vence el " + m.getFechaFin()
                    + " (" + m.getDiasRestantes() + " días)");
        }
    }

    // ---------- Métodos de apoyo ----------

    private Socio pedirSocio() {
        return gimnasio.buscarSocio(leerTexto("Cédula del socio: "));
    }

    private <T> T elegirDeLista(String titulo, List<T> lista) {
        if (lista.isEmpty()) {
            throw new IllegalStateException("No hay elementos registrados para elegir.");
        }
        System.out.println(titulo);
        mostrarListaNumerada(lista);
        int numero = leerEntero("Elija una opción: ");
        if (numero < 1 || numero > lista.size()) {
            throw new IllegalArgumentException("Opción fuera de rango.");
        }
        return lista.get(numero - 1);
    }

    private <T> void mostrarListaNumerada(List<T> lista) {
        if (lista.isEmpty()) {
            System.out.println("(lista vacía)");
        }
        for (int i = 0; i < lista.size(); i++) {
            System.out.println((i + 1) + ". " + lista.get(i));
        }
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private int leerEntero(String mensaje) {
        return Integer.parseInt(leerTexto(mensaje));
    }

    private double leerDecimal(String mensaje) {
        return Double.parseDouble(leerTexto(mensaje));
    }
}
