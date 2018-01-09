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
package org.avpsoft.streaming.main;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.avpsoft.streaming.net.DatagramCommandClient;
import org.avpsoft.streaming.net.DatagramStreamServer;
import org.avpsoft.streaming.net.SocketCommandClient;

/**
 *
 * @author andresvelezperez
 */
public class Streaming {

    public static void main(String... args) throws SocketException, UnknownHostException, IOException {

        if (args.length == 2) {
            new Thread(new DatagramStreamServer(Integer.valueOf(args[0]).intValue(), args[1]), "DatagramStreamServer").start();
        } else if (args.length > 2) {
            String[] cmd = new String[args.length - 2];
            System.arraycopy(args, 2, cmd, 0, cmd.length);
            new DatagramCommandClient(args[0], Integer.valueOf(args[1]).intValue(), cmd);
        } else {
            System.out.println("Usage Client, Only in Raspberry Pi: org.avpsoft.streaming.main.Streaming server port <raspistill or raspivid> <options> \n");
            System.out.println("Usage Server: org.avpsoft.streaming.main.Streaming port <jpeg or h264>  \n");
            System.out.println("Example 1:");
            System.out.println("Server: 4445 jpeg");
            System.out.println("Client: <ip server> 4445 raspistill -n -t 0 -tl 150 -th 0:0:0 -w 640 -h 480 -q 5 -o - \n");
            System.out.println("Example 2:");
            System.out.println("Server: 4445 h264");
            System.out.println("Client: <ip server> 4445 raspivid -n -t 0 -w 640 -h 480 -fps 10 -o - \n");
            System.out.println();
            System.out.println("Ingresando al Modo Comando, escriba help, para ver la ayuda.");

            SocketCommandClient socketCommandClient = new SocketCommandClient();
            Thread thread = new Thread(socketCommandClient, "socketCommandClient");
            thread.start();

//            socketCommandClient.sendCoomandSever("CONNECT localhost 1024");
//            socketCommandClient.sendCoomandSever("CONNECT 192.168.137.10 1024");
//            socketCommandClient.sendCoomandSever("SETPORT 4445");
//            socketCommandClient.sendCoomandSever("RUNPI raspivid -n -t 0 -w 640 -h 480 -fps 10 -o -");
//            socketCommandClient.sendCoomandSever("RUNPI raspistill -n -t 0 -tl 150 -th 0:0:0 -w 640 -h 480 -q 5 -o -");
//            socketCommandClient.sendCoomandSever("RUNFILE D:\\test\\admiral.264");
        }

    }

}
