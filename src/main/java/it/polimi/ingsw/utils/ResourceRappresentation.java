package it.polimi.ingsw.utils;

import it.polimi.ingsw.enumeration.CardType;
import it.polimi.ingsw.enumeration.ResourceType;
import it.polimi.ingsw.view.utils.CliColors;

import java.util.HashMap;

import static it.polimi.ingsw.enumeration.ResourceType.*;


/**
 * CLI resources rappresentation
 */
public class ResourceRappresentation {

    private final HashMap<ResourceType,String> colorRappresentation             = new HashMap<ResourceType,String>();
    private final HashMap<ResourceType,String> noColorRappresentation           = new HashMap<ResourceType,String>();
    private final HashMap<CardType,String>     cardTypeColorRappresentation     = new HashMap<CardType,String>();
    private final HashMap<CardType,String>     cardTypeNonColorRappresentation = new HashMap<CardType,String>();

    public ResourceRappresentation()
    {
        colorRappresentation.put(COIN   , CliColors.YELLOW_BACKGROUND);
        colorRappresentation.put(SERVANT, CliColors.MAGENTA_BACKGROUND);
        colorRappresentation.put(SHIELD , CliColors.BLUE_BACKGROUND);
        colorRappresentation.put(ROCK   , CliColors.WHITE_BACKGROUND);


        noColorRappresentation.put(COIN   , "-X");
        noColorRappresentation.put(SERVANT, "-Y");
        noColorRappresentation.put(SHIELD , "-Z");
        noColorRappresentation.put(ROCK   , "-F");

        cardTypeColorRappresentation.put(CardType.YELLOW    , CliColors.YELLOW_BACKGROUND);
        cardTypeColorRappresentation.put(CardType.BLUE      , CliColors.BLUE_BACKGROUND);
        cardTypeColorRappresentation.put(CardType.PURPLE    , CliColors.MAGENTA_BACKGROUND);
        cardTypeColorRappresentation.put(CardType.GREEN     , CliColors.GREEN_BACKGROUND);
    }

    /**
     *
     * @param resourceType
     * @return resource reappresentation inside CLI
     */
    public String getColorRappresentation(ResourceType resourceType)
    {
        return this.colorRappresentation.get(resourceType);
    }

    //TODO
    public String getNonColorRappresentation(ResourceType resourceType)
    {
        return this.noColorRappresentation.get(resourceType);
    }

    /**
     *
     * @param type
     * @return Card rappresentation inside cli
     */
    public String getCardTypeColorRappresentation(CardType type)
    {
        return this.cardTypeColorRappresentation.get(type);
    }
}
