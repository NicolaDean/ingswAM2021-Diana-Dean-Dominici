package it.polimi.ingsw.controller.packets;

import it.polimi.ingsw.controller.ServerController;

/**
 * the standard packet class, the father of all packets
 * @param <T>
 */
public abstract class   Packet<T> implements PacketManager<T>
{

    private transient String type;
    private transient int       playerIndex;
    private transient boolean   isBroadcast;

    public Packet(String type) {
        this.type = type;
    }

    public void setBroadcast(boolean broadcast) {
        isBroadcast = broadcast;
    }

    public  Packet(String type, int playerIndex)
    {
        this.type = type;
        this.playerIndex = playerIndex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Packet analyze(T controller) throws InterruptedException {
        return null;
    }

    public String generateJson()
    {
        BasicPacketFactory packet = new BasicPacketFactory(this.getType(), this);

        return packet.toJson();
    }

    public int getClientIndex() {
        return playerIndex;
    }

    public void setPlayerIndex(int playerIndex)
    {
        this.playerIndex = playerIndex;
    }
}
