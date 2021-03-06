package it.polimi.ingsw.modelTest;

import static it.polimi.ingsw.enumeration.CardType.YELLOW;
import static it.polimi.ingsw.enumeration.ResourceType.*;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.PrerequisiteCard;
import it.polimi.ingsw.model.cards.ProductionCard;
import it.polimi.ingsw.model.cards.leaders.LeaderTradeCard;
import it.polimi.ingsw.model.dashboard.Dashboard;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourceList;
import it.polimi.ingsw.model.resources.ResourceOperator;


import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LeadersTest {
    /**
     * check if discount leader activation work well (add to player discount list)
     */
    @Test
    public void discountBonusTest()
    {
        Dashboard dash = new Dashboard();

        dash.setDiscount(new Resource(COIN,1));
        dash.setDiscount(new Resource(ROCK,1));
        dash.setDiscount(new Resource(SERVANT,1));

        List<Resource> cost = new ResourceList();

        cost.add(new Resource(COIN,1));
        cost.add(new Resource(ROCK,2));

        ProductionCard card = new ProductionCard(cost,cost,cost,1,1);

        List<Resource> result = card.getCost(dash);//get cost with the discount if exist

        assertEquals(ResourceOperator.extractQuantityOf(COIN,result),0);
        assertEquals(ResourceOperator.extractQuantityOf(ROCK,result),1);
        assertEquals(ResourceOperator.extractQuantityOf(SERVANT,result),0);
        assertEquals(ResourceOperator.extractQuantityOf(SHIELD,result),0);

    }

    /**
     * check if trade bonus leader work well (if add correctly bonusProduction interfaces to player)
     */
    @Test
    public void tradeBonusTest()
    {
        List<Resource> cost = new ResourceList();

        cost.add(new Resource(COIN,1));

        LeaderCard l[] = {new LeaderTradeCard(cost,new ArrayList<>(),1,COIN), new LeaderCard(cost,new ArrayList<>(),1, COIN) };
        Player p=new Player("nick");
        p.setLeaders(l);

        p.chestInsertion(new Resource(COIN,1));
        //LEADER ACTIVATION
        //Lv3 on lv 3
        Assertions.assertDoesNotThrow(()-> {
            p.activateLeader(0);
        });

        //TRADE BONUS TEST
        Assertions.assertDoesNotThrow(()-> {
            p.bonusProduction(0,ROCK);
        });

        //PAY ACTIVATION
        p.payChestResource(new Resource(COIN,1));

        //CHECK POS
        assertEquals(p.getPosition(),1);

        //CHECK ADDED/REMOVED RESOURCE
        assertEquals(ResourceOperator.extractQuantityOf(COIN,p.getDashboard().getAllAvailableResource()),0);
        assertEquals(ResourceOperator.extractQuantityOf(SERVANT,p.getDashboard().getAllAvailableResource()),0);
        assertEquals(ResourceOperator.extractQuantityOf(SHIELD,p.getDashboard().getAllAvailableResource()),0);
        assertEquals(ResourceOperator.extractQuantityOf(ROCK,p.getDashboard().getAllAvailableResource()),1);



    }

    /**
     * check if leader prerequisite check work well (if user have rigth prerequisite to activate card)
     * @throws Exception model exception
     */
    @Test
    public void checkPrerequisite() throws Exception {
        Game g = new Game();
        LeaderCard [] leaders = new LeaderCard[1];
        List<Resource> cost = new ResourceList();

        cost.add(new Resource(COIN,1));

        List<PrerequisiteCard> cards = new ArrayList<>();
        cards.add(new PrerequisiteCard(YELLOW,1,1));

        LeaderCard c = new LeaderCard(cost,cards,1,COIN);

        Player p = new Player("nicola");
        ProductionCard card = new ProductionCard(new ResourceList(),new ResourceList(),new ResourceList(),1,1,1,YELLOW);
        p.chestInsertion(new Resource(COIN,2));
        card.buy(p,1);


        Assertions.assertDoesNotThrow(()-> {
            c.activate(p);
        });

    }

    /**
     * try loading and adding leaders card to minimodel
     */
    @Test
    public void testMinimodel()
    {
        Game g = new Game();

        LeaderCard[] leaderCards = g.get4leaders();

        Player p = new Player();

        p.setLeaders(leaderCards);
    }


}
