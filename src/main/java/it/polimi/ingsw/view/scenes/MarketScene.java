package it.polimi.ingsw.view.scenes;

import it.polimi.ingsw.utils.ConstantValues;
import it.polimi.ingsw.view.GuiHelper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class MarketScene extends BasicSceneUpdater{


    private ImageView balls;
    private ImageView discartedBall;

    @FXML
    AnchorPane pane;

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void updateMarket() {

    }
    @FXML
    public void exstractionCol(ActionEvent event){
        int pos =Integer.parseInt((String) ((Node)event.getSource()).getUserData());
        System.out.println("estratto in posizione "+pos);
        //this.notifyObserver(ClientController::exstractCol(pos));
        //updateMarket();
    }
}