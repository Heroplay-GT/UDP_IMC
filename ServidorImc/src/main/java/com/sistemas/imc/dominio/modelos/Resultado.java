package com.sistemas.imc.dominio.modelos;

import com.sistemas.imc.dominio.enums.CategoriaImc;
import com.sistemas.imc.dominio.excepciones.ResultadoInvalidoException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

/**
 * Representa el resultado inmutable del cálculo del IMC.
 * Responsabilidad única: Encapsular los datos del diagnóstico y su formateo.
 */
public final class Resultado {

    private final double imc;
    private final String condicion;
    private final String observacion;
    private final CategoriaImc categoria;

    public Resultado(
            final double imc,
            final String condicion,
            final String observacion,
            final CategoriaImc categoria) {
        if (!Double.isFinite(imc) || Objects.isNull(condicion)
                || Objects.isNull(observacion) || Objects.isNull(categoria)) {
            throw new ResultadoInvalidoException("El resultado del IMC contiene datos inválidos.");
        }
        this.imc = imc;
        this.condicion = condicion;
        this.observacion = observacion;
        this.categoria = categoria;
    }

    public double getImc() {
        return imc;
    }

    public String getCondicion() {
        return condicion;
    }

    public String getObservacion() {
        return observacion;
    }

    public CategoriaImc getCategoria() {
        return categoria;
    }

    public String getImcFormateado() {
        final DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
        return df.format(imc);
    }
}
