package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.ClientController;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.ProductionCard;
import it.polimi.ingsw.model.market.balls.BasicBall;
import it.polimi.ingsw.model.dashboard.Deposit;
import it.polimi.ingsw.model.minimodel.MiniPlayer;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.view.observer.Observable;

import java.util.List;

public class GUI extends Observable<ClientController> implements View{


    @Override
    public void printWelcomeScreen() {

    }

    @Override
    public void showPapalCell(MiniPlayer[] p) {

    }

    @Override
    public void setMiniMarketDiscardedResouce(BasicBall miniMarketBall){

    }

    @Override
    public BasicBall[][] getMiniMarketBalls() {
        return null;
    }

    @Override
    public BasicBall getMiniMarketDiscardedResouce() {
        return null;
    }

    @Override
    public void showMarket(){

    }

    @Override
    public void setMarket(BasicBall[][] balls, BasicBall discarted) {

    }

    @Override
    public void showError() {

    }

    @Override
    public void askNickname() {

    }

    @Override
    public void askServerData() {

    }

    @Override
    public void askServerData(String errore) {

    }

    @Override
    public void askBuy() {

    }

    @Override
    public void askProduction() {

    }

    @Override
    public void askBonusProduction() {

    }

    @Override
    public void askBasicProduction() {

    }

    @Override
    public void askMarketExtraction() {

    }

    @Override
    public void showDecks(ProductionCard[][] ProductionCards) {

    }

    @Override
    public Resource askDiscardResource(Resource resource) {
        return null;
    }

    @Override
    public void askResourceInsertion(List<Resource> resourceList) {

    }

    @Override
    public void askResourceExtraction(List<Resource> resourceList) {

    }

    @Override
    public void askSwapDeposit(int index) {

    }

    @Override
    public void askTurnType() {

    }

    @Override
    public void showPlayer(Deposit[]deposits, List<Resource> chest, ProductionCard[] cards,LeaderCard[] leaderCards,String name) {

    }

    @Override
    public void askCommand() {

    }

    @Override
    public void askLeaders(LeaderCard[] cards) {

    }

    @Override
    public void askLeaderActivation() {

    }

    @Override
    public void askDiscardLeader() {

    }

    @Override
    public void askInitialResoruce(int number) {

    }

    @Override
    public void showGameStarted() {

    }

    @Override
    public void abortHelp() {

    }


    @Override
    public void showMarketExtraction(List<Resource> resourceList, int whiteballs) {
        
    }

    @Override
    public void showStorage(Deposit[] deposits,List<Resource>chest) {

    }

    @Override
    public void showDashboard(Deposit[] deposits, List<Resource> chest, ProductionCard[] cards,LeaderCard[] leaderCards) {

    }

    @Override
    public void askEndTurn() {

    }

    @Override
    public void playerLogged(String nickname) {

    }
}
