package it.polimi.ingsw.model.lorenzo.token;

import it.polimi.ingsw.controller.packets.LorenzoPositionUpdate;
import it.polimi.ingsw.enumeration.CardType;
import it.polimi.ingsw.model.lorenzo.LorenzoGame;
import it.polimi.ingsw.view.utils.CliColors;

public class SpecialBlackCrossToken extends BlackCrossToken{
    int bonus=1;

    @Override
    public void activateToken(LorenzoGame l) {
        l.getLorenzo().incrementPosition(bonus);
        this.notifyObserver(c->c.broadcastMessage(-1,new LorenzoPositionUpdate(l.getLorenzo().getPosition())));
    }

    public boolean isSpecial()
    {
        return true;
    }
    @Override
    public CardType getType() {
        return null;
    }

    @Override
    public String getColor() {
        return CliColors.WHITE_BACKGROUND+"2" +CliColors.BLACK_TEXT;
    }
}
