package it.polimi.ingsw.controller;

import it.polimi.ingsw.ClientHandler;
import it.polimi.ingsw.controller.packets.*;
import it.polimi.ingsw.enumeration.ErrorMessages;
import it.polimi.ingsw.enumeration.ResourceType;
import it.polimi.ingsw.exceptions.AckManager;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.ProductionCard;
import it.polimi.ingsw.model.dashboard.Dashboard;
import it.polimi.ingsw.model.dashboard.Deposit;
import it.polimi.ingsw.model.market.Market;
import it.polimi.ingsw.model.minimodel.MiniPlayer;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourceList;
import it.polimi.ingsw.utils.DebugMessages;
import it.polimi.ingsw.view.utils.CliColors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static it.polimi.ingsw.enumeration.ResourceType.*;

public class ServerController{

    //view
    protected Game                game;
    protected List<ClientHandler> clients;
    protected final Object              lock;
    protected int                 currentClient = 0;
    protected boolean             isSinglePlayer;
    protected boolean             isStarted;
    protected  int         idpartita;

    /**
     *
     * @param real if true create a real controller(with clientHandlers) if false an emptyController for accept Login in waitingRoom
     */
    public ServerController(boolean real)
    {
        this.currentClient = 0;
        this.isStarted = false;
        this.game = new Game();
        this.lock = new Object();
        if(real)  clients = new ArrayList<>();//If is a real controller create also ClientHandlers
    }



    public void setIdpartita(int idpartita) {
        this.idpartita = idpartita;
    }

    public int getIdpartita() {
        return idpartita;
    }

    public void warning(String msg)
    {
        CliColors c = new CliColors(System.out);
        c.printlnColored(msg,CliColors.YELLOW_TEXT);
    }


    /**
     * Send a broadcast to all clients (except sender)
     * @param except if -1 send message also to itself
     * @param message packet to broadcast
     */
    public void broadcastMessage(int except,Packet message)
    {
        if(clients == null) return;

        this.warning("Broadcast sending: "+ message.generateJson());
        for(ClientHandler c: clients)
        {
            if(c.getIndex()!=except || except == -1) c.sendToClient(message);
        }
    }

    /**
     * Remove client logged but disconnected before start (change also  index of others clinetHandlers to match pings packet)
     * @param index index of this client in the client handler list
     */
    public void removeClient(int index)
    {
        synchronized (this.lock)
        {
            this.warning("Client "+ index + " removed from game number "+ this.getIdpartita());
            this.clients.remove(index);
            currentClient = currentClient -1;

            //Change other client index
            int i=0;
            for(ClientHandler c : clients)
            {
                this.warning("Now Client "+  c.getIndex() + " is -> " + i);

                c.setIndex(i);
                i++;
            }
            this.lock.notify();
        }

    }


    /**
     * "fake" controller need this function to "notify" waiting room that user logged as single player
     * method used by "LoginSinglePlayer"
     */
    public void setSinglePlayer() {
        isSinglePlayer = true;
    }

    /**
     *  method used by waiting room to check if user logged as single player to "fake controler"
     * @return return true if user logged as single player
     */

    public boolean isSinglePlayer()
    {
        return this.isSinglePlayer;
    }

    /**
     *
     * @return the list of clients connected to this match
     */
    public List<ClientHandler> getClients() {
        return clients;
    }

    /**
     * add a player to the match
     * @param client client to add
     */
    public void addClient(ClientHandler client) {
        this.clients.add(client);

        client.setIndex(this.clients.size()-1);
        new Thread(client.initializePingController(this)).start();
    }



    public void sendPositionUpdate(int pos,int clientIndex)
    {
        if(this.isStarted)
        {
            this.sendMessage(new IncrementPosition(pos,this.clients.get(clientIndex).getRealPlayerIndex()),clientIndex);
        }
    }

    public Game getGame() {
        return game;
    }

    /**
     * Start the game (called from StartGame packet)
     * //TODO Send a broadcast to all player with "GAME STARTED" packet (and remove from clientApp the line with automatic start sender)
     * @throws Exception (if game cant start)
     */
    public void startGame() throws Exception
    {
        //TODO RARE EXCEPTION:
        //TODO USO SYNCRONIZED per lockasre la lista di client ed evitare che un client venga rimosso mentre inizio il game,
        // o inizi il game prima che un giocatore venga rimosso
        //Se game non ha abbastanza giocatori lancia eccezione e manda NACK
        synchronized (this.lock) {
            for(Player player:this.game.getPlayers()) player.setObserver(this); //set observer for papal space

            if (!this.isStarted) {

                if(this.game.getPlayers().size()==1 && !isSinglePlayer)
                {
                    this.sendMessage(new ACK(12), 0);
                    return;
                }



                this.warning("\n-----------Game " + this.getIdpartita() + " avviato---------- \n");

                int[] realIndex = game.startGame();


                int i = 0;
                for (ClientHandler c : clients) {
                    c.getPingController().setGameStarted();
                    c.setRealPlayerIndex(realIndex[i]);
                    i++;
                }

                int firstPlayer = this.game.getPlayer(0).getControllerIndex();
                currentClient = firstPlayer;
                //Send broadcast with game started packet

                i=0;
                MiniPlayer[] miniPlayers = this.generateMiniPlayer();
                for (ClientHandler c : clients) {
                    DebugMessages.printError("PLAYER "+ c.getRealPlayerIndex() + "->controller: "+i);
                    c.sendToClient(generateGameStartedPacket(miniPlayers,c.getRealPlayerIndex()));
                    i++;
                }

                this.isStarted = true;
                TimeUnit.SECONDS.sleep(2);
                //notify first player the is its turn
                //this.clients.get(firstPlayer).sendToClient(new TurnNotify());
                turnNotifier();
            } else {
                this.warning("Game already started");
                //return null;
            }
            //DebugMessages.printGeneric("\n new currplayer: "+ currentClient + ", total players: "+this.clients.size()+"\n");
            for (Player p:game.getPlayers()) {
                p.getDashboard().getStorage().safeInsertion(new Resource(COIN,1), 0);
                p.getDashboard().getStorage().safeInsertion(new Resource(SHIELD,2), 1);
                p.getDashboard().getStorage().safeInsertion(new Resource(ROCK,3), 2);
                sendStorageUpdate(p.getControllerIndex());

            }
            this.lock.notify();
        }

    }


    /**
     * Generate entire minimodel/miniplayers for the "startGame" packet (sended only the first time then update only changed parts)
     * @param index player index inside game
     * @return game started packet
     */
    public Packet generateGameStartedPacket(MiniPlayer[] players,int index){
        return new GameStarted(index,players,game.getProductionDecks(),game.getMarket().getResouces(),game.getMarket().getDiscardedResouce());
    }

    /**
     *
     * @return a list of miniplayer
     */
    public MiniPlayer[] generateMiniPlayer(){
        MiniPlayer[] players= new MiniPlayer[game.getNofplayers()];
        int i=0;

        List<Resource> resources = new ResourceList();
        if(DebugMessages.infiniteResources)
        {
            resources.add(new Resource(COIN,100));
            resources.add(new Resource(SERVANT,100));
            resources.add(new Resource(SHIELD,100));
            resources.add(new Resource(ROCK,100));
        }
        for (Player p:game.getPlayers()){
            players[i]=new MiniPlayer(p.getNickname());
            players[i].setStorage(p.getDashboard().getStorage().getDeposits());
            players[i].updateChest(resources);
            players[i].setIndex(i);
            LeaderCard[] leaderCards = this.game.get4leaders();
            players[i].setLeaderCards(leaderCards);
            p.setLeaders(leaderCards);
            if(DebugMessages.infiniteResources) {
                p.chestInsertion(resources);
            }
            i++;
        }
        return players;
    }

    /**
     * check papalspace position and ad point
     */
    public void checkPapalSpaceActivation(){
        int nOfplayer= this.game.getPlayers().size();
        int[] tmp_score = new int[nOfplayer];
        for (int i = 0; i < nOfplayer; i++) { //save score to check if someone activate a papal cell
                tmp_score[i]=this.game.getPlayers().get(i).getScore();
        }

        this.game.papalSpaceCheck();  //increment point

        int index=0;
        boolean out=false;
        for (int i = 0; i < nOfplayer; i++) { //check if someone have activated papal space
            if(tmp_score[i]!=this.game.getPlayers().get(i).getScore()){
                out=true;
            }
        }

        for (int j = 0; j < nOfplayer-1; j++) { //found player that activate papal cell
            if(this.game.getPlayers().get(j).getScore()>this.game.getPlayers().get(j+1).getScore())
                index=j;
        }

        if(out){
            this.broadcastMessage(-1, new PapalScoreActiveted(index));
        }
    }

    /**
     * send turn packet to the next player and notify the other withthe name of current player
     */
    public void turnNotifier()
    {
        clients.get(currentClient).sendToClient(new TurnNotify());
        for (Player p: this.getGame().getPlayers()) {
            if (p.getControllerIndex() != currentClient)
                clients.get(p.getControllerIndex()).sendToClient(new NotifyOtherPlayerTurn(this.game.getCurrentPlayer().getNickname()));

        }
    }


    /**
     * Check if the player that send the command is the current player
     * @param playerIndex index of the packet sender
     * @return true if is the current player
     */
    public boolean isRightPlayer(int playerIndex)
    {
        //TODO CHECK BETTER THE BOOLEAN EXPRESSION
        return (this.game.getCurrentPlayerIndex() == this.clients.get(playerIndex).getRealPlayerIndex());
        //return true;
    }

    /**
     * Set a pending cost response
     * @param dashboard dashboard from wich extract the pending cost
     */
    public Packet setPendingCost(Dashboard dashboard)
    {
        return  new PendingCost(dashboard.getPendingCost());
    }


    /**
     *
     * @param pos1   first leader
     * @param pos2   second leader
     * @param index  controller index
     * @return  a response packet (only if exception occur)
     */
    public Packet setLeaders(int pos1,int pos2, int index)
    {
        int playerIndex = this.clients.get(index).getRealPlayerIndex();
        //this.game.getCurrentPlayer().setLeaders(leaders);
        this.game.getCurrentPlayer().setLeaders(pos1,pos2);
        this.broadcastMessage(-1,new UpdateLeaders(this.game.getCurrentPlayer().getLeaders(),playerIndex));
        return null;
    }
    /**
     *
     * @return a NACK packet indicating that is not your turn
     */
    public Packet notYourTurn()
    {
        return new ACK(ErrorMessages.NotYourTurn);
    }


    public void sendPendingCard(int index)
    {
        this.broadcastMessage(-1,this.game.getCurrentPlayer().getPendingCard());
    }
    /**
     * Player "player" buy the card in position x,y of the deks
     * @param x level
     * @param y color
     * @param pos where i want to put the card
     * @param player packet sender index
     */
    public Packet buyCard(int x,int y,int pos,int player){
        Player p = this.game.getCurrentPlayer();

        if(!isRightPlayer(player)) return this.notYourTurn();

        ProductionCard card = this.game.drawProductionCard(x,y);
        try
        {
            card.buy(p,pos);

            this.game.getProductionDecks()[x][y].pop();
            //Set a pending card, when user finish to pay it i will send the updateBuyedCard packet i added to player
            ProductionCard newCard = this.game.getProductionDecks()[x][y].peek();
            p.setPendingBuy(newCard,x,y,pos,this.clients.get(player).getRealPlayerIndex());

            return setPendingCost(p.getDashboard());
        } catch (AckManager err) {
            return err.getAck();
        }

    }

    /**
     * production of a player
     * @param pos which card i wanna use
     * @param player client index
     */
    public Packet production(int pos,int player)
    {
        Player p = this.game.getCurrentPlayer();
        Dashboard dashboard = p.getDashboard();

        if(!isRightPlayer(player)) return this.notYourTurn();

        try {
            dashboard.production(p,pos);
            return setPendingCost(dashboard);
        } catch (AckManager err) {
            return err.getAck();
        }
    }

    public boolean isFull(String nickname)
    {
        return this.game.isFull(nickname);
    }
    /**
     * basic production from player
     * @param res1 first spended resource
     * @param res2 second spended resource
     * @param obt  wanted resource
     * @param player client index
     */
    public Packet basicProduction(ResourceType res1,ResourceType res2, ResourceType obt, int player)
    {
        System.out.println(game.getCurrentPlayer().getNickname());
        Player p = this.game.getCurrentPlayer();
        Dashboard dashboard = p.getDashboard();

        //if(!isRightPlayer(player)) return this.notYourTurn();
        System.out.println("Res 1: "+ res1);
        System.out.println("Res 2: "+ res2);
        System.out.println("obt: "+ obt);

        try {

            dashboard.basicProduction(res1,res2,obt);
            return setPendingCost(dashboard);
        } catch (AckManager err) {
            System.out.println("\nerrore nella production\n");
            return err.getAck();
        }
    }

    /**
     * Try to login to the game
     * @param nickname player name
     * @return ack if of Nack if exception occurred
     */
    public Packet login(String nickname)
    {
        try {
            this.game.addPlayer(nickname);
            //System.out.println("Login di " + nickname);
            return new ACK(0);
        } catch (Exception e) {
            //System.out.println("Login di " +nickname + " FALLITO");
            return new ACK(4);
        }
    }

