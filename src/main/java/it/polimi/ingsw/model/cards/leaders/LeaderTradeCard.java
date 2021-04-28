package it.polimi.ingsw.model.cards.leaders;

import it.polimi.ingsw.enumeration.ResourceType;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.PrerequisiteCard;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourceOperator;

import java.util.List;

public class LeaderTradeCard extends LeaderCard implements BonusProductionInterface {

    private ResourceType obtain;

    public LeaderTradeCard(List<Resource> cost, List<PrerequisiteCard> cardPrequisite, int victoryPoints, ResourceType type) {
        super(cost,cardPrequisite, victoryPoints, type);

        this.obtain = null;
    }


    @Override
    public void activate(Player p) throws Exception {
        super.activate(p);
        p.addTradeBonus(this);
    }

    //USER can select the card and call the method "changeRawMat()" or
    @Override
    public void produce(Player p, ResourceType obtain) throws Exception {
        int possession = ResourceOperator.extractQuantityOf(this.getType(),p.getDashboard().getAllAvailableResource());

        if(possession >= 1)
        {
            p.chestInsertion(new Resource(obtain,1));
            p.incrementPosition(); //Get a faith point
        }
        else
        {
            throw new Exception("not enough money");
        }

    }

    @Override
    public Resource getProdCost()
    {
        return new Resource(this.getType(),1);
    }


}
