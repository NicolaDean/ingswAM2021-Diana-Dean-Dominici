package it.polimi.ingsw;

import it.polimi.ingsw.controller.ClientController;
import it.polimi.ingsw.controller.interpreters.JsonInterpreterClient;
import it.polimi.ingsw.controller.packets.Login;
import it.polimi.ingsw.controller.packets.StartGame;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.view.utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;


public class ClientApp {

    ClientController controller;
    int port;


    public ClientApp()
    {

    }

    public void setViewType(boolean type)
    {
        this.controller = new ClientController(type);
    }

    /**
     * Show welcome page
     */
    public void start() throws IOException {


        this.controller.startGame();

        this.controller.starttolisten();

        //System.out.println("Click enter to start");


        this.controller.printHelp();


        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }





    public static boolean isValidInet4Address(String ip)
    {
        String[] groups = ip.split("\\.");

        if (groups.length != 4) {
            return false;
        }

        try {
            return Arrays.stream(groups)
                    .filter(s -> s.length() >= 1 )
                    .map(Integer::valueOf)
                    .filter(i -> (i >= 0 && i <= 255))
                    .count() == 4;
        } catch (NumberFormatException e) {
            System.out.println("-------");
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) throws IOException {
        ClientApp app = new ClientApp();

        int i=0;
        boolean viewType = true;

        for(String arg: args)
        {
            if(arg.equals("-cli")||arg.equals("-c"))
            {
                //viewType = true;
            }
            i++;
        }


        app.setViewType(viewType);//CLI poi il bool verra caricato da args
        app.start();

    }

}
