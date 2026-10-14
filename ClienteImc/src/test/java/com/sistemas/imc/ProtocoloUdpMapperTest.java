package com.sistemas.imc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sistemas.imc.adaptadores.red.ProtocoloUdpMapper;
import com.sistemas.imc.dominio.excepciones.MedidaIncorrectaException;
import com.sistemas.imc.dominio.excepciones.RespuestaServidorException;
import com.sistemas.imc.dominio.modelos.DatosImc;
import com.sistemas.imc.dominio.modelos.ResultadoImc;
import com.sistemas.imc.dominio.vo.Altura;
import com.sistemas.imc.dominio.vo.Peso;
import org.junit.jupiter.api.Test;

class ProtocoloUdpMapperTest {

    private final ProtocoloUdpMapper mapper = new ProtocoloUdpMapper();

    @Test
    void serializaElComandoCalcularConPesoYAltura() {
        final DatosImc datos = new DatosImc(new Peso(70.0), new Altura(1.75));
        assertEquals("CALCULAR;70.00;1.75", mapper.serializarCalculo(datos));
    }

    @Test
    void parseaUnaRespuestaExitosaDelServidor() {
        final ResultadoImc resultado = mapper.parsearCalculo("OK_IMC;22.86;Peso normal;Estas bien de peso");
        assertEquals(22.86, resultado.imc());
        assertEquals("Peso normal", resultado.condicion());
    }

    @Test
    void rechazaUnaRespuestaDeErrorDelServidor() {
        assertThrows(RespuestaServidorException.class, () -> mapper.parsearCalculo("ERROR;Datos inválidos"));
    }

    @Test
    void rechazaPesoYAlturaNoPositivos() {
        assertThrows(MedidaIncorrectaException.class, () -> new Peso(0));
        assertThrows(MedidaIncorrectaException.class, () -> new Altura(-1.5));
    }
}
