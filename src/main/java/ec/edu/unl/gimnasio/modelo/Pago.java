package ec.edu.unl.gimnasio.modelo;

import java.time.LocalDate;

/**
 * Pago realizado para una membresía. Solo lo crea Membresia
 * (composición), por eso su constructor no es público.
 */
public class Pago {

    public static final String EFECTIVO = "Efectivo";
    public static final String TARJETA = "Tarjeta";
    public static final String TRANSFERENCIA = "Transferencia";

    private static int totalPagos = 0;

    private final int numero;
    private final double monto;
    private final LocalDate fecha;
    private final String metodo;

    Pago(double monto, String metodo) {
        validarMonto(monto);
        this.metodo = validarMetodo(metodo);
        this.monto = monto;
        this.fecha = LocalDate.now();
        totalPagos++;
        this.numero = totalPagos;
    }

    private static void validarMonto(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor que cero.");
        }
    }

    private static String validarMetodo(String metodo) {
        if (metodo == null || metodo.isBlank()) {
            throw new IllegalArgumentException("El método de pago es obligatorio.");
        }
        String limpio = metodo.trim();
        for (String valido : new String[]{EFECTIVO, TARJETA, TRANSFERENCIA}) {
            if (valido.equalsIgnoreCase(limpio)) {
                return valido;
            }
        }
        throw new IllegalArgumentException("Método de pago no válido: " + limpio
                + " (use " + EFECTIVO + ", " + TARJETA + " o " + TRANSFERENCIA + ").");
    }

    public void mostrarPago() {
        System.out.println(this);
    }

    public static int getTotalPagos() {
        return totalPagos;
    }

    public int getNumero() {
        return numero;
    }

    public double getMonto() {
        return monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getMetodo() {
        return metodo;
    }

    @Override
    public String toString() {
        return "Comprobante #" + numero + " | $" + String.format("%.2f", monto) + " | " + metodo + " | " + fecha;
    }
}
