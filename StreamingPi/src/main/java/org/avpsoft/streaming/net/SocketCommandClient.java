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
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.SocketFactory;
import org.avpsoft.streaming.util.ConsoleCommand;
import org.avpsoft.streaming.util.ConsoleEvent;

/**
 *
 * @author andres
 */
public class SocketCommandClient implements Runnable {

    private SocketAddress socketAddress;
    private Socket socket;
    private int remotePort;
    private PrintWriter out;
    private Thread datagramStreamServerThread;
    private Thread readFromServer;

    private enum Command {

        CONNECT, SETPORT, RUNPI, RUNFILE, STOP, STATUS, BYE, UNKNOWN, HELP
    };

    public SocketCommandClient() throws IOException {

        this.socket = SocketFactory.getDefault().createSocket();
    }

    @Override
    public void run() {

        ConsoleCommand consoleCommand = new ConsoleCommand(System.in);
        consoleCommand.addConsoleEvent(new ConsoleEvent() {
            @Override
            public void consoleEvent(String string) {
                sendCoomandSever(string);
            }
        });
        Thread readFromConsole = new Thread(consoleCommand, "System.in");
        readFromConsole.start();

        boolean exit = false;
        while (!exit) {

            if (!readFromConsole.isAlive()) {
                exit = true;
            }
        }
    }

    public void sendCoomandSever(String command) {
        processCommand(command);
    }

    private Command getCommand(final String input) {

        for (Command c : Command.values()) {
            if (input.startsWith(c.name())) {
                return c;
            }
        }
        return Command.UNKNOWN;

    }

    private String[] getParameters(final String command, final String input) {

        final String[] split = input.substring(command.length()).trim().split(" ");
        return split;
    }

    private void processCommand(final String readLine) {

        try {
            Command command = getCommand(readLine);
            String[] parameters = getParameters(command.name(), readLine);

            switch (command) {
                case HELP:
                    help();
                    break;
                case CONNECT:
                    this.socketAddress = new InetSocketAddress(parameters[0], Integer.valueOf(parameters[1]));
                    connectSocket();
                    break;
                case SETPORT:
                    this.remotePort = Integer.valueOf(parameters[0]);
                    out.println(readLine);
                    break;
                case RUNFILE:
                    datagramStreamServerThread = new Thread(new DatagramStreamServer(this.remotePort, "h264"), "DatagramStreamServer");
                    datagramStreamServerThread.start();
                    out.println(readLine);
                    break;
                case RUNPI:
                    if ("raspistill".compareTo(parameters[0]) == 0) {
                        datagramStreamServerThread = new Thread(new DatagramStreamServer(this.remotePort, "jpeg"), "DatagramStreamServer");
                    } else if ("raspivid".compareTo(parameters[0]) == 0) {
                        datagramStreamServerThread = new Thread(new DatagramStreamServer(this.remotePort, "h264"), "DatagramStreamServer");
                    }
                    datagramStreamServerThread.start();
                    out.println(readLine);
                    break;
                case STATUS:
                    out.println(readLine);
                    break;
                case STOP:
                    out.println(readLine);
                    break;
                case BYE:
                    out.println(readLine);
                    break;
                default:
                    out.println(readLine);
                    break;
            }
        } catch (Exception exception) {

        }
    }

    private void connectSocket() throws IOException {
        socket.connect(socketAddress);
        out = new PrintWriter(socket.getOutputStream(), true);

        ConsoleCommand serverCommand = new ConsoleCommand(socket.getInputStream());

        serverCommand.addConsoleEvent(new ConsoleEvent() {

            @Override
            public void consoleEvent(String string) {
                System.out.printf("Server # %s %n", string);
            }
        });

        readFromServer = new Thread(serverCommand, "ServerCommand");

        readFromServer.start();
    }

    private void help() {

        InputStream resourceAsStream = SocketCommandClient.class.getResourceAsStream("Help.txt");
        int c;
        try {
            while( (c = resourceAsStream.read()) != -1 ){
                     System.out.print((char)c);
            }
            resourceAsStream.close();
        } catch (IOException ex) {
             System.out.println("Help no found!!");
        }
    }
}
