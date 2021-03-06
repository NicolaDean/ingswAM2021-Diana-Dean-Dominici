package it.polimi.ingsw.controller.packets;

import it.polimi.ingsw.controller.ClientController;


/**
 * packet that notifies the client that the buy failed
 */
public class BuyFailed extends  Packet<ClientController> implements PacketManager<ClientController> {

        public BuyFailed()
        {
            super("BuyFailed");
        }

        @Override
        public Packet analyze(ClientController controller) {
            controller.askBuy();
        return null;
        }
}