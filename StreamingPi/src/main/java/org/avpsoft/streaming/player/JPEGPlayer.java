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
package org.avpsoft.streaming.player;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;
import javax.swing.JFrame;

/**
 *
 * @author andresvelezperez
 */
public class JPEGPlayer implements Runnable{
    
    private volatile boolean readData = true;
    private InputStream inputStream;
    private String fileName;
    private PlayerFrame playerFrame;
    
    public JPEGPlayer(InputStream inputStream, String fileName){
        
        this.inputStream = inputStream;
        this.fileName = fileName;
        
        JFrame frame = new JFrame("Player");
        playerFrame = new PlayerFrame();

        frame.getContentPane().add(playerFrame, BorderLayout.CENTER);

        // Finish setting up the frame, and show it.
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        playerFrame.setVisible(true);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(new Dimension(645, 380));

        new Thread(this).start();
        
    }

    @Override
    public void run() {
        System.out.println("Playing " + fileName);
        playStream();
    }
    
    public boolean playStream() {
       int maxBuffer = 2048;
        byte[] buffer = new byte[maxBuffer];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        boolean inicioImagen = false;
        byte ant1 = (byte) 0xD1;
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        DatagramPacket datagramPacket;
        int length = 0;
        // jpeg end of file EOI= FFD9, jpg file generated per raspistill  contain (two) multiple EOI
        int EOI = 0;
        while(readData){
            try {
                length = inputStream.read(buffer, 0, maxBuffer);
                for (int i = 0; i < length; i++) {
                    //System.out.println(buffer[i]);
                    if (!inicioImagen && buffer[i] == (byte) 0xD8 && ant1 == (byte) 0xFF) {
                        inicioImagen = true;
                        byteArrayOutputStream.write((byte)0xFF);
                    }
                    if (inicioImagen) {
                        byteArrayOutputStream.write(buffer[i]);
                    }
                    if (inicioImagen && buffer[i] == (byte) 0xD9 && ant1 == (byte) 0xFF) {
                        if (EOI > 0) {
                            inicioImagen = false;
                            try {
                                playerFrame.setImage(toolkit.createImage(byteArrayOutputStream.toByteArray()));
                            } catch (Exception e) {
                            }
                            byteArrayOutputStream.reset();
                            EOI = 0;
                        } else {
                            EOI++;
                        }
                    }
                    ant1 = buffer[i];
                }

            } catch (SocketTimeoutException stex) {

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        return true;
    }
    
}
