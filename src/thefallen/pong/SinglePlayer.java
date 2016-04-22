package thefallen.pong;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static com.sun.glass.ui.Cursor.setVisible;
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
    boolean pause_flag = false;
    Timer timer;
    SinglePlayer quest;
    String time;


    Timer countdownTimer;
    int timeRemaining = 10;

    public SinglePlayer()
    {
        quest = this;
    }

    class CountdownTimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (--timeRemaining > 0) {
                time = String.valueOf(timeRemaining);
            } else {
                time = "Time's up!";
                countdownTimer.stop();
            }
        }
    }


    public void pressed(int e)
    {
        if(e == KeyMap.exit)
        {
            if(pause_flag)
            {
                pause_flag = false;
                game.f_balls.get(0).dt = 1;
                game.rackets[0].dt = 0.5f;
                out.println("cdsjhkc");
            }
            else
            {
                pause_flag = true;
                game.f_balls.get(0).dt = 0;
                game.rackets[0].dt = 0f;
                out.println("jhdcj");
            }

        }
    }




    public void instantiate_game()
    {
        timer = new Timer(0,gameInstance());
        timer.setRepeats(false); // Only execute once
        while(true) {
            countdownTimer = new Timer(1000, new CountdownTimerListener());
            countdownTimer.start();
            timer.start();
            try {
                gameInstance();
                Thread.sleep(10000);
                out.print("sleeping now for 10s");
            } catch (Exception e) {
                out.println("oops");
            }
            timer.stop();
            N++;
            timeRemaining = 10;
        }
    }

    public ActionListener gameInstance () {

        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                out.println("Number of players : " + N);
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {

                        game = new pong(N);

                        game.lol = quest;

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                game.addBall();
                                game.f_balls.get(0).vx = 3;
                                game.f_balls.get(0).vy = 5;

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
                });
            }
        };
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
