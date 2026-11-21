package ec.edu.unl.gimnasio.modelo;

/**
 * Plan de membresía del gimnasio (Estudiantil o Normal).
 */
public class Plan {

    public static final String ESTUDIANTIL = "Estudiantil";
    public static final String NORMAL = "Normal";

    private final String tipo;
    private double precioMensual;

    public Plan(String tipo, double precioMensual) {
        this.tipo = validarTipo(tipo);
        setPrecioMensual(precioMensual);
    }

    private static String validarTipo(String tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de plan es obligatorio.");
        }
        String limpio = tipo.trim();
        if (limpio.equalsIgnoreCase(ESTUDIANTIL)) {
            return ESTUDIANTIL;
        }
        if (limpio.equalsIgnoreCase(NORMAL)) {
            return NORMAL;
        }
        throw new IllegalArgumentException("El tipo de plan debe ser " + ESTUDIANTIL + " o " + NORMAL + ".");
    }

    public String getTipo() {
        return tipo;
    }

    public double getPrecioMensual() {
        return precioMensual;
    }

    public final void setPrecioMensual(double precioMensual) {
        if (precioMensual <= 0) {
            throw new IllegalArgumentException("El precio mensual debe ser mayor que cero.");
        }
        this.precioMensual = precioMensual;
    }

    @Override
    public String toString() {
        return "Plan " + tipo + " - $" + String.format("%.2f", precioMensual) + " mensual";
    }
}
