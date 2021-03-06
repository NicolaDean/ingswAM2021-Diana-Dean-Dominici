package it.polimi.ingsw.modelTest;
import it.polimi.ingsw.exceptions.EmptyDeposit;
import it.polimi.ingsw.exceptions.FullDepositException;
import it.polimi.ingsw.exceptions.NoBonusDepositOwned;
import it.polimi.ingsw.exceptions.WrongPosition;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.ProductionCard;
import it.polimi.ingsw.model.dashboard.Dashboard;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourceList;
import it.polimi.ingsw.model.resources.ResourceOperator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static it.polimi.ingsw.enumeration.ResourceType.*;
import static org.junit.jupiter.api.Assertions.*;

public class ProductionTest {

    /**
     * check if user can buy a production card
     * @throws FullDepositException model exception for deposit insertion
     * @throws NoBonusDepositOwned  model exception for deposit exctraction
     * @throws WrongPosition        model exception for not existing deposit
     */
    @Test
    public void CheckCost() throws FullDepositException, NoBonusDepositOwned, WrongPosition {
        Player p = new Player();

        p.getDashboard().chestInsertion(new Resource(COIN,1));
        p.getDashboard().chestInsertion(new Resource(ROCK,1));

        List<Resource> tmp = new ResourceList();

        tmp.add(new Resource(COIN,1));
        tmp.add(new Resource(ROCK,1));
        tmp.add(new Resource(SHIELD,1));

        ProductionCard card = new ProductionCard(tmp,2,1);

        assertFalse(card.checkCost(p.getDashboard()));

        p.getDashboard().storageInsertion(new Resource(SHIELD,1),0);
        assertTrue(card.checkCost(p.getDashboard()));
    }

    /**
     * try to buy a card from shop, test user dosnt have res and user have res case
     * @throws FullDepositException model exception for deposit insertion
     * @throws NoBonusDepositOwned  model exception for deposit exctraction
     * @throws WrongPosition        model exception for not existing deposit
     */
    @Test
    public void BuyTest() throws FullDepositException, NoBonusDepositOwned, WrongPosition {
        Player p = new Player();


        p.getDashboard().chestInsertion(new Resource(COIN,2));
        p.getDashboard().chestInsertion(new Resource(ROCK,1));


        List<Resource> tmp = new ResourceList();

        tmp.add(new Resource(COIN,1));
        tmp.add(new Resource(ROCK,1));
        tmp.add(new Resource(SHIELD,1));

        ProductionCard card = new ProductionCard(tmp,2,1);

        Assertions.assertThrows(Exception.class,()-> {
            card.buy(p,0);
        });

        p.getDashboard().storageInsertion(new Resource(SHIELD,1),0);

        Assertions.assertDoesNotThrow(()-> {
            card.buy(p,0);
        });
    }

    /**
     * try doing some varius operation with production (produce with wrong cards, dont have res,have res,,,)
     * @throws FullDepositException model exception for deposit insertion
     * @throws NoBonusDepositOwned  model exception for deposit exctraction
     * @throws WrongPosition        model exception for not existing deposit
     * @throws EmptyDeposit         model exception for not existing deposit
     */
    @Test
    public void ProductionTest() throws FullDepositException, NoBonusDepositOwned, WrongPosition, EmptyDeposit {
        Player p = new Player();

        p.getDashboard().chestInsertion(new Resource(COIN,1));
        p.getDashboard().chestInsertion(new Resource(ROCK,3));
        p.getDashboard().storageInsertion(new Resource(SHIELD,1),0);

        List<Resource> check = new ResourceList();
        check =  p.getDashboard().getAllAvailableResource();

        //Check RESOURCE INSERTION
        assertTrue(ResourceOperator.extractQuantityOf(ROCK,check) == 3);
        assertTrue(ResourceOperator.extractQuantityOf(COIN,check) == 1);
        assertTrue(ResourceOperator.extractQuantityOf(SHIELD,check) == 1);
        assertTrue(ResourceOperator.extractQuantityOf(SERVANT,check) == 0);

        //COST
        List<Resource> cost = new ResourceList();
        cost.add(new Resource(COIN,1));
        cost.add(new Resource(ROCK,1));
        cost.add(new Resource(SHIELD,1));

        //RAW MAT
        List<Resource> raw = new ResourceList();
        raw.add(new Resource(COIN,2));

        //OBTAINED
        List<Resource> obt = new ResourceList();
        obt.add(new Resource(ROCK,1));

        //BUY A CARD
        ProductionCard card = new ProductionCard(cost,raw,obt,2,1);
        Assertions.assertDoesNotThrow(()-> {
            card.buy(p,0);
        });

        //Apllying costs
        p.getDashboard().applyChestCosts(new Resource(COIN,1));
        p.getDashboard().applyChestCosts(new Resource(ROCK,1));
        p.getDashboard().applyStorageCosts(new Resource(SHIELD,1),0);


        check  =  p.getDashboard().getAllAvailableResource();
        //Check Application of costs
        assertTrue(ResourceOperator.extractQuantityOf(ROCK,check) == 2);
        assertTrue(ResourceOperator.extractQuantityOf(COIN,check) == 0);
        assertTrue(ResourceOperator.extractQuantityOf(SHIELD,check) == 0);
        assertTrue(ResourceOperator.extractQuantityOf(SERVANT,check) == 0);

        //Sorage Refill

        p.getDashboard().storageInsertion(new Resource(COIN,2),1);

        //Production
        try {
            p.getDashboard().production(p,0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Cost Application

        p.getDashboard().applyStorageCosts(new Resource(COIN,2),1);

        //Check Application of costs
        check  =  p.getDashboard().getAllAvailableResource();


        //Check Cost apply and resource adding
        assertTrue(ResourceOperator.extractQuantityOf(ROCK,check) == 3);
        assertTrue(ResourceOperator.extractQuantityOf(COIN,check) == 0);
        assertTrue(ResourceOperator.extractQuantityOf(SHIELD,check) == 0);
        assertTrue(ResourceOperator.extractQuantityOf(SERVANT,check) == 0);


    }

    @Test

    /**
     * try buy a card with discount leader activated (check if discount work)
     */
    public void discountedCostTest()
    {

        //COST
        List<Resource> cost = new ResourceList();
        cost.add(new Resource(COIN,1));
        cost.add(new Resource(ROCK,1));
        cost.add(new Resource(SHIELD,1));

        //RAW MAT
        List<Resource> raw = new ResourceList();
        raw.add(new Resource(COIN,2));

        //OBTAINED
        List<Resource> obt = new ResourceList();
        obt.add(new Resource(ROCK,1));

        //BUY A CARD
        ProductionCard card = new ProductionCard(cost,raw,obt,2,1);

        Player p = new Player();

        p.addDiscount(new Resource(COIN,1));

        List<Resource> resultCost = card.getCost(p.getDashboard());

        assertTrue(ResourceOperator.extractQuantityOf(ROCK,resultCost) == 1);
        assertTrue(ResourceOperator.extractQuantityOf(COIN,resultCost) == 0);
        assertTrue(ResourceOperator.extractQuantityOf(SHIELD,resultCost) == 1);
        assertTrue(ResourceOperator.extractQuantityOf(SERVANT,resultCost) == 0);

    }
}
