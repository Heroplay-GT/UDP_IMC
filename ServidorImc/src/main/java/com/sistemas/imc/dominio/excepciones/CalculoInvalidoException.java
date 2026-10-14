package com.sistemas.imc.dominio.excepciones;

import java.io.Serial;

/** Excepción lanzada cuando un cálculo del IMC recibe datos inválidos. */
public final class CalculoInvalidoException extends DominioException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CalculoInvalidoException(final String mensaje) {
        super(mensaje);
    }
}
