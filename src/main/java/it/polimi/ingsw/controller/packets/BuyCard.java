package it.polimi.ingsw.controller.packets;

import it.polimi.ingsw.controller.ServerController;
import it.polimi.ingsw.controller.packets.Packet;
import it.polimi.ingsw.controller.packets.PacketManager;
import it.polimi.ingsw.exceptions.AckManager;

/**
 * packet that asks to perform a buy action
 */
public class BuyCard  extends Packet<ServerController> implements PacketManager<ServerController> {

    int x;
    int y;
    int position;

    public BuyCard(int x,int y,int pos)
    {
        super("BuyCard");
        this.x =x;
        this.y =y;
        this.position = pos;
        //this.playerIndex = playerIndex;
    }
    @Override
    public Packet analyze(ServerController controller)
    {
        Packet p = controller.buyCard(this.x,this.y,this.position,this.getClientIndex());



        if(p.getType().equals("ACK"))
        {
            controller.sendMessage(new BuyFailed(),this.getClientIndex());
            return p;
        }
       return p;
    }
}