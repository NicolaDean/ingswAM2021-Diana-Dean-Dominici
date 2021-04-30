package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.packets.*;
import it.polimi.ingsw.controller.packets.ACK;
import it.polimi.ingsw.controller.packets.MarketResult;
import it.polimi.ingsw.controller.packets.PendingCost;
import it.polimi.ingsw.enumeration.ResourceType;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.ProductionCard;
import it.polimi.ingsw.model.dashboard.Dashboard;
import it.polimi.ingsw.model.market.Market;
import it.polimi.ingsw.model.resources.Resource;

import java.util.List;

public class ServerController{

    //view
    Game game;


    public ServerController()
    {
        game = new Game();
    }

    public Game getGame() {
        return game;
    }


    /**
     * Check if the player that send the command is the current player
     * @param playerIndex index of the packet sender
     * @return true if is the current player
     */
    public boolean isRightPlayer(int playerIndex)
    {
        return (this.game.getCurrentPlayerIndex() == playerIndex);
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
     * Player "player" buy the card in position x,y of the deks
     * @param x level
     * @param y color
     * @param pos where i want to put the card
     * @param player packet sender index
     */
    public Packet buyCard(int x,int y,int pos,int player){
        Player p = this.game.getCurrentPlayer();

        if(!isRightPlayer(player)) return new ACK(1);

        ProductionCard card = this.game.drawProductionCard(x,y);
        try
        {
            card.buy(p,pos);
            return setPendingCost(p.getDashboard());
        } catch (Exception e) {
            return  new ACK(1);
        }

    }

    /**
     * production of a player
     * @param pos which card i wanna use
     * @param player player who wand to do an action
     */
    public Packet production(int pos,int player)
    {
        Player p = this.game.getCurrentPlayer();
        Dashboard dashboard = p.getDashboard();

        if(!isRightPlayer(player)) return new ACK(1);

        try {
            dashboard.production(p,pos);
            return setPendingCost(dashboard);
        } catch (Exception e) {
            return new ACK(1);
        }
    }

    /**
     * basic production from player
     * @param res1 first spended resource
     * @param res2 second spended resource
     * @param obt  wanted resource
     * @param player packet sender
     */
    public Packet basicProduction(ResourceType res1,ResourceType res2, ResourceType obt, int player)
    {
        Player p = this.game.getCurrentPlayer();
        Dashboard dashboard = p.getDashboard();

        if(!isRightPlayer(player)) return new ACK(1);

        try {
            dashboard.basicProduction(res1,res2,obt);
            return setPendingCost(dashboard);
        } catch (Exception e) {
            return new ACK(1);
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
            System.out.println("Login di " + nickname);
            return new ACK(0);
        } catch (Exception e) {
            System.out.println("Login di " +nickname + " FALLITO");
            return new ACK(1);
        }
    }

    /**
     * Extract resource from the storage
     * @param resource resource to remove
     * @param pos      deposit selected
     * @param player   packet sender
     * @return Ack or Nack
     */
    public Packet storageExtraction(Resource resource, int pos, int player)
    {
        if(!isRightPlayer(player)) return new ACK(1);

        Player p = this.game.getCurrentPlayer();
        p.payStorageResource(resource,pos);
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
        if(!isRightPlayer(player)) return new ACK(1);

        Player p = this.game.getCurrentPlayer();
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
        if(!isRightPlayer(player)) return new ACK(1);

        Player p = this.game.getCurrentPlayer();
        try {
            if(action)
            {
                p.activateLeader(pos);
            }
            else
            {
                p.discardLeader(pos);
            }

            return new ACK(0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ACK(1);
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
        if(!isRightPlayer(player)) return new ACK(1);

        Player p = this.game.getCurrentPlayer();
        try {
            p.bonusProduction(pos,obt);
            return new ACK(0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ACK(1);
        }
    }

    public Packet swapDeposit(int pos1,int pos2,int player)
    {
        if(!isRightPlayer(player)) return new ACK(1);

        Player p = this.game.getCurrentPlayer();
        try {
            p.getDashboard().getStorage().swapDeposit(pos1,pos2);
            return new ACK(0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ACK(1);
        }
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
        if(!isRightPlayer(player)) return new ACK(1);

        Player p = this.game.getCurrentPlayer();
        Market m = this.game.getMarket();

        if(direction)
        {
            m.exstractColumn(pos,p);
        }
        else
        {
            m.exstractRow(pos,p);
        }

        List <Resource> res = m.getPendingResourceExtracted();
        int white           = m.getWhiteCount();

        return  new MarketResult(res,white);
    }


}
