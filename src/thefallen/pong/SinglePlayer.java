package thefallen.pong;

import javax.swing.*;

import java.lang.reflect.Array;
import java.util.Arrays;

import static java.lang.System.out;

/**
 * Created by saurabh on 4/14/2016.
 */

public class SinglePlayer {

    pong game;
    int N;
    int[] HPs;
    boolean[] died;
    boolean constructing;
    static int init_val = 7;

//    Ball.onDiedListener diedListener = new Ball.onDiedListener() {
//        @Override
//        public void onDied(int index) {
//            racketOnDied(index);
//        }
//    };
//
//    public void constructGame(int i)
//    {
//        N=i;
//        SwingUtilities.invokeLater(new Runnable() {
//
//            @Override
//            public void run()
//            {
//                game = new pong(i);
//
//                SwingUtilities.invokeLater(new Runnable() {
//
//                    @Override
//                    public void run() {
//
//                        game.onDiedListener = diedListener;
//                        game.addBall();
//
//                        for (int i = 0,k = 0; i < HPs.length; i++)
//                        {
//                            if(died[i])
//                            {
//                                i++;
//                                k++;
//                            }
//                            else
//                            {
//                                game.rackets[i-k].hp = 5;//HPs[i];
//                            }
//                        }
//
//                        HPs = new int[i];
//                        died = new boolean[i];
//                        constructing = false;
//
//                    }
//
//
//                });
//            }
//        });
//    }
//
//    public void racketOnDied(int index)
//    {
//        out.println("dieded");
//
//        if (!constructing)
//        {
//            died[index]=true;
//
//            for (int i=0;i<game.rackets.length;i++)
//                HPs[i]=game.rackets[i].hp;
//
//            if(N>2)
//            {
//                constructing=true;
//                constructGame(N-1);
//            }
//        }
//    }
//
    public void setupGame(int n)
    {
    }


    static public void main(String[] args)
    {
        SinglePlayer quest = new SinglePlayer();
        quest.setupGame(init_val);
        quest.died = new boolean[init_val];
        quest.HPs = new int[init_val];
    }
}
