package it.polimi.ingsw.exceptions;

import it.polimi.ingsw.enumeration.ErrorMessages;

public class NotSoddisfedPrerequisite extends AckManager{

    public NotSoddisfedPrerequisite(String message)
    {
       super("NotSoddisfedPrerequisite: " + message);
       this.setErrorCode(ErrorMessages.NotSoddisfedPrerequisite);
    }
}
