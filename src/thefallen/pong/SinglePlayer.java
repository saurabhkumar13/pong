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
    Timer[] timer = new Timer[3];
    int tim = 0;
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




    public void instantiate_game(int n)
    {
        timer[0] = new Timer(0,gameInstance(n));
        timer[0].setRepeats(false); // Only execute once
        timer[1] = new Timer(0,gameInstance(n+1));
        timer[1].setRepeats(false); // Only execute once
        timer[2] = new Timer(0,gameInstance(n+2));
        timer[2].setRepeats(false); // Only execute once
        countdownTimer = new Timer(1000, new CountdownTimerListener());
        countdownTimer.start();

        timer[0].start(); // Go go go!
        try
        {
            Thread.sleep(10000);
            out.print("dih");
        }
        catch (Exception e)
        {
            out.println("djhc");
        }
        timer[0].stop();
        tim = 1;
        timeRemaining = 10;
        countdownTimer.start();
        timer[1].start(); // Go go go!
        try
        {
            Thread.sleep(10000);
            out.print("dih2");
        }
        catch (Exception e)
        {
            out.println("djhc2");
        }
        timer[1].stop();
        tim = 2;
        timer[2].start(); // Go go go!
        timeRemaining = 10;
        countdownTimer.start();
        try
        {
            Thread.sleep(10000);
            out.print("dih3");
        }
        catch (Exception e)
        {
            out.println("djhc3");
        }
        timer[2].stop();

    }

    public ActionListener gameInstance (int n) {

        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                out.println("Number of players : " + n);
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {

                        game = new pong(n);
                        game.lol = quest;

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                game.addBall();
                                game.f_balls.get(0).vx = 3;
                                game.f_balls.get(0).vy = 5;

                                for (int i = 0; i < n; i++) {
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
        instantiate_game(N);

    }

    static public void main(String[] args)
    {
        SinglePlayer quest = new SinglePlayer();
        quest.startQuest();
    }
}
