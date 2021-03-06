package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enumeration.CardType;
import it.polimi.ingsw.exceptions.AlreadyUsed;
import it.polimi.ingsw.exceptions.NotEnoughResource;
import it.polimi.ingsw.exceptions.WrongPosition;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.dashboard.Dashboard;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourceOperator;
import it.polimi.ingsw.utils.ConstantValues;

import java.io.Serializable;
import java.util.List;

public class ProductionCard extends Card {

    private int id;
    private CardType type;
    private int level;
    private int obtainedFaith;
    private List<Resource> rawMaterials;
    private List<Resource> obtainedMaterials;
    private boolean alreadyUsed;

    //Empty Constructor for GSON
    public ProductionCard(List<Resource> cost,List<Resource> raw,List<Resource>obt,int victoryPoints,int level,int obtainedFaith,CardType type)
    {
        super(cost,victoryPoints);
        this.level = level;
        this.rawMaterials =  raw;
        this.obtainedMaterials =  obt;
        this.type = type;
        this.obtainedFaith = obtainedFaith;
        this.alreadyUsed = false;
    }



    public ProductionCard(List<Resource> cost, int victoryPoints,int level) {
        super(cost, victoryPoints);
        this.level = level;
        this.alreadyUsed = false;
    }

    public ProductionCard(List<Resource> cost,List<Resource> raw,List<Resource>obt,int victoryPoints,int level)
    {
        super(cost,victoryPoints);
        this.level = level;
        this.rawMaterials =  raw;
        this.obtainedMaterials =  obt;
        this.alreadyUsed = false;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return this.id;
    }

    public int getLevel() {
        return level;
    }

    public void setUnused()
    {
        this.alreadyUsed = false;
    }
    public CardType getType() {
        return type;
    }

    /**
     *
     * @param card card to compare
     * @return true if type is equals
     */
    public boolean compareType(ProductionCard card)
    {
        return this.getType() == card.getType();
    }

    /**
     *
     * @param card type to compare
     * @return true if type is equals
     */
    public boolean compareType(PrerequisiteCard card)
    {
        boolean out = false;


        //If level is not negative check level
        if(card.getLevel() != -1)
        {
            if(card.getLevel() != this.level) return false;
        }

        //If level is ok or not necessary check Type
        return this.getType().equals(card.getType());

    }

    @Override
    public boolean checkCost(Dashboard dash) {

        List<Resource> availableRes = ResourceOperator.merge(dash.getDiscount(),dash.getAllAvailableResource());

        boolean out = ResourceOperator.compare(availableRes,this.getCost());

        return out;
    }


    /**
     * Player select A production card trough the view and this model method will be called to activate
     * THIS FUNCTION DOSNT REMOVE COST RESOURCE (it will be done by controller)
     * @param p Dashboard of the player
     * @return  true if the activation goes well
     */
    public boolean produce(Player p) throws NotEnoughResource, AlreadyUsed {

        if(alreadyUsed) throw new AlreadyUsed("");

        //Check if necesary resources are availabe
        List<Resource> resAvailable = p.getDashboard().getAllAvailableResource();
        //Remove from res available resources obtained during this turn
        resAvailable = ResourceOperator.listSubtraction(resAvailable,p.getDashboard().getTurnGain());

        boolean out = ResourceOperator.compare(resAvailable,this.rawMaterials);

        //if true add obtained resources to the chest
        if(out)
        {
            p.incrementPosition(this.obtainedFaith);
            p.chestInsertion(this.obtainedMaterials);
            p.getDashboard().setGain(this.obtainedMaterials);
            this.alreadyUsed = true;
        }
        else
        {
            throw  new NotEnoughResource("");
        }


        return out;
    }


    /**
     *
     * @param p the player that buys the card
     * @param pos positioning of the card inside the dashboard
     * @return true if all goes in the right way
     */
    public void buy(Player p, int pos) throws WrongPosition, NotEnoughResource {
        //First Buy the card then ask player where chose resource in the controller
        boolean out = this.checkCost(p.getDashboard());

        if(out)
        {
            try
            {
                out = p.getDashboard().setProductionCard(this,pos);
                if (out)
                {
                    p.increaseScore(this.getVictoryPoints());
                }


            } catch (WrongPosition wrongPosition) {
                throw new WrongPosition("");
            }


        }else
        {
            throw new NotEnoughResource("");
        }

    }

    /**
     * get the card price with the applied discount
     * @param dash the player dashboard
     * @return the cost of the card discounted(full price instead)
     */

    public List<Resource> getCost(Dashboard dash)
    {
        List<Resource> cost = super.getCost();

        if(dash.getDiscount() != null)
        {
            for(Resource scont : dash.getDiscount())
            {
                cost.remove(scont);
            }
        }
        return cost;
    }

    public List<Resource> getRawMaterials()
    {
        return this.rawMaterials;
    }
    public List<Resource> getObtainedMaterials(){return this.obtainedMaterials;}
    public int getObtainedFaith()
    {
        return this.obtainedFaith;
    }

    public String getColor()
    {
        return ConstantValues.resourceRappresentation.getCardTypeColorRappresentation(this.type);
    }

    //printHeader con gia isWindow integrata

}
