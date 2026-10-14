package com.sistemas.imc.dominio.vo;

import com.sistemas.imc.dominio.excepciones.MedidaIncorrectaException;

/** Value Object inmutable que encapsula y valida una altura en metros (mayor que 0, máximo 3). */
public record Altura(double metros) {

    private static final double MAXIMO = 3.0;

    public Altura {
        if (!Double.isFinite(metros) || metros <= 0 || metros > MAXIMO) {
            throw new MedidaIncorrectaException(
                    "La altura debe ser mayor que 0 y como máximo " + MAXIMO + " m.");
        }
    }
}
