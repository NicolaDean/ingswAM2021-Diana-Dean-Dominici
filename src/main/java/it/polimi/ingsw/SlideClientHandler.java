package it.polimi.ingsw;


import it.polimi.ingsw.controller.ServerController;
import it.polimi.ingsw.controller.interpreters.JsonInterpreterServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

class SlideClientHandler implements Runnable {

    private Socket socket;
    public SlideClientHandler(Socket socket) {
        this.socket = socket;
    }
    public void run() {
        try {
            JsonInterpreterServer interpreter= new JsonInterpreterServer(0,new ServerController());
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream());
// Leggo e scrivo nella connessione finche' non ricevo "quit"
            while (true) {
                String line = in.nextLine();
                if (line.equals("quit")) {
                    break;
                } else {
                    System.out.println("COMMAND: -> " + line);
                    try
                    {
                        interpreter.analyzePacket(line);
                        out.println(interpreter.getResponse());
                        out.flush();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
// Chiudo gli stream e il socket
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
