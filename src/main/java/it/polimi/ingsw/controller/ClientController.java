package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.interpreters.JsonInterpreterClient;
import it.polimi.ingsw.controller.packets.*;
import it.polimi.ingsw.controller.pingManager.PongController;
import it.polimi.ingsw.model.MiniModel;
import it.polimi.ingsw.model.dashboard.Deposit;
import it.polimi.ingsw.model.market.balls.BasicBall;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.view.GUI;
import it.polimi.ingsw.utils.DebugMessages;
import it.polimi.ingsw.view.utils.ErrorManager;
import it.polimi.ingsw.view.CLI;
import it.polimi.ingsw.view.View;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public class ClientController implements Runnable{

    private Socket server;
    private Scanner input;
    private PrintWriter output;
    private JsonInterpreterClient interpreter;


    private PongController pongController;
    private int                   index;
    private boolean               connected;

    private ErrorManager          errorManager;
    private View                  view;   //Interface with all view methods
    private MiniModel             model;
    private AckExample            resolver;

    public ClientController(boolean type)
    {
        this.connected = false;
        if(type)view = new CLI();
        else view = new GUI();//GUI()

        this.view.setObserver(this);

        this.interpreter= new JsonInterpreterClient(this);
        this.errorManager = new ErrorManager();
        this.resolver = new AckExample();
        model= new MiniModel(); //provvisiorio

    }

    /**
     * set all initial information into miniMarted
     * @param miniModel miniModel
     * @param balls miniMarketBall
     * @param discarted miniBallDiscarted
     */
    public void setInformation(MiniModel miniModel,BasicBall[][] balls, BasicBall discarted){
        view.setMarket(balls,discarted);
        this.model = miniModel;
    }

    public View getView() {
        return view;
    }

    public void setAckManagmentAction(Consumer <View> action)
    {
        this.resolver.setAction(action);
    }

    public MiniModel getMiniModel()
    {
        return this.model;
    }

    /**
     * Add new logged player to the minimodel
     * @param index
     * @param nickname
     */
    public void addPlayer(int index,String nickname)
    {
        this.view.playerLogged(nickname);
        this.model.addPlayer(nickname,index);
    }

    public ClientController() {

    }

    public void printGameStarted()
    {
        this.view.showGameStarted();
    }

    public void exampleACK(int code)
    {
        errorManager.getErrorMessageFromCode(code);//TODO magari oltre al numero passo la view che chiamera "showError"
    }


    public void setConnected(boolean conn)
    {
        this.connected = conn;
    }
    public void setIndex(int index)
    {
        if(!connected)
        {
            this.index = index;
            this.setConnected(true);
            this.pongController = new PongController(index,output);
            new Thread(this.pongController);
        }
    }

    public void starttolisten(){

        Thread t = new Thread(this);
        DebugMessages.printNetwork("\nmi metto in ascolto \n");
        t.start();
    }

    public boolean isConnected() {
        return connected;
    }

    public PongController getPongController() {
        return pongController;
    }

    public void abortHelp()
    {
        this.view.abortHelp();
    }
    public void startGame()
    {

        //0System.out.println("Start Game");
        view.printWelcomeScreen();
        view.askServerData();
        view.askNickname();
    }


    public void sendStartCommand()
    {
        this.sendMessage(new StartGame());
    }

    public void printHelp()
    {
        view.askCommand();
    }
    /**
     * Open a connection with this server
     * @param ip server ip
     * @param port server port
     */
    public void connectToServer(String ip,int port) {
        try {
            this.interpreter = new JsonInterpreterClient(this);
            this.server = new Socket(ip,port);
            initializeReader(server);
            initializeWriter(server);
            new Thread(this);//create input messages manager thread
            setConnected(true);
            this.pongController = new PongController(index, output);

        } catch (IOException e) {
            setConnected(false);
            view.askServerData("Connection failed, try insert new server data:");
        }
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
     * Set his own nickname to minimodel
     * @param nickname his nickname
     * @param singlePlayer if hes singleplayer
     */
    public void setNickname(String nickname,boolean singlePlayer)
    {
        if(singlePlayer)
            sendMessage(new LoginSinglePlayer(nickname));
        else
            sendMessage(new Login(nickname));
    }


    public void storageUpdate(Deposit[] deposits)
    {
        this.model.updateStorage(deposits);
    }

    public void showStorage()
    {
        this.view.showStorage(this.model.getStorage());
    }
    /**
     * send packet to server
     * @param p
     */
    public void sendMessage(Packet p)
    {
        this.output.println(p.generateJson()); ;   //(p.generateJson());
        this.output.flush();
    }
    /**
     * wait server messages
     */
    public void waitMessage()
    {
        String message = this.input.nextLine();

        Thread t = new Thread(()->{

            try
            {
                this.interpreter.analyzePacket(message);
                this.respondToClient();
                DebugMessages.printNetwork("Recived command:" + message);
            }catch (Exception e)
            {
                e.printStackTrace();
                DebugMessages.printError("Not a json Message: "+ message);
            }

        });
        t.start();
    }

    /**
     * //TODO Obsolete function, client almost never respond to server message immediatly but use directly "sendMessage"
     * Send respond to server
     */
    public void respondToClient()
    {
        try {
            String response = interpreter.getResponse();
            if(response!=null)
            {
                output.println(response);
                output.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showMarketResult(List<Resource>res,int whiteBalls)
    {
        this.view.showMarketExtraction(res,whiteBalls);
    }

    public void excecuteTurn()
    {
        this.view.askTurnType();
    }
    public void askEndTurn(){this.view.askEndTurn();}
    public void sendResourceInsertion(List<InsertionInstruction> instructions)
    {
        this.sendMessage(new StorageMassInsertion(instructions));
    }
    /**
     * Send a packet of market exrtrction
     * @param dir col = true row = false
     * @param pos col,row to select
     */
    public void sendMarketExtraction(boolean dir,int pos)
    {
        //view.showMarket(minimodel.getMiniMarket)
        this.sendMessage(new MarketExtraction(dir,pos));
    }
    /**
     * This thread listen to the server packet and analyze them
     */
    @Override
    public void run() {
        //Thread con server
        DebugMessages.printNetwork("Waiting message thread chreated");
        while(this.connected)
        {
            this.waitMessage();
        }
    }
}
