/*
 * Copyright (C) 2015 Andres Velez Perez
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
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.avpsoft.streaming.stream.Stream;
import org.avpsoft.streaming.stream.StreamFile;

/**
 *
 * @author andresvelezperez
 */
public class DatagramStreamClient implements Runnable {

    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private Stream stream;

    private static final int maxBuffer = 2048;
    private byte[] buffer = new byte[maxBuffer];

    private volatile boolean stop = false;

    public DatagramStreamClient(final SocketAddress socketAddress, final Stream stream) throws UnknownHostException, SocketException {
        this.stream = stream;
        this.datagramPacket = new DatagramPacket(buffer, maxBuffer, socketAddress);
        this.datagramSocket = new DatagramSocket();
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        int read = 0;
        try {
            if (stream != null) {
                inputStream = stream.getInputStream();
            } else {
                throw new NullPointerException("stream is null");
            }

            while (!stop) {

                if (!stream.isAvailable() || Thread.currentThread().isInterrupted() || read == -1) {
                    stop = true;
                }

                read = inputStream.read(buffer, 0, maxBuffer);
                if (read > 0) {
                    datagramPacket.setData(buffer, 0, read);
                    datagramPacket.setLength(read);
                    datagramSocket.send(datagramPacket);
                }

                if (stream instanceof StreamFile) {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException ignore) {
                        stop = true;
                    }
                }
            }

            stream.close();

            inputStream.close();
            datagramPacket = null;
            datagramSocket.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
