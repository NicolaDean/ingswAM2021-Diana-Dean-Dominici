package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.ClientController;
import it.polimi.ingsw.controller.packets.EndTurn;
import it.polimi.ingsw.controller.packets.ExtractionInstruction;
import it.polimi.ingsw.controller.packets.InsertionInstruction;
import it.polimi.ingsw.model.market.Market;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourceList;
import it.polimi.ingsw.utils.ConstantValues;
import it.polimi.ingsw.utils.DebugMessages;
import it.polimi.ingsw.view.observer.Observable;
import it.polimi.ingsw.view.utils.CliColors;
import it.polimi.ingsw.view.utils.InputReaderValidation;
import it.polimi.ingsw.view.utils.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CLI extends Observable<ClientController> implements View {

    Logger                  terminal; //print formatted and colored text on the cli
    InputReaderValidation   input;
    Thread                  helpThread;
    boolean                 waiting;
    int                     lastTurn;

    public CLI()
    {
        lastTurn = -1;
        waiting = true;
        input = new InputReaderValidation();
        terminal = new Logger();
    }

    public boolean helpCommands(String cmd,String message)
    {
        cmd = cmd.toLowerCase();
        switch (cmd) {
            case "h":
            case "-h":
            case "help":
                terminal.printHelp();
                customRead(message);
                return true;
            case "-quit": //quit case
                //this.quit();
                customRead(message);
                return true;

            case "-exit": //cancel case
                this.notifyObserver(controller -> {controller.sendMessage(new EndTurn());});
                customRead(message);
                return true;

            case "-startgame": //cancel case
                this.notifyObserver(ClientController::sendStartCommand);
                return true;

            case "-dashboard": //cancel case
                customRead(message);
                return true;

            case "-swapdeposits": //cancel case
                customRead(message);
                return true;

            case "-spy": //cancel case
                customRead(message);
                return true;

            default:
                return false;

        }
    }
    public String customRead()
    {
        String s = this.input.readLine();
        helpCommands(s,"");
        return s;
    }


    public String customRead(String message)
    {
        terminal.printRequest(message);
        String s = this.input.readLine();
        helpCommands(s,message);
        return s;
    }

    @Override
    public void printWelcomeScreen() {
        this.terminal.printLogo();
        this.terminal.out.setBackgroundColor(CliColors.BLACK_BACKGROUND);
        this.clickEnter();
    }

    @Override
    public void showError() {

    }

    public void clickEnter() {
        this.terminal.out.printlnColored("Click enter to continue",CliColors.RED_TEXT,CliColors.BLACK_BACKGROUND);
        this.input.enter();
        this.terminal.out.clear();
        this.terminal.out.print("\033[H\033[2J");
    }

    @Override
    public void askNickname() {

        terminal.printRequest("Type here your nickname:");

        String nickname = ".";
        do {
            nickname = input.readLine(3);
            if(nickname.length() == 0) terminal.printWarning("Nickname too short, minimum 3 letters");
        }while(nickname.length() == 0);


        String validNickname = nickname;
        this.notifyObserver(controller -> controller.setNickname(validNickname,true));

        //Example set action to do in case of NACK on Login command
        //this.notifyObserver(controller -> controller.setAckManagmentAction(View::askNickname));
    }

    @Override
    public void askServerData()
    {
        terminal.printRequest("Insert a valid server IP: ( empty for default: localhost ) ");

        String ip =".";
        int port = -1;

        do {
            ip= this.input.readLine();
            if(ip.length()==0) ip = ConstantValues.defaultIP;
            if(ip.equals(".")) terminal.printWarning("please, insert a valid IP address");
        }
        while(!this.input.validateIP(ip));

        terminal.printRequest("Insert server port: ( 0 for default: 1234 ) ");

        do {
            port = this.input.readInt();
            if(port == 0) port = 1234;
            if(!this.input.validatePortNumber(port)) terminal.printWarning(port + " is not valid a valid port number, insert a value between 1 and 65535");
        }
        while (!this.input.validatePortNumber(port));

        String validIp = ip;
        int validPort = port;

        this.terminal.printGoodMessages("Trying to connect to "+ ip + " : "+ port +"\n");
        this.notifyObserver(controller -> controller.connectToServer(validIp, validPort));
    }

    @Override
    public void askServerData(String error) {
        this.terminal.printWarning(error);
        this.input.console.nextLine();
        this.clickEnter();
        askServerData();
    }

    @Override
    public void askBuy() {
        //this.notifyObserver(controller -> controller.buyCard());

        //per evitare loop bisognera inserire nell'input reader un comando
        // che verifichi se lutente ha scritto cancel o qualcosa del genere per tprnare al menu con i comandi
    }

    @Override
    public void askProduction() {

    }

    @Override
    public void askBonusProduction() {

    }

    @Override
    public void askBasicProduction() {

    }

    @Override
    public void askMarketExtraction() {

        String msg = "Insert \"col\" or \"row\" to select the extraction mode";
        boolean direction = false;
        String in = "";
        int max = 0;
        boolean cond = true;
        do {
            in = this.customRead(msg);

            if(in.equals("col"))
            {
                direction = false;
                max = ConstantValues.marketCol;
                cond = false;
            }else if( in.equals("row"))
            {
                direction = true;
                max = ConstantValues.marketRow;
                cond = false;
            }
            else
            {
                this.terminal.printWarning("Wrong command");
                this.terminal.printRequest(msg);
            }
        }while(cond);

        msg = "Insert the row/col to extract";
        //this.terminal.printRequest(msg);
        int num = askInt(msg,"wrong market row/col number",1,max);


        boolean finalDirection = direction;
        this.notifyObserver(controller -> {controller.sendMarketExtraction(finalDirection,num);});
    }

    public int askInt(String msg,String error,int min,int max)
    {
        int num =0;
        boolean cond = true;
        do{
            String in = this.customRead(msg);
            try
            {
                num = Integer.parseInt(in);
            }
            catch (Exception e)
            {
                this.terminal.printError("Not an integer");
            }
            cond = !input.validateInt(num,min,max);



            if(cond) this.terminal.printWarning(error);
        }while(cond );

        return num;
    }

    //TODO da fare il metodo showMarket(MiniModel) che chiama quello del logger


    @Override
    public void askDiscardResource(List<Resource> resourceList) {
    }

    /**
     * Ask user in which deposit he want to insert recived resources
     * @param resourceList ask user where to put recived resources (eventualy call discard resources)
     */
    @Override
    public void askResourceInsertion(List<Resource> resourceList) {

        //resourceList = (ResourceList) resourceList;
        boolean flag = false;

        List<InsertionInstruction> insertions = new ArrayList<>();
        do
        {
            List<Resource> removed = new ResourceList();
            for(Resource res:resourceList)
            {
                int pos = 0;
                int qty = 0;
                boolean discarded = false;
                if(res.getQuantity()!= 0)
                {
                    do
                    {
                        this.terminal.printSeparator();
                        this.terminal.printResource(res);
                        this.terminal.printRequest("If you want to discard this resource type \"d\" or \"discard\"");
                        this.terminal.printRequest("If you want to keep it type the deposit number (1-3) for normale (4-5) to bonus");
                        this.terminal.printSeparator();

                        String in = this.customRead();
                        if(in.equals("discard"))
                        {
                            String msg = "How much of this resources you want to discard";
                            qty = askInt(msg,"thers not that much quantity",1,res.getQuantity());
                            Resource tmp = new Resource(res.getType(),qty);
                            removed.add(tmp);
                            //this.notifyObserver(controller->{controller.discardResources(qty)})
                            discarded = true;
                        }
                        else
                        {
                            try
                            {
                                pos = Integer.parseInt(in);
                            }
                            catch (Exception exception)
                            {
                                this.terminal.printWarning("Not an integer");
                                pos = -1;
                            }
                            if(!input.validateInt(pos,1,5)) this.terminal.printWarning("Pos not valid");
                        }

                    }while((!input.validateInt(pos,1,5)) && !discarded);

                    if(!discarded)
                    {
                        if(res.getQuantity()>1)
                        {
                            String msg = "How much of this resources you want to insert in this deposit";
                            qty = askInt(msg,"thers not that much quantity",1,res.getQuantity());
                            Resource tmp = new Resource(res.getType(),qty);
                            removed.remove(tmp);
                        }
                        else
                        {
                            removed.add(res);
                        }

                        pos = pos-1;
                        insertions.add(new InsertionInstruction(res,pos));
                    }

                }
            }
            resourceList.removeAll(removed);
        }while(resourceList.isEmpty() && flag);
       //

        this.notifyObserver(controller -> {controller.sendResourceInsertion(insertions);});
    }

    @Override
    public void askResourceExtraction(List<Resource> resourceList) {

    }

    @Override
    public void askSwapDeposit() {

    }

    @Override
    public void askTurnType() {

        this.terminal.printTurnTypesHelp();
        String cmd = customRead();
        turnTypeInterpreter(cmd);
    }

    public void waitingHelpLoop()
    {
        try
        {
            while(waiting)
            {
                this.terminal.printHelp();
                while(!this.input.bufferReady())
                {
                    Thread.sleep(100);
                }
                System.out.println("TREAD VIVO ");

                this.helpCommands(this.input.readLine(),"");

            }
        }catch (InterruptedException | IOException e)
        {
            DebugMessages.printError("OPSS");
        }

        DebugMessages.printError("Waiting thread help aborted");
    }
    @Override
    public void askCommand() {
        helpThread = new Thread(this::waitingHelpLoop);
        helpThread.start();
    }

    @Override
    public void showGameStarted() {
        this.terminal.printGoodMessages("GAME HAS STARTED");
        this.terminal.printRequest("Click enter to continue");
    }

    @Override
    public void abortHelp() {
        if(helpThread!=null)
        {
            DebugMessages.printError("HELP ABORTED");
            helpThread.interrupt();
            helpThread = null;
            waiting   =false;
        }
    }

    @Override
    public void showMarketExtraction(List<Resource> resourceList, int whiteballs) {
        terminal.printGoodMessages("You extracted the following resources from market");
        terminal.printResourceList(resourceList);

        //TODO CHIEDERE PRIMA COME CONVERTIRE LE PALLINE BIANCHE, AGGIUNGERE LE NUOVE PALLINE ALLA RESOURCE LIST
        //this.askWhiteBalls();
        this.askResourceInsertion(resourceList);

    }

    @Override
    public void askEndTurn() {
        terminal.printGoodMessages("Your last action has been sucesfuly completed");
        terminal.printRequest("Do you want to end turn? (yes or no)");

        String in = this.customRead("Do you want to end turn?");
        in = in.toLowerCase(Locale.ROOT);
        if(in.equals("yes") || in.equals("y")) this.notifyObserver(controller -> controller.sendMessage(new EndTurn()));
        else
        {
            if(lastTurn == 1)
            {
                this.askBuy();
            }
            else if(lastTurn == 2)
            {
                this.notifyObserver(controller -> controller.sendMessage(new EndTurn()));
            }
            else if(lastTurn == 3)
            {
                this.askProduction();
            }
            else
            {

            }
        }
    }

    @Override
    public void playerLogged(String nickname) {
        this.terminal.printGoodMessages(nickname + " joined the game");
    }



    public void turnTypeInterpreter(String cmd)
    {
        switch (cmd) {
            case "market":
            case "2":
                this.askMarketExtraction();
                break;
            case "3":
                this.askProduction();
                break;
            default:
                this.askBuy();
                break;
        }
    }

}
