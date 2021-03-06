package it.polimi.ingsw.view.scenes;

import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.ProductionCard;
import it.polimi.ingsw.model.dashboard.Deposit;
import it.polimi.ingsw.model.minimodel.MiniPlayer;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.utils.DebugMessages;
import it.polimi.ingsw.view.scenes.BasicSceneUpdater;
import javafx.application.Platform;
import javafx.scene.layout.Background;

import java.util.List;

/**
 * set a different "userIndex" inside dashboard so you see others player data
 */
public class SpyScene extends DashboardScene {

    private String nickname = "0";

    public SpyScene(int spyIndex,String nickname)
    {
        this.nickname = nickname;
        this.setIndex(spyIndex);
    }

    @Override
    public void init() {
        //root.getStylesheets().add("/css/Spy.css");
        isaspy=true;
        disableCardClick();
        super.init();
        disableSwap();
        DebugMessages.printError("Spying Player " + nickname + " -> " + getIndex());
    }

}
