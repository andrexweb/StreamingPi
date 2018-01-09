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

import com.twilight.h264.player.H264Player;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import org.avpsoft.streaming.player.JPEGPlayer;

/**
 *
 * @author andresvelezperez
 */
public class DatagramStreamServer implements Runnable {

    private DatagramSocket datagramSocket;
    private volatile boolean stop = true;
    private PipedOutputStream pipedOutputStream;
    

    public DatagramStreamServer(int port,String player) throws SocketException, IOException {

        datagramSocket = new DatagramSocket(port);
        datagramSocket.setSoTimeout(500);
        
        pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
    
        if("jpeg".compareToIgnoreCase(player) == 0 || "jpg".compareToIgnoreCase(player) == 0){
            JPEGPlayer jPEGPlayer = new JPEGPlayer(pipedInputStream, "Stream-JPEG");
        }
        
        if("h264".compareToIgnoreCase(player) == 0){
            H264Player h264Player = new H264Player(pipedInputStream, "Stream");
        }
        
    }

    @Override
    public void run() {
        this.stop = false;
        int maxBuffer = 2048;
        byte[] buffer = new byte[maxBuffer];
        DatagramPacket datagramPacket;
        int length = 0;
       
        while (!stop) {

            try {
                datagramPacket = new DatagramPacket(buffer, maxBuffer);
                datagramSocket.receive(datagramPacket);
                length = datagramPacket.getLength();
         
                pipedOutputStream.write(buffer, 0, length);
                pipedOutputStream.flush();
                
                if(Thread.currentThread().isInterrupted()){
                    stop = true;
                }

            } catch (SocketTimeoutException stex) {

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

}
