package ec.edu.unl.gimnasio.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Membresía contratada por un socio. Usa un Plan (asociación) y
 * crea y guarda sus pagos (composición 1 -- 0..*).
 */
public class Membresia {

    public static final int MESES_MINIMOS = 1;
    public static final int MESES_MAXIMOS = 12;
    public static final int DIAS_AVISO_VENCIMIENTO = 3;

    public static final String VIGENTE = "VIGENTE";
    public static final String SUSPENDIDA = "SUSPENDIDA";
    public static final String CANCELADA = "CANCELADA";
    public static final String VENCIDA = "VENCIDA";

    private static final double DESCUENTO_3_MESES = 0.05;
    private static final double DESCUENTO_6_MESES = 0.10;
    private static final double DESCUENTO_12_MESES = 0.15;

    private final Plan plan;
    private final int meses;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private String estado;
    private final List<Pago> pagos;

    /**
     * Solo Socio crea membresías (por eso el constructor no es público).
     */
    Membresia(Plan plan, int meses, LocalDate fechaInicio) {
        if (plan == null) {
            throw new IllegalArgumentException("La membresía debe tener un plan.");
        }
        validarMeses(meses);
        validarFechaInicio(fechaInicio);
        this.plan = plan;
        this.meses = meses;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaInicio.plusMonths(meses);
        this.estado = VIGENTE;
        this.pagos = new ArrayList<>();
    }

    private static void validarMeses(int meses) {
        if (meses < MESES_MINIMOS || meses > MESES_MAXIMOS) {
            throw new IllegalArgumentException("La membresía debe ser de " + MESES_MINIMOS
                    + " a " + MESES_MAXIMOS + " meses.");
        }
    }

    private static void validarFechaInicio(LocalDate fechaInicio) {
        if (fechaInicio == null) {
            throw new IllegalArgumentException("La fecha de inicio es obligatoria.");
        }
        if (fechaInicio.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser futura.");
        }
    }

    // ---------- Precio y pagos ----------

    /**
     * Precio final con descuento según la duración (5 %, 10 % o 15 %).
     */
    public double calcularPrecio() {
        double precioSinDescuento = plan.getPrecioMensual() * meses;
        return redondear(precioSinDescuento * (1 - obtenerDescuento()));
    }

    private double obtenerDescuento() {
        if (meses == MESES_MAXIMOS) {
            return DESCUENTO_12_MESES;
        }
        if (meses >= 6) {
            return DESCUENTO_6_MESES;
        }
        if (meses >= 3) {
            return DESCUENTO_3_MESES;
        }
        return 0;
    }

    /**
     * Crea el pago y lo agrega a la membresía si es válido.
     */
    public Pago registrarPago(double monto, String metodo) {
        if (estado.equals(CANCELADA)) {
            throw new IllegalStateException("No se puede pagar una membresía cancelada.");
        }
        if (monto > getSaldoPendiente()) {
            throw new IllegalArgumentException("El monto ($" + String.format("%.2f", monto)
                    + ") supera el saldo pendiente ($" + String.format("%.2f", getSaldoPendiente()) + ").");
        }
        Pago pago = new Pago(monto, metodo);
        pagos.add(pago);
        return pago;
    }

    public double getTotalPagado() {
        double total = 0;
        for (Pago pago : pagos) {
            total += pago.getMonto();
        }
        return redondear(total);
    }

    public double getSaldoPendiente() {
        return redondear(calcularPrecio() - getTotalPagado());
    }

    private static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    // ---------- Estado ----------

    public boolean estaVigente() {
        actualizarSiVencio();
        return estado.equals(VIGENTE);
    }

    /**
     * True si la membresía está vigente y vence en DIAS_AVISO_VENCIMIENTO días o menos.
     */
    public boolean estaPorVencer() {
        return estaVigente() && getDiasRestantes() <= DIAS_AVISO_VENCIMIENTO;
    }

    public long getDiasRestantes() {
        return Math.max(0, ChronoUnit.DAYS.between(LocalDate.now(), fechaFin));
    }

    private void actualizarSiVencio() {
        if (!estado.equals(CANCELADA) && LocalDate.now().isAfter(fechaFin)) {
            estado = VENCIDA;
        }
    }

    /**
     * Transiciones permitidas:
     * VIGENTE -> SUSPENDIDA o CANCELADA; SUSPENDIDA -> VIGENTE o CANCELADA.
     * Una membresía CANCELADA o VENCIDA ya no cambia (se contrata una nueva).
     */
    public void cambiarEstado(String nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado es obligatorio.");
        }
        String destino = nuevoEstado.trim().toUpperCase();
        if (!destino.equals(VIGENTE) && !destino.equals(SUSPENDIDA) && !destino.equals(CANCELADA)) {
            throw new IllegalArgumentException("Estado no válido: " + destino);
        }
        actualizarSiVencio();
        if (estado.equals(CANCELADA) || estado.equals(VENCIDA)) {
            throw new IllegalStateException("Una membresía " + estado + " ya no puede cambiar de estado.");
        }
        if (estado.equals(destino)) {
            throw new IllegalStateException("La membresía ya está en estado " + destino + ".");
        }
        estado = destino;
    }

    // ---------- Getters ----------

    public Plan getPlan() {
        return plan;
    }

    public int getMeses() {
        return meses;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public String getEstado() {
        actualizarSiVencio();
        return estado;
    }

    public List<Pago> getPagos() {
        return Collections.unmodifiableList(pagos);
    }

    @Override
    public String toString() {
        return plan.getTipo() + " | " + meses + " mes(es) | " + fechaInicio + " a " + fechaFin
                + " | estado: " + getEstado() + " | precio: $" + String.format("%.2f", calcularPrecio())
                + " | saldo: $" + String.format("%.2f", getSaldoPendiente());
    }
}
