package it.polimi.ingsw.view.scenes;

import it.polimi.ingsw.controller.ClientController;
import it.polimi.ingsw.view.GUI;
import it.polimi.ingsw.view.GuiHelper;
import it.polimi.ingsw.view.scenes.BasicSceneUpdater;
import it.polimi.ingsw.view.utils.FXMLpaths;

import java.io.IOException;

public class HomeScene  extends BasicSceneUpdater {


    public HomeScene()
    {

    }

    @Override
    public void init() {
        super.init();
        //GuiHelper.resize(800,600);
    }

    public void singlePlayerCall()
    {
        GuiHelper.getGui().setSingleplayer();
        GuiHelper.getGui().askServerData();
    }

    public void multiplayerCall()
    {
        GuiHelper.getGui().askServerData();
    }

    public void reconnectCall()
    {

    }
}