    /**
     * allow to insert resources into a specific player storage
     * @param resource resourtce to insert
     * @param pos      deposit to select
     * @param player   client index
     * @return         true
     */
    public Packet storageInsertion(Resource resource,int pos, int player)
    {
        if(!isRightPlayer(player)) return this.notYourTurn();

        Player p = this.game.getCurrentPlayer();

        try {
            p.storageInsertion(resource,pos);
        } catch (AckManager err) {
            DebugMessages.printError("Un errore di inserimento");
            return err.getAck();
        }
        return  null;
    }
    /**
     * Extract resource from the storage
     * @param resource resource to remove
     * @param pos      deposit selected
     * @param player   client index
     * @return Ack or Nack
     */
    public Packet storageExtraction(Resource resource, int pos, int player)
    {
        if(!isRightPlayer(player)) return this.notYourTurn();

        Player p = this.game.getCurrentPlayer();
        try {
            p.payStorageResource(resource,pos);
        } catch (AckManager err) {
            return err.getAck();
        }
        return  null;
    }
    /**
     * Extract resource from the chest
     * @param resource resource to remove
     * @param player   packet sender
     * @return Ack or Nack
     */
    public Packet chestExtraction(Resource resource, int player)
    {
        if(!isRightPlayer(player)) return this.notYourTurn();

        Player p = this.game.getPlayers().get(this.clients.get(player).getRealPlayerIndex());
        p.payChestResource(resource);
        return  null;
    }

    /**
     *
     * @param pos leader to activate/discard
     * @param player
     * @return
     */
    public Packet activateLeader(int pos,boolean action, int player)
    {
        if(!isRightPlayer(player)) return this.notYourTurn();

        Player p = this.game.getPlayer(this.clients.get(player).getRealPlayerIndex());
        try {
            if(action)
            {
                p.activateLeader(pos);
                Packet update = p.getLeaderCardUpdate(pos,this.clients.get(player).getRealPlayerIndex());
                this.broadcastMessage(-1,update);

                this.sendLeaderUpdate(player);
            }
            else
            {
                p.discardLeader(pos);
                this.broadcastMessage(-1,new UpdateLeaders(p.getLeaders(),this.clients.get(player).getRealPlayerIndex()));
                //TODO send leaderUpdate with discarded leader
            }

            return new ACK(0);
        } catch (AckManager err) {
            return err.getAck();
        }
    }

    /**
     * bonus production
     * @param pos   card to activate
     * @param obt   wanted res
     * @param player packet sender
     * @return ack or nack
     */
    public Packet bonusProduction(int pos,ResourceType obt,int player)
    {
        if(!isRightPlayer(player)) return this.notYourTurn();

        Player p = this.game.getCurrentPlayer();
        try {
            p.bonusProduction(pos,obt);
            return new ACK(0);
        } catch (AckManager err) {
            return err.getAck();
        }
    }

    /**
     * execute swap on a specific player storage
     * @param pos1    first deposit
     * @param pos2    destination deposit
     * @param player  client index
     * @return
     */
    public Packet swapDeposit(int pos1,int pos2,int player)
    {
        //if(!isRightPlayer(player)) return this.notYourTurn();

        //System.out.println("\n" +player +"\n");
        Player p = this.game.getPlayer(player);
        //System.out.println(player + "!!!!\n\n\n");



        //System.out.println("\n preswap d1: "+ p.getDashboard().getStorage().getStorage()[0].getResource().getType());
        //System.out.println(" preswap d2: "+ p.getDashboard().getStorage().getStorage()[1].getResource().getType());


        try {


            p.getDashboard().getStorage().swapDeposit(pos1 -1,pos2 -1);

            Deposit[] tmp = (p.getDashboard().getStorage().getDeposits());

            //System.out.println("\n postswap d1: "+ p.getDashboard().getStorage().getStorage()[0].getResource().getType());
            //System.out.println(" postswap d2: "+ p.getDashboard().getStorage().getStorage()[1].getResource().getType()+"\n");
            sendStorageUpdate(p.getControllerIndex());

            return null;

        } catch (AckManager err) {
            return err.getAck();
        }

    }

    /**
     * same as swap but allow to transfer a specific resource quantity from a deposit A to a deposit B
     * @param pos1   start deposit
     * @param pos2   dest deposit
     * @param q      quantity to move
     * @param player client index
     * @return
     */
    public Packet MoveResources(int pos1,int pos2,int q, int player)
    {
        Player p = this.game.getPlayer(this.clients.get(player).getRealPlayerIndex());
        try{
        p.getDashboard().getStorage().moveResource(pos1,pos2,q);
        Deposit[] tmp = (p.getDashboard().getStorage().getDeposits());
        sendStorageUpdate(p.getControllerIndex());

        return null;}

        catch (AckManager err) {
            return err.getAck();
        }

    }

