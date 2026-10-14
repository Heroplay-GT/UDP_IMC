package com.sistemas.imc.dominio.modelos;

import com.sistemas.imc.dominio.enums.CategoriaImc;
import com.sistemas.imc.dominio.excepciones.CalculoInvalidoException;
import com.sistemas.imc.dominio.vo.Altura;
import com.sistemas.imc.dominio.vo.Peso;
import java.util.Objects;

/**
 * Entidad/Modelo del dominio para el cálculo del Índice de Masa Corporal.
 * Responsabilidad única: Aplicar la fórmula IMC = peso / altura^2 y el criterio diagnóstico.
 */
public final class Calculo {

    private final Peso peso;
    private final Altura altura;

    public Calculo(final Peso peso, final Altura altura) {
        if (Objects.isNull(peso) || Objects.isNull(altura)) {
            throw new CalculoInvalidoException("El peso y la altura son obligatorios para calcular el IMC.");
        }
        this.peso = peso;
        this.altura = altura;
    }

    /**
     * Calcula el IMC y determina su categoría diagnóstica.
     *
     * @return Objeto Resultado con el valor numérico, condición y observación.
     */
    public Resultado calcular() {
        final double imc = peso.kilogramos() / (altura.metros() * altura.metros());
        final CategoriaImc categoria = CategoriaImc.deImc(imc);
        return new Resultado(imc, categoria.getCondicion(), categoria.getObservacion(), categoria);
    }

    public Peso getPeso() {
        return peso;
    }

    public Altura getAltura() {
        return altura;
    }
}
