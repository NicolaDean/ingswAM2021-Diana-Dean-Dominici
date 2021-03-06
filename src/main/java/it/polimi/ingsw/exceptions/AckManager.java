package it.polimi.ingsw.exceptions;

import it.polimi.ingsw.controller.packets.ACK;
import it.polimi.ingsw.controller.packets.Packet;
import it.polimi.ingsw.enumeration.ErrorMessages;

/**
 * Allow to use a single exception class to handle and generate all possible Model/server errors
 */
public class AckManager extends Exception{

    int errorCode;
    public AckManager(String message)
    {
        super(message);
    }

    public ACK getAck()
    {
        return new ACK(errorCode);
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    public void setErrorCode(ErrorMessages errorCode) {
        this.errorCode = errorCode.ordinal();
    }
}