    /**
     * called when a speecific user discard market resource
     * @param quantity  quantity discarded
     * @param index     client index
     * @return
     */
    public Packet discardResource(int quantity,int index)
    {
        this.game.discardResource(quantity);
        this.broadcastMessage(-1,new UpdatePosition(quantity,this.clients.get(index).getRealPlayerIndex()));
        return new ACK(0);
    }

    /**
     *
     * @param direction row or column (row = false,col = true)
     * @param pos    row/col position of the market
     * @param player packet sender
     * @return a packet containing the resources extracted and eventual "whiteballs" to ask the user
     */
    public Packet marketExtraction(boolean direction,int pos,int player)
    {
        if(!isRightPlayer(player)) return this.notYourTurn();

        Player p = this.game.getCurrentPlayer();
        Market m = this.game.getMarket();

        try
        {
            if(direction)
            {
                m.exstractColumn(pos,p);
            }
            else
            {
                m.exstractRow(pos,p);
            }
        }catch (AckManager err) {
            return err.getAck();
        }

        if(this.game.getMarket().getRedBallExtracted()){
            this.broadcastMessage(-1,new ExtreactedRedBall(1,clients.get(player).getRealPlayerIndex()));
        }

        List <Resource> res = m.getPendingResourceExtracted();
        int white           = m.getWhiteCount();
        this.broadcastMessage(-1,new UpdateMiniMarket(direction,pos));
        return  new MarketResult(res,white);

    }


    /**
     * send to clients the information a specific player updated his leaders
     * @param index client index
     */
    public void sendLeaderUpdate(int index)
    {
        LeaderCard[] leaderCards = this.game.getPlayer(this.clients.get(index).getRealPlayerIndex() ).getLeaders();
        this.broadcastMessage(-1,new UpdateLeaders(leaderCards,this.clients.get(index).getRealPlayerIndex()));
    }

    /**
     * send to clients the information a specific player updated his storage
     * @param index client index
     */
    public void sendStorageUpdate(int index)
    {
        Deposit[] tmp = this.game.getPlayer(this.clients.get(index).getRealPlayerIndex() ).getDashboard().getStorage().getDeposits();
        this.broadcastMessage(-1,new StorageUpdate(tmp,this.clients.get(index).getRealPlayerIndex()));
    }

    /**
     * send to clients the information a specific player updated his chest
     * @param index client index
     */
    public void sendChestUpdate(int index)
    {
        List<Resource>chest = this.game.getPlayer(this.clients.get(index).getRealPlayerIndex()).getDashboard().getChest();
        this.broadcastMessage(-1,new ChestUpdate(chest,this.clients.get(index).getRealPlayerIndex()));
    }
    /**
     * if end condition are true send to all a "last Turn" packet
     * if the current player is 4 and the match is ended then send an "end game" packet to all
     * if nor of prewious is true then send a message "typeTurn" to the next player
     * @return null
     */
    public Packet nextTurn(){
        Player player = game.nextTurn();
        DebugMessages.printError("PLAYER "+ this.game.getCurrentPlayerIndex() + "->controller:"+this.game.getCurrentPlayer().getControllerIndex());
        //se risulterà positivo invierà in broadcast EndTurn e chiudera la connessione in maniera safe
        if(game.checkEndGame()) lastTurn();
        if(game.IsEnded())
        {
            endGame();
            return null;
        }


        currentClient = player.getControllerIndex();
        //currentClient=this.game.getRealPlayerHandlerIndex();

        //DebugMessages.printGeneric("\n new currplayer: "+ currentClient + ", total players: "+this.clients.size()+"\n");


        turnNotifier();
        return null;
    }

    /**
     * Broadcast endgame packet
     */
    public void endGame()
    {
        this.broadcastMessage(-1, new EndGame());
    }

    /**
     * Broadcast lastTurn packet
     */
    public void lastTurn()
    {
        this.broadcastMessage(-1, new EndGame());
    }

    public void sendMessage(Packet p,int index)
    {
        this.clients.get(index).sendToClient(p);
        /*for(ClientHandler c: clients )
        {
            if(c.getRealPlayerIndex() == index) c.sendToClient(p);
        }*/
    }



}
