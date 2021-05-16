package it.polimi.ingsw.model.market;

import it.polimi.ingsw.enumeration.ResourceType;
import it.polimi.ingsw.exceptions.WrongPosition;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.market.balls.*;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourceList;
import it.polimi.ingsw.utils.ConstantValues;

import java.awt.*;
import java.util.List;

import static it.polimi.ingsw.enumeration.ResourceType.*;
import static it.polimi.ingsw.utils.ConstantValues.*;

public class Market {
    private int whiteCount=0;
    private List<Resource> pendingResourceExtracted = new ResourceList();
    private BasicBall discardedResouce;
    private BasicBall resouces[][] = { { new WhiteBall(), new WhiteBall() , new WhiteBall() ,new WhiteBall() } ,
                               { new ResourceBall(Color.blue,SHIELD), new ResourceBall(Color.blue,SHIELD) , new ResourceBall(Color.gray,ROCK) ,new ResourceBall(Color.gray,ROCK) } ,
                               { new ResourceBall(Color.yellow,COIN), new ResourceBall(Color.yellow,COIN) , new ResourceBall(Color.magenta,SERVANT) ,new ResourceBall(Color.magenta,SERVANT) } };

    /**
     * build e shuffle balls
     */
    public Market() {
        int     nw=numWhiteBall,
                nb=numBlueBall,
                ng=numGrayBall,
                ny=numYellowBall,
                nv=numVioletBall;
        discardedResouce = new RedBall();
        for(int i=0;i<marketRow;i++)
            for(int j=0;j<marketCol;j++){
                if(nw>0){
                    nw--;
                    resouces[i][j]=new WhiteBall();
                }else if(nb>0){
                    nb--;
                    resouces[i][j]= new ResourceBall(Color.blue,SHIELD);
                }else if(ng>0){
                    ng--;
                    resouces[i][j]= new ResourceBall(Color.gray,ROCK);
                }else if(ny>0){
                    ny--;
                    resouces[i][j]= new ResourceBall(Color.yellow,COIN);
                }else if(nv>0){
                    nv--;
                    resouces[i][j]= new ResourceBall(Color.magenta,SERVANT);
                }

            }

        randomized();
    }

    /**
     * get resource
     * @return balls
     */
    public BasicBall[][] getResouces() {
        return resouces;
    }

    /**
     * get discarded ball
     * @return discarded ball
     */
    public BasicBall getDiscardedResouce() {
        return discardedResouce;
    }

    /**
     * shuffle balls 300 times
     */
    private void randomized(){
        BasicBall Tmp;
        int r,c,n=2;

        for(int p=0;p<n;p++) {
            for (int i = 0; i < marketRow; i++) {
                for(int j = 0; j < ConstantValues.marketCol; j++){
                    r=(int)(Math.random()*10)%3;
                    c=(int)(Math.random()*10)%4;
                    Tmp = resouces[i][j];
                    resouces[i][j] = resouces[r][c];
                    resouces[r][c] = Tmp;

                    if((int)(Math.random()*10)<4){
                        r=(int)(Math.random()*10)%3;
                        c=(int)(Math.random()*10)%4;
                        Tmp = resouces[r][c];
                        resouces[r][c] = discardedResouce;
                        discardedResouce = Tmp;
                    }
                }
            }

        }
    }

    /**
     *extraction row
     * @param pos row position, it must be between 1 and 3
     * @param p player
     */
    public void exstractRow(int pos, Player p) throws WrongPosition {
        BasicBall tmp;
        BasicBall out[] = new BasicBall[ConstantValues.marketCol];
        whiteCount=0;
        pendingResourceExtracted = new ResourceList();
        if ((pos > marketRow) || (pos < 1)) {
            throw new WrongPosition("invalid position");
        } else {
            pos--;

            for (int i = 0; i < ConstantValues.marketCol; i++)
                out[i] = resouces[pos][i];

            for (int i = 1; i < ConstantValues.marketCol; i++) {
                tmp = resouces[pos][i];
                resouces[pos][i] = resouces[pos][0];
                resouces[pos][0] = tmp;
            }
            tmp = discardedResouce;
            discardedResouce = resouces[pos][0];
            resouces[pos][0] = tmp;

            for(BasicBall i:out){
                i.active(this,p);
            }

        }
    }
    /**
     *
     * @param pos column position, it must be between 1 and 4
     * @param p player
     */
    public void exstractColumn(int pos,Player p) throws WrongPosition {
        BasicBall tmp;
        BasicBall out[] = new BasicBall[marketRow];
        whiteCount=0;
        pendingResourceExtracted = new ResourceList();
        if ((pos > marketCol) || (pos < 1)) {
            throw new WrongPosition("invalid position");
        } else {
            pos--;
            for (int i = 0; i < marketRow; i++)
                    out[i] = resouces[i][pos];

            for (int i = 1; i < marketRow; i++) {
                tmp = resouces[i][pos];
                resouces[i][pos] = resouces[0][pos];
                resouces[0][pos] = tmp;
            }
            tmp = discardedResouce;
            discardedResouce = resouces[0][pos];
            resouces[0][pos] = tmp;

            for(BasicBall i:out){
                i.active(this,p);
            }

        }
    }

    /**
     * increment whiteCount
     */
    public void incrementWhiteCount(){
        whiteCount++;
    }

    /**
     *  add resourse in pendingResourceExtracted
     * @param type
     */
    public void addResourceExtracted(ResourceType type){
        pendingResourceExtracted.add(new Resource(type,1));
    }

    /**
     * get pendingResourceExtracted
     * @return pendingResourceExtracted
     */
    public List<Resource> getPendingResourceExtracted() {
        return pendingResourceExtracted;
    }

    public int getWhiteCount() {
        return whiteCount;
    }
}
