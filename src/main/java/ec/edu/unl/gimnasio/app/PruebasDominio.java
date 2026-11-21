package ec.edu.unl.gimnasio.app;

import ec.edu.unl.gimnasio.excepciones.AccesoDenegadoException;
import ec.edu.unl.gimnasio.modelo.ClaseGrupal;
import ec.edu.unl.gimnasio.modelo.Membresia;
import ec.edu.unl.gimnasio.modelo.Plan;
import ec.edu.unl.gimnasio.modelo.Socio;
import ec.edu.unl.gimnasio.servicio.Gimnasio;

import java.time.LocalDate;

/**
 * Ejecuta los casos de prueba válidos (CP) e inválidos (CI) del informe.
 * Cada caso inválido muestra la excepción lanzada y controlada.
 */
public class PruebasDominio {

    /** Acción de un caso de prueba que puede lanzar cualquier excepción. */
    private interface Accion {
        void ejecutar() throws Exception;
    }

    private static int casosOk = 0;
    private static int excepcionesControladas = 0;

    public static void main(String[] args) {
        Gimnasio gym = new Gimnasio("You vs You");
        Main.cargarDatosIniciales(gym);
        Plan normal = gym.getPlanes().get(1);
        Plan estudiantil = gym.getPlanes().get(0);

        titulo("PREGUNTAS DE CONTROL");
        Socio ana = new Socio("1105555551", "Ana", "Pérez", "0981111111", "ana@correo.com");
        Socio luis = new Socio("1105555552", "Luis", "Mora", "0982222222", "luis@correo.com");
        System.out.println("P5) Socio.totalSocios después de crear 2 socios = " + Socio.getTotalSocios());
        gym.registrarSocio(ana);
        gym.registrarSocio(luis);

        Membresia mAna = ana.contratarMembresia(normal, 6);
        mAna.registrarPago(50, "Efectivo");
        mAna.registrarPago(60, "Tarjeta");
        mAna.registrarPago(20, "Transferencia");
        System.out.println("P3) ArrayList<Pago>: tamaño después de agregar 3 pagos = " + mAna.getPagos().size());

        titulo("CASOS VÁLIDOS");
        caso("CP01", "Registrar socio con datos correctos", () -> {
            gym.registrarSocio(new Socio("1106666661", "María", "Castillo", "0983333333", "maria@unl.edu.ec"));
            System.out.println("   Socios registrados: " + gym.getSocios().size());
        });
        caso("CP02", "Contratar membresía Normal de 6 meses (10 % de descuento)", () -> {
            Membresia m = luis.contratarMembresia(normal, 6);
            System.out.printf("   Precio: 6 x $30 = $180.00 - 10 %% = $%.2f%n", m.calcularPrecio());
        });
        caso("CP03", "Registrar pago válido", () ->
                System.out.println("   " + luis.getMembresiaActual().registrarPago(80, "Efectivo")
                        + " | saldo: $" + String.format("%.2f", luis.getMembresiaActual().getSaldoPendiente())));
        caso("CP04", "Acceso de socio con membresía vigente", () -> {
            gym.registrarAcceso(luis.getCedula());
            System.out.println("   Acceso PERMITIDO para " + luis.getNombreCompleto());
        });
        caso("CP05", "Inscribir socio en clase grupal", () -> {
            ClaseGrupal spinning = gym.getClases().get(0);
            spinning.inscribirSocio(luis);
            System.out.println("   " + spinning);
        });
        caso("CP06", "Suspender y reactivar una membresía", () -> {
            luis.getMembresiaActual().cambiarEstado("suspendida");
            System.out.println("   Estado: " + luis.getMembresiaActual().getEstado());
            luis.getMembresiaActual().cambiarEstado("VIGENTE");
            System.out.println("   Estado: " + luis.getMembresiaActual().getEstado());
        });
        caso("CP07", "Membresía próxima a vencer (aviso de " + Membresia.DIAS_AVISO_VENCIMIENTO + " días)", () -> {
            Socio maria = gym.buscarSocio("1106666661");
            maria.contratarMembresia(estudiantil, 1, LocalDate.now().minusMonths(1).plusDays(2));
            for (Socio s : gym.listarMembresiasPorVencer()) {
                System.out.println("   AVISO: " + s.getNombreCompleto() + " vence en "
                        + s.getMembresiaActual().getDiasRestantes() + " días");
            }
        });
        caso("CP08", "Calcular ingresos totales", () ->
                System.out.printf("   Ingresos: $%.2f%n", gym.calcularIngresos()));

        titulo("CASOS INVÁLIDOS (excepciones lanzadas y controladas)");
        caso("CI01", "Cédula con letras", () ->
                new Socio("11A5555551", "Pedro", "Ruiz", "0984444444", "pedro@correo.com"));
        caso("CI02", "Nombre vacío", () ->
                new Socio("1107777771", "  ", "Ruiz", "0984444444", "pedro@correo.com"));
        caso("CI03", "Correo sin @", () ->
                new Socio("1107777771", "Pedro", "Ruiz", "0984444444", "pedrocorreo.com"));
        caso("CI04", "Plan con precio negativo", () -> new Plan(Plan.NORMAL, -15));
        caso("CI05", "Membresía de 15 meses", () ->
                new Socio("1107777772", "Rosa", "León", "0985555555", "rosa@correo.com").contratarMembresia(normal, 15));
        caso("CI06", "Pago con monto 0", () -> luis.getMembresiaActual().registrarPago(0, "Efectivo"));
        caso("CI07", "Pago mayor al saldo pendiente", () -> luis.getMembresiaActual().registrarPago(500, "Efectivo"));
        caso("CI08", "Método de pago no permitido", () -> luis.getMembresiaActual().registrarPago(10, "Bitcoin"));
        caso("CI09", "Registrar socio con cédula repetida", () ->
                gym.registrarSocio(new Socio("1105555551", "Otra", "Persona", "0986666666", "otra@correo.com")));
        caso("CI10", "Contratar otra membresía teniendo una vigente", () -> luis.contratarMembresia(estudiantil, 1));
        caso("CI11", "Acceso de socio sin membresías (excepción verificada)", () -> {
            Socio sinMembresia = new Socio("1108888881", "Jorge", "Paz", "0987777777", "jorge@correo.com");
            gym.registrarSocio(sinMembresia);
            gym.registrarAcceso(sinMembresia.getCedula());
        });
        caso("CI12", "Acceso con membresía vencida", () -> {
            Socio jorge = gym.buscarSocio("1108888881");
            jorge.contratarMembresia(estudiantil, 1, LocalDate.now().minusMonths(3));
            gym.registrarAcceso(jorge.getCedula());
        });
        caso("CI13", "Cambiar estado de una membresía cancelada", () -> {
            mAna.cambiarEstado(Membresia.CANCELADA);
            mAna.cambiarEstado(Membresia.VIGENTE);
        });
        caso("CI14", "Inscribir socio en una clase llena", () -> {
            ClaseGrupal pilates = gym.crearClase("Pilates", "Sábado 09:00", 1, "1100000002");
            pilates.inscribirSocio(luis);
            pilates.inscribirSocio(gym.buscarSocio("1106666661"));
        });
        caso("CI15", "Clase con cupo mayor a " + ClaseGrupal.CUPO_MAXIMO_PERMITIDO, () ->
                gym.crearClase("Crossfit", "Viernes 19:00", 40, "1100000001"));
        caso("CI16", "Buscar socio inexistente", () -> gym.buscarSocio("1199999999"));
        caso("CI17", "Leer número desde texto \"abc\" (simula la entrada del menú)", () -> Integer.parseInt("abc"));
        caso("CI18", "Fecha con formato incorrecto \"21/11/2026\"", () -> LocalDate.parse("21/11/2026"));

        titulo("RESUMEN");
        System.out.println("Casos correctos: " + casosOk + " | Excepciones controladas: " + excepcionesControladas);
        System.out.println("Socio.totalSocios (instancias creadas) = " + Socio.getTotalSocios());
        System.out.println("El programa terminó normalmente.");
    }

    private static void caso(String codigo, String descripcion, Accion accion) {
        System.out.println(codigo + " - " + descripcion);
        try {
            accion.ejecutar();
            casosOk++;
            System.out.println("   [OK]");
        } catch (IllegalArgumentException | IllegalStateException | AccesoDenegadoException e) {
            excepcionesControladas++;
            System.out.println("   [EXCEPCIÓN CONTROLADA] " + e.getClass().getSimpleName() + ": " + e.getMessage());
        } catch (Exception e) {
            excepcionesControladas++;
            System.out.println("   [EXCEPCIÓN CONTROLADA] " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private static void titulo(String texto) {
        System.out.println("\n========== " + texto + " ==========");
    }
}
