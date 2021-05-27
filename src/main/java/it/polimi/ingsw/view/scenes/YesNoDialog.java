package it.polimi.ingsw.view.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class YesNoDialog extends BasicDialog{


    @FXML
    public Label question;

    private String msg;

    public YesNoDialog(String question)
    {
        this.msg = question;
    }

    @Override
    public void init() {
        super.init();

        question.setText(msg);
    }
}
