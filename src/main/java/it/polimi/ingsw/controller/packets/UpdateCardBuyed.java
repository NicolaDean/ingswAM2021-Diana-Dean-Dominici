package it.polimi.ingsw.controller.packets;

import it.polimi.ingsw.controller.ClientController;
import it.polimi.ingsw.model.cards.ProductionCard;

public class UpdateCardBuyed  extends Packet<ClientController> implements PacketManager<ClientController>{

    ProductionCard newCard;
    int x;
    int y;
    int dashboardPos;

    public UpdateCardBuyed(ProductionCard newCard,int x,int y,int dashboardPos) {
        super("UpdateCardBuyed");
        this.newCard = newCard;
        this.x = x;
        this.y = y;
        this.dashboardPos= dashboardPos;
    }

    @Override
    public Packet analyze(ClientController controller)
    {
        controller.updateCardBuyed(this.newCard,this.x,this.y,this.dashboardPos);
        return null;
    }
}