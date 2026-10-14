package com.sistemas.imc.dominio.vo;

import com.sistemas.imc.dominio.excepciones.MedidaIncorrectaException;

/** Value Object inmutable que encapsula y valida un peso en kilogramos (mayor que 0, máximo 500). */
public record Peso(double kilogramos) {

    private static final double MAXIMO = 500.0;

    public Peso {
        if (!Double.isFinite(kilogramos) || kilogramos <= 0 || kilogramos > MAXIMO) {
            throw new MedidaIncorrectaException(
                    "El peso debe ser mayor que 0 y como máximo " + MAXIMO + " kg.");
        }
    }
}
