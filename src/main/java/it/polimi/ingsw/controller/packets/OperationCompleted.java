package it.polimi.ingsw.controller.packets;

import it.polimi.ingsw.controller.ClientController;

/**
 * packet that informs that the operation has been completed
 */

public class OperationCompleted  extends Packet<ClientController> implements PacketManager<ClientController> {
    public OperationCompleted() {
        super("OperationCompleted");
    }

    @Override
    public Packet analyze(ClientController controller) {
        controller.askEndTurn();

        return null;
    }
}
