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
    int uHP;
    boolean[] died;
    boolean constructing;
    static int init_num = 2;
    static int init_hp = 100;

    Ball.onDiedListener diedListener = new Ball.onDiedListener() {
        @Override
        public void onDied(int index,SinglePlayer lol) {
            racketOnDied(index,lol);

        }
    };

    public void racketOnDied(int index,SinglePlayer quest)
    {
        out.println(index + " dieded");

        if (!quest.constructing)
        {
            quest.died[index]=true;
            quest.uHP=game.rackets[0].hp;

            if(N<6)
            {
                out.println("New Game Called "+N);
                quest.constructing=true;
                setupGame(N+1,quest);
            }
            else
            {
                out.println("Game Pause "+ N);
                for(int i = 0;i<N;i++)
                {
                    game.rackets[i].dt = 0f;
                }
                game.f_balls.get(0).dt = 0f;
            }
        }
    }

    public void setupGame(int n,SinglePlayer quest)
    {
        N = n;
        out.println("Number of players : "+n);
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                game = new pong(n);
                game.lol = quest;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run()
                    {
                        game.onDiedListener = quest.diedListener;
                        game.addBall();

                        for (int i = 0, k = 0; i < N; i++)
                        {
                            if(i == 0)
                            {
                                game.rackets[0].hp = quest.uHP;
                            }
                            else
                            {
                                game.rackets[i].hp = init_hp;
                                out.println("Init_hp"+i+" "+game.rackets[i].hp);
                            }
                            if(i < N-1)
                            {
                                if (quest.died[i]) {
                                    quest.died[i] = false;
                                }
                            }
                        }

                        quest.died = new boolean[n];
                        Arrays.fill(quest.died,false);
                        constructing = false;
                    }
                });
            }
        });
    }


    static public void main(String[] args)
    {
        SinglePlayer quest = new SinglePlayer();
        quest.died = new boolean[init_num];
        Arrays.fill(quest.died,false);
        quest.uHP = init_hp;
        quest.setupGame(init_num,quest);
    }
}
