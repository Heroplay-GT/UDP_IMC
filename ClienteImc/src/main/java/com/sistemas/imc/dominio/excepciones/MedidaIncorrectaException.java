package com.sistemas.imc.dominio.excepciones;

import java.io.Serial;

/** Excepción lanzada cuando el peso o la altura son inválidos. */
public final class MedidaIncorrectaException extends DominioException {
    @Serial
    private static final long serialVersionUID = 1L;

    public MedidaIncorrectaException(final String mensaje) {
        super(mensaje);
    }
}
