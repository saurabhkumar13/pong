package thefallen.pong;

import javax.swing.*;
import java.awt.event.*;

import static com.sun.glass.ui.Cursor.setVisible;
import static java.lang.System.err;
import static java.lang.System.out;

/**
 * Created by saurabh on 4/14/2016.
 */

public class SinglePlayer {

    pong game;
    int N;
    int uHP = 100;
    static int init_num = 4;
    static int init_hp = 100;
    boolean pause_flag = false,died;
    SinglePlayer quest;
    String time;
    Ball.onDiedListener diedListener = new Ball.onDiedListener() {
        @Override
        public void onDied(int index, SinglePlayer lol) {
            if(index==0)
            {
                died = true;
                err.println("YOU DIED");
            }
        }
    };

    Timer countdownTimer;
    int timeLimit = 30;
    int timeRemaining;

    public SinglePlayer()
    {
        quest = this;
    }

    class CountdownTimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (--timeRemaining > 0) {
                time = timeRemaining+"";
            } else {
                time = "Aww Yiss";
                timeRemaining = timeLimit;
                N++;
                newLevel();
            }
        }
    }


    public void pressed(int e)
    {
        if(e == KeyMap.exit)
            pause();
        else if(e == KeyMap.resume&&pause_flag)
            pause();
    }

    void pause()
    {
        if(pause_flag)
        {
            pause_flag = false;
            game.ball.dt = 1;
            game.rackets[0].dt = 0.1f;
            countdownTimer.start();
        }
        else
        {
            pause_flag = true;
            game.ball.dt = 0;
            game.rackets[0].dt = 0f;
            countdownTimer.stop();
        }

    }



    public void instantiate_game()
    {
        countdownTimer = new Timer(1000, new CountdownTimerListener());
        timeRemaining = timeLimit;
        countdownTimer.start();
        newLevel();

    }
    pong temp;
    public void newLevel() {
        out.println("Number of players : " + N);
        time = "Press Space to start";
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(game!=null)
                    game.f_frame.setVisible(false);
                temp=game;
                game = new pong(N,0);
                game.lol = quest;
                game.onDiedListener = diedListener;
                    game.addBall();
                    pause();
                    game.ball.vx = 1;
                    game.ball.vy = 2;

                    for (int i = 0; i < N; i++) {
                        if (i == 0) {
                            game.rackets[0].hp = uHP;
                        } else {
                            game.rackets[i].hp = init_hp;
                            out.println("Init_hp" + i + " " + game.rackets[i].hp);
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


    public void startQuest()
    {
        N = init_num;
        instantiate_game();

    }

    static public void main(String[] args)
    {
        SinglePlayer quest = new SinglePlayer();
        quest.startQuest();
    }
}
