package com.sistemas.imc.dominio.enums;

/** Clasificación diagnóstica del Índice de Masa Corporal (IMC), según la guía de la asignatura. */
public enum CategoriaImc {
    BAJO_PESO("Peso bajo", "Debes consultar un Medico, tu peso es muy bajo"),
    PESO_NORMAL("Peso normal", "Estas bien de peso"),
    SOBREPESO("Sobrepeso", "Debes bajar un poco de peso"),
    OBESIDAD("Obesidad", "Debes consultar un Medico, tu peso es muy alto");

    private final String condicion;
    private final String observacion;

    CategoriaImc(final String condicion, final String observacion) {
        this.condicion = condicion;
        this.observacion = observacion;
    }

    public String getCondicion() {
        return condicion;
    }

    public String getObservacion() {
        return observacion;
    }

    /**
     * Determina la categoría según el valor del IMC.
     *
     * @param imc Índice de masa corporal calculado (kg/m2).
     * @return La categoría correspondiente.
     */
    public static CategoriaImc deImc(final double imc) {
        if (imc < 18.5) {
            return BAJO_PESO;
        } else if (imc < 25.0) {
            return PESO_NORMAL;
        } else if (imc < 30.0) {
            return SOBREPESO;
        } else {
            return OBESIDAD;
        }
    }
}
