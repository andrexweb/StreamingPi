/*
 * Copyright (C) 2015 andres
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
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author andres
 */
public class SocketCommandServer implements Runnable {

    private ServerSocket serverSocket;
    private volatile boolean stop = false;

    public SocketCommandServer(final int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {

        try {

            while (!stop) {

                Socket socket = serverSocket.accept();
                System.out.println("[StreamingServer PI] New Conection");
                Thread threadCliente = new Thread(new SocketCommand(socket), "SocketCommand");
                threadCliente.start();
                try {
                    threadCliente.join();
                    System.out.println("[StreamingServer PI] Finish Conection");
                } catch (InterruptedException ex) {
                    stop = true;
                }

                if (Thread.currentThread().isInterrupted()) {
                    stop = true;
                }
            }

            serverSocket.close();

        } catch (IOException ex) {
            throw new RuntimeException("ServerSocketPi", ex);
        }

    }

}
