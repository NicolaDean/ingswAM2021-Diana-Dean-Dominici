package it.polimi.ingsw.exceptions;

import it.polimi.ingsw.enumeration.ErrorMessages;

/**
 * Match is full
 */
public class MatchFull extends AckManager{
    public MatchFull(String message) {
        super("Match already full" + message);
        this.setErrorCode(ErrorMessages.MatchFull);
    }
}
