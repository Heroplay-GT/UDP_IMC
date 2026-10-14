package com.sistemas.imc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sistemas.imc.adaptadores.red.AdaptadorNotificacionEvento;
import com.sistemas.imc.adaptadores.red.AdaptadorSalidaUdp;
import com.sistemas.imc.adaptadores.red.CanalUdp;
import com.sistemas.imc.adaptadores.red.mapper.UdpNetworkMapper;
import com.sistemas.imc.aplicacion.mapper.PeticionMapper;
import com.sistemas.imc.aplicacion.mapper.ImcMapper;
import com.sistemas.imc.aplicacion.puertos.entrada.ProcesarPeticionUdpInputPort;
import com.sistemas.imc.aplicacion.servicios.ProcesarPeticionUdpService;
import com.sistemas.imc.dominio.puertos.salida.PuertoSalidaRed;
import com.sistemas.imc.entrypoint.udp.ReceptorPeticionesUdp;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Prueba de integración que levanta el servidor UDP real (canal + receptor + servicio de
 * aplicación) y verifica el protocolo de red completo usando un DatagramSocket de cliente.
 */
class IntegracionUdpTest {

    private CanalUdp canalUdp;
    private ReceptorPeticionesUdp receptor;
    private DatagramSocket clienteSocket;
    private int puertoServidor;

    @BeforeEach
    void iniciarServidor() throws IOException {
        canalUdp = new CanalUdp();
        final AdaptadorNotificacionEvento notificador = new AdaptadorNotificacionEvento();
        final PuertoSalidaRed puertoSalidaRed = new AdaptadorSalidaUdp(canalUdp, new UdpNetworkMapper(), notificador);
        final ProcesarPeticionUdpInputPort servicio = new ProcesarPeticionUdpService(
                puertoSalidaRed, notificador, new PeticionMapper(), new ImcMapper());

        puertoServidor = 15123;
        canalUdp.abrir(puertoServidor);

        receptor = new ReceptorPeticionesUdp(canalUdp, servicio, notificador);
        receptor.iniciar();

        clienteSocket = new DatagramSocket();
        clienteSocket.setSoTimeout(2000);
    }

    @AfterEach
    void detenerServidor() {
        receptor.detener();
        canalUdp.cerrar();
        clienteSocket.close();
    }

    @Test
    void respondeConectadoOkAlComandoConectar() throws IOException {
        final String respuesta = enviarYRecibir("CONECTAR");
        assertTrue(respuesta.startsWith("CONECTADO_OK;"));
    }

    @Test
    void calculaElImcPorRed() throws IOException {
        final String respuesta = enviarYRecibir("CALCULAR;70;1.75");
        final String[] partes = respuesta.split(";", -1);
        assertEquals("OK_IMC", partes[0]);
        assertEquals("22.86", partes[1]);
    }

    @Test
    void respondeErrorAntePeticionMalFormada() throws IOException {
        final String respuesta = enviarYRecibir("CALCULAR;70");
        assertTrue(respuesta.startsWith("ERROR;"));
    }

    private String enviarYRecibir(final String mensaje) throws IOException {
        final byte[] salida = mensaje.getBytes(StandardCharsets.UTF_8);
        clienteSocket.send(new DatagramPacket(
                salida, salida.length, InetAddress.getLoopbackAddress(), puertoServidor));

        final byte[] buffer = new byte[2048];
        final DatagramPacket entrada = new DatagramPacket(buffer, buffer.length);
        clienteSocket.receive(entrada);
        return new String(entrada.getData(), 0, entrada.getLength(), StandardCharsets.UTF_8);
    }
}
