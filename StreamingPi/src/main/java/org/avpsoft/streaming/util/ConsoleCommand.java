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
package org.avpsoft.streaming.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author andres
 */
public class ConsoleCommand implements Runnable{
    
    private List<ConsoleEvent> consoleEventList = new ArrayList<ConsoleEvent>();
    private volatile boolean stop = true;
    private BufferedReader bufferedReader;
    
    public ConsoleCommand(InputStream inputStream){
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream)); 
    }
    
    public void addConsoleEvent(ConsoleEvent consoleEvent){
        this.consoleEventList.add(consoleEvent);
    }

    @Override
    public void run() {
        this.stop = false;
        String input = null;
        
        while(!stop){
            try {
                
                input = bufferedReader.readLine();

                if(input != null){
                    if("null".compareToIgnoreCase(input) == 0){
                        stop = true;
                        break;
                    }
                    for(ConsoleEvent consoleEvent: consoleEventList){
                        consoleEvent.consoleEvent(input);
                    }
                }else{
                    stop = true;
                }
                if(Thread.currentThread().isInterrupted()){
                    stop = true;
                }
            } catch (IOException ex) {
                stop = true;
            }
        }
        
    }
    
}
