package it.polimi.ingsw;

import it.polimi.ingsw.controller.ServerController;
import it.polimi.ingsw.controller.interpreters.JsonInterpreterServer;
import it.polimi.ingsw.controller.packets.Packet;
import it.polimi.ingsw.controller.pingManager.PingController;
import it.polimi.ingsw.utils.DebugMessages;
import it.polimi.ingsw.view.utils.CliColors;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable, Serializable {

    private final Socket    socket;
    private Scanner         input;
    private PrintWriter     output;
    JsonInterpreterServer   interpreter;
    transient private final Object    lock;
    private int index;
    private int realPlayerIndex;
    private boolean ping = false;
    private PingController pingController;


    //TODO aggiungere una funzione nel game "getIndexFromIndex" che viene chiamata quando mischio i giocatori
    public ClientHandler(Socket client,ServerController controller)
    {

        this.interpreter= new JsonInterpreterServer(0,controller);
        this.socket = client;

        this.initializeReader(client);
        this.initializeWriter(client);
        lock = new Object();

    }

    public void disconnect()
    {
        this.input.close();
        this.output.close();

        try {
            this.socket.close();
        } catch (IOException e) {
            DebugMessages.printWarning("Client aborted for endgame");
        }
    }

    /**
     * get the ping controller (its needed to setPing)
     * @return ping controller associated with this client controller
     */
    public PingController getPingController()
    {
        return this.pingController;
    }

    /**
     *  comunicate to this client handler the index of its assigned player
     * @param index the player index inside "players" model inside GAME class
     */
    public void setRealPlayerIndex(int index)
    {
        //this.interpreter.setPlayerIndex(index);
        this.realPlayerIndex = index;
    }

    /**
     *
     * @return the player index inside "players" model inside GAME class
     */
    public int getRealPlayerIndex() {
        return realPlayerIndex;
    }

    /**
     * initialize pingController class by setting ServerController  as an observer
     * @param controller sererController that contain this clientHandler
     * @return
     */
    public PingController initializePingController(ServerController controller)
    {
        this.pingController = new PingController(index,output);
        this.pingController.setObserver(controller);
        return this.pingController;
    }

    /**
     * set the index of this client inside serverController list
     * @param index
     */
    public void setIndex(int index) {
        this.interpreter.setPlayerIndex(index);
        if(this.pingController!=null) this.pingController.setIndex(index);
        this.index = index;
    }

    /**
     *
     * @return the client index inside "clients" list of serverController
     */
    public int getIndex() {
        return index;
    }

    /**
     *
     * @return the json interpreter working inside this client
     */
    public JsonInterpreterServer getInterpreter() {
        return interpreter;
    }

    /**
     *
     * @return client socket
     */
    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        waitClientMassages();
    }

    /**
     * Initialize the Input stream of the socket
     * @param s
     */
    public void initializeReader(Socket s)
    {
        try {
            this.input  = new Scanner(s.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the Output stream of the socket
     * @param s
     */
    public void initializeWriter(Socket s)
    {
        try {
            this.output = new PrintWriter(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exit condition of the waiting room
     * @return
     */
    public boolean exitCondition()
    {
        return  true;
    }

    /**
     * Wait message from the client
     */
    public void waitClientMassages()
    {
        boolean flag = true;
            while (flag) {
                //System.out.println("wait command");
                String message = this.input.nextLine();
                if (message.equals("quit")) {
                    System.out.println("Client " + socket.getInetAddress() + " Exited the server");
                    break;
                } else {
                    readMessage(message);
                    flag = exitCondition();
                }

            }
       // System.out.println("Exit loop");
    }

    /**
     * Read a message and analyze it, then get a response if available and send it back
     * @param message
     */
    public void readMessage(String message)
    {
        try {
            interpreter.analyzePacket(message);
            respondToClient();

            System.out.println("COMMAND: -> " + message);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Not JSON MESSAGE: " + message);
           // e.printStackTrace();
        }
    }

    /**
     * Get response if available and send it back
     */

    public void respondToClient()
    {
        try {
            String response = interpreter.getResponse();
            if(response!=null)
            {
                synchronized (lock) {
                System.out.println("RESPONSE : -> " + response);
                    output.println(response);
                    output.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * send a message to this specific client
     * @param p packet to send
     */
    public void sendToClient(Packet p)
    {
        //Avoid using output channel at the same time
        synchronized (lock)
        {
            System.out.println(p.generateJson());
            output.println(p.generateJson());
            output.flush();
            lock.notify();
        }

    }


}
