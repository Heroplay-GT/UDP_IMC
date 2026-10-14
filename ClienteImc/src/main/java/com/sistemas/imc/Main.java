package com.sistemas.imc;

import com.sistemas.imc.adaptadores.notificacion.AdaptadorNotificacionCliente;
import com.sistemas.imc.adaptadores.red.AdaptadorClienteUdp;
import com.sistemas.imc.adaptadores.red.CanalUdp;
import com.sistemas.imc.adaptadores.red.ProtocoloUdpMapper;
import com.sistemas.imc.aplicacion.mapper.ClienteMapper;
import com.sistemas.imc.aplicacion.servicios.ClienteImcService;
import com.sistemas.imc.entrypoint.gui.ClienteFrame;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import javax.swing.SwingUtilities;

/**
 * Punto de entrada del programa (Composition Root). Responsabilidad única: Ensamblar las
 * dependencias según los principios SOLID y la Arquitectura Hexagonal, y lanzar la interfaz
 * de usuario.
 */
public final class Main {

    private Main() {
        // Evita instanciación – clase de arranque estática
    }

    public static void main(final String[] args) {
        final AdaptadorNotificacionCliente notificador = new AdaptadorNotificacionCliente();
        final AdaptadorClienteUdp udp = new AdaptadorClienteUdp(new CanalUdp(3000), new ProtocoloUdpMapper());
        final ExecutorService executor = Executors.newSingleThreadExecutor(tarea -> {
            final Thread hilo = new Thread(tarea, "cliente-udp");
            hilo.setDaemon(true);
            return hilo;
        });
        final ClienteImcService servicio = new ClienteImcService(
                udp, notificador, new ClienteMapper(), executor);
        SwingUtilities.invokeLater(() -> {
            final ClienteFrame frame = new ClienteFrame(servicio, servicio);
            notificador.registrar(frame);
            frame.setVisible(true);
        });
    }
}
