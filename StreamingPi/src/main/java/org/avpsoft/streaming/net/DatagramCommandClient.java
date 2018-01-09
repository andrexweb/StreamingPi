/*
 * Copyright (C) 2014 Andres Velez Perez
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.avpsoft.streaming.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author andresvelezperez
 */
public class DatagramCommandClient implements Runnable {

    private ProcessBuilder processBuilder;
    private DatagramSocket datagramSocket;
    private InetAddress server;
    private int port;
    private volatile boolean sendData = true;
    private Thread thread;
    private String[] command;

    public DatagramCommandClient(String server, int port, String... command) throws UnknownHostException, SocketException {

        this.command = command;
        processBuilder = new ProcessBuilder(command);
        this.server = InetAddress.getByName(server);
        datagramSocket = new DatagramSocket();
        this.port = port;

        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {

        try {
            InputStream inputStream = null;
            if (command != null && command.length > 0) {
                Process process = processBuilder.start();
                inputStream = process.getInputStream();
            } else {
                throw new IOException("Command Not Found!!");
            }

            int maxBuffer = 2048;
            byte[] buffer = new byte[maxBuffer];
            DatagramPacket datagramPacket = new DatagramPacket(buffer, maxBuffer, server, port);
            int read;
            while (sendData) {

                read = inputStream.read(buffer, 0, maxBuffer);
                if (read > 0) {
                    datagramPacket.setData(buffer, 0, read);
                    datagramPacket.setLength(read);
                    datagramSocket.send(datagramPacket);
                }

                if (read == -1) {
                   sendData = false;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
