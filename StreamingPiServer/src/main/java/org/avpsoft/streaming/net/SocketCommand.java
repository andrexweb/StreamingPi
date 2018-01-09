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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.avpsoft.streaming.stream.StreamFile;
import org.avpsoft.streaming.stream.StreamProcess;

/**
 *
 * @author andres
 */
public class SocketCommand implements Runnable {

    private Socket socket;
    private Thread threadDatagramCommandClient;
    private InetSocketAddress inetSocketAddress;

    private PrintWriter out;
    private BufferedReader in;

    private volatile boolean stop = false;

    private enum Command {

        CONNECT, SETPORT, RUNPI, RUNFILE, STOP, STATUS, BYE, UNKNOWN
    };

    public SocketCommand(final Socket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        String readLine;
        try {

            out.println("Streaming PI Server");
            out.printf("IP Client: %s \n", socket.getInetAddress().getHostAddress());

            while (!stop) {

                if (socket.isClosed() || Thread.currentThread().isInterrupted()) {
                    stop = true;
                }
                try {
                    readLine = in.readLine();
                    processCommand(readLine);
                } catch (SocketException socketException) {
                    stop = true;
                }
            }
            out.println("Server Stoped");

            out.flush();
            out.close();
            in.close();
            socket.close();
        } catch (IOException ex) {
            throw new RuntimeException("ServerSocketPi", ex);
        }
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
                case SETPORT:
                    int port = Integer.valueOf(parameters[0]);
                    this.inetSocketAddress = new InetSocketAddress(socket.getInetAddress(), port);
                    out.printf("Connect to %s port %d %n", socket.getInetAddress(), port);
                    break;
                case RUNFILE:
                    startThreadDatagramCommandClient_F(parameters);
                    break;
                case RUNPI:
                    startThreadDatagramCommandClient_D(parameters);
                    break;
                case STATUS:
                    if (threadDatagramCommandClient != null) {
                        if (threadDatagramCommandClient.isAlive()) {
                            out.printf("Is Running ... %n");
                        } else {
                            stopThreadDatagramCommandClient();
                        }
                    } else {
                        out.printf("Is Not Running ... %n");
                    }
                    break;
                case STOP:
                    stopThreadDatagramCommandClient();
                    break;
                case BYE:
                    stopThreadDatagramCommandClient();
                    stop = true;
                    out.printf("Bye, bye ... %n");
                    break;
                default:
                    out.printf("Unknown command %s %n", command);
                    break;
            }
        } catch (NumberFormatException | UnknownHostException | SocketException | FileNotFoundException exception) {
            stopThreadDatagramCommandClient();
            out.printf("Exception: %s %n", exception.getMessage());
        }
    }

    private void stopThreadDatagramCommandClient() {

        if (threadDatagramCommandClient != null) {
            threadDatagramCommandClient.interrupt();
            try {
                threadDatagramCommandClient.join();
            } catch (InterruptedException interruptedException) {
                stop = true;
            }
            threadDatagramCommandClient = null;
            out.printf("Is stoped... %n");
        }
    }

    private void startThreadDatagramCommandClient_D(String... parameters) throws UnknownHostException, SocketException, FileNotFoundException {
        if (threadDatagramCommandClient == null) {
            threadDatagramCommandClient = new Thread(new DatagramStreamClient(inetSocketAddress, new StreamProcess(parameters)), "DatagramCommandClient");
            threadDatagramCommandClient.start();
        } else {
            out.printf("Is running... %n");
        }
    }

    private void startThreadDatagramCommandClient_F(String... parameters) throws UnknownHostException, SocketException, FileNotFoundException {
        if (threadDatagramCommandClient == null) {
            threadDatagramCommandClient = new Thread(new DatagramStreamClient(inetSocketAddress, new StreamFile(parameters)), "DatagramCommandClient");
            threadDatagramCommandClient.start();
        } else {
            out.printf("Is running... %n");
        }
    }

}
