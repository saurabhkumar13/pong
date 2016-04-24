package thefallen.pong;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static com.sun.glass.ui.Cursor.setVisible;
import static java.lang.System.err;
import static java.lang.System.in;
import static java.lang.System.out;

/**
 * Created by saurabh on 4/14/2016.
 */

public class SinglePlayer2 {

    pong game;
    int N;
    int uHP = 80;
    static int init_num = 4;
    static int init_hp = 40;
    boolean pause_flag = false,constructing;
    SinglePlayer2 quest;

    Ball.onDiedListener diedListener = new Ball.onDiedListener() {
        @Override
        public void onDied(int index) {
            out.println(index+":  YOU DIED");
            if(N>2&&!constructing&&index!=0)
            {
                err.println("started constructing");
                uHP = game.rackets[0].hp;
                startGame(N-1);
                N--;
                constructing=true;
            }
            else if(index==0)
            {
                if(listener!=null)
                    listener.onGameOver(false,42);
                out.println("YOU DIED");
            }
            else if(N==2)
            {
                if(listener!=null)
                listener.onGameOver(true,42);
                out.println("YOU WON");
            }
        }
    };
    QuestMode.onGameOverListener listener;
    public SinglePlayer2()
    {
        quest = this;
    }
    public void pressed(int e)
    {
        if(e == KeyMap.exit)
            pause();
        else if(e==KeyMap.resume)
            if(!pause_flag)
                pause();
    }

    void pause()
    {
        if(pause_flag)
        {
            pause_flag = false;
            if(game!=null)game.pause();
            out.println("paused");
        }
        else
        {
            pause_flag = true;
            if(game!=null)game.resume();
            out.println("resuming");
        }

    }
    boolean G;
    Misc.Avatar mode;
    int Diff;
    boolean full=true;
    void startGame(int n)
    {
        System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(game!=null) {
                    game.pause();
                    game.f_frame.setVisible(false);
                }
                game = new pong(n,Diff,full);
                if(G)
                    game.ball.setGravity();
                game.rackets[0].setPowerup(mode);
                game.ball.diedListener= diedListener;
                pause();
                game.ball.setVel();
                constructing = false;
                err.println("constructing done");
                for (int i = 0; i < n; i++) {
                    if (i == 0) {
                        game.rackets[0].hp = uHP;
                    } else {
                        game.rackets[i].hp = init_hp;
                    }
                }
                game.f_frame.addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        pressed(e.getKeyCode());
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {

                    }
                });
            }
        });

    }


      public void startQuest(boolean G,int Start,Misc.Avatar mode)
    {
        init_num = Start;
        N = init_num;
        this.G = G;
        this.mode = mode;
        startGame(N);
        pause();
    }

    static public void main(String[] args)
    {
        SinglePlayer2 quest = new SinglePlayer2();
        quest.startQuest(false,10, Misc.Avatar.WATER);
    }
}
