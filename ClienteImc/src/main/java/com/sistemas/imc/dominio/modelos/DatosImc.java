package com.sistemas.imc.dominio.modelos;

import com.sistemas.imc.dominio.vo.Altura;
import com.sistemas.imc.dominio.vo.Peso;
import java.util.Objects;

public record DatosImc(Peso peso, Altura altura) {
    public DatosImc {
        Objects.requireNonNull(peso, "El peso es obligatorio.");
        Objects.requireNonNull(altura, "La altura es obligatoria.");
    }
}
