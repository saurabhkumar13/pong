package thefallen.pong;

import javax.swing.*;

import static java.lang.System.out;

/**
 * Created by saurabh on 4/14/2016.
 */
public class SinglePlayer {
    pong game;
    int N;
    Ball.onDiedListener diedListener = new Ball.onDiedListener() {
        @Override
        public void onDied(int index) {
            racketOnDied(index);
        }
    };
    public void constructGame(int i){
        N=i;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                game = new pong(i);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        game.onDiedListener = diedListener;
                        game.addBall();
                    }});
            }
        });
    }
    public void racketOnDied(int index)
    {
        int[] HPs  = new int[N-1];
        for(int i=0,k=0;i<N;i++)
        {
            HPs[i-k] = game.rackets[i].hp;
            if(i==index) k=1;
        }
        constructGame(N-1);
        for(int i=0;i<N;i++)
        game.rackets[i].hp = HPs[i];
    }
    static public void main(String[] args)
    {
        SinglePlayer quest = new SinglePlayer();
        quest.constructGame(5);
    }
}
