package ec.edu.unl.gimnasio.excepciones;

/**
 * Excepción verificada (checked): obliga a quien registra un acceso
 * a capturarla o declararla con throws.
 */
public class AccesoDenegadoException extends Exception {

    private static final long serialVersionUID = 1L;

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
