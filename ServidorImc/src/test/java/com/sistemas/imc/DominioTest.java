package com.sistemas.imc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sistemas.imc.dominio.enums.CategoriaImc;
import com.sistemas.imc.dominio.excepciones.MedidaIncorrectaException;
import com.sistemas.imc.dominio.modelos.Calculo;
import com.sistemas.imc.dominio.modelos.Resultado;
import com.sistemas.imc.dominio.vo.Altura;
import com.sistemas.imc.dominio.vo.Peso;
import org.junit.jupiter.api.Test;

class DominioTest {

    @Test
    void calculaElImcDePesoYAltura() {
        final Resultado resultado = new Calculo(new Peso(70.0), new Altura(1.75)).calcular();

        assertEquals(22.86, resultado.getImc(), 0.01);
        assertEquals("22.86", resultado.getImcFormateado());
        assertEquals(CategoriaImc.PESO_NORMAL, resultado.getCategoria());
    }

    @Test
    void rechazaPesoYAlturaInvalidos() {
        assertThrows(MedidaIncorrectaException.class, () -> new Peso(0));
        assertThrows(MedidaIncorrectaException.class, () -> new Peso(-5));
        assertThrows(MedidaIncorrectaException.class, () -> new Altura(0));
    }

    @Test
    void clasificaLaCategoriaSegunElImc() {
        assertEquals(CategoriaImc.BAJO_PESO, CategoriaImc.deImc(18.4));
        assertEquals(CategoriaImc.PESO_NORMAL, CategoriaImc.deImc(24.9));
        assertEquals(CategoriaImc.SOBREPESO, CategoriaImc.deImc(27.0));
        assertEquals(CategoriaImc.OBESIDAD, CategoriaImc.deImc(30.0));
    }
}
