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
package org.avpsoft.streaming.main;

import java.io.IOException;
import org.avpsoft.streaming.net.SocketCommandServer;

/**
 *
 * @author andresvelezperez
 */
public class StreamingServer {

    public static void main(String... args) throws IOException {

        int port = 1024;

        if (args.length == 1) {
            port = Integer.valueOf(args[0]);
            System.out.printf("[StreamingServer PI] Set Port %d %n ", port);
        } else {
            System.out.println("Usage Server, Only in Raspberry Pi: org.avpsoft.streaming.main.StreamingServer port  \n");
            System.out.printf("[StreamingServer PI] Set Port Default %d %n", port);
        }

        final Thread thread = new Thread(new SocketCommandServer(port), "SocketCommandServer");
        

        Runnable runnableShutdownHook = new Runnable() {
            @Override
            public void run() {
                System.out.println("[StreamingServer PI] Server killing...");
                thread.interrupt();
                try {
                    thread.join(5000);
                } catch (InterruptedException ex) {

                }
                System.out.println("[StreamingServer PI] Server Stoped");
            }
        };

        Runtime.getRuntime().addShutdownHook(new Thread(runnableShutdownHook, "RunnableShutdownHook"));
        
        thread.start();
    }

}
