package thefallen.pong;

import javax.swing.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import static java.lang.System.err;
import static java.lang.System.out;

/**
 * Created by mayank on 23/04/16.
 */

public class QuestMode {

    //Declaring and initialising variables

    pong game;
    int num_ai = 1, init_hp = 80, max_ai = 5;
    int userHp = 80, score = 0, aiLevel = 1;
    boolean uDied = false, aiDied = false, pause_flag = false, constructing = false, gOver = false,success = false,G,full=true;
    QuestMode quest;
    onGameOverListener gameOverListener;
    Random rand = new Random();

    interface onGameOverListener{
        void onGameOver(boolean success, int score);
    }

    public QuestMode() {
        quest = this;
    }

    // Performs tasks on death of one of the paddles.
    // If user dies ends the game, if AI dies either the quest is completed or a new level is started

    Ball.onDiedListener diedListener = new Ball.onDiedListener() {

        @Override
        public void onDied(int index) {
            if (index == 0) {
                uDied = true;
                out.println("USER DIED");
                failedQuest();
            } else {
                aiDied = true;
                out.println("AI DIED "+constructing+" "+aiLevel+" "+max_ai);

                if (!constructing && aiLevel < max_ai)
                {
                    out.println("Constructing new AI");
                    userHp = game.rackets[0].hp;
                    aiLevel+=1;
                    startLevel(aiLevel);
                    constructing = true;
                }
                else if (aiLevel == max_ai) {
                    successQuest();
                }

            }
        }
    };

    public void startLevel(int level) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                if (game != null) {
                    game.shutdown();
                }

                game = new pong(num_ai + 1, level,full);
                if(G)
                    game.ball.setGravity();
                game.ball.diedListener = diedListener;
                game.ball.setVel();
                pause();

                constructing = false;
                err.println("constructing done " + level);

                //Give initial hp values to all paddles

                for (int i = 0; i < num_ai + 1; i++) {
                    if (i == 0) {
                        game.rackets[0].hp = userHp;
                        switch (level) {
                            case 2 : game.rackets[0].setPowerup(Misc.Avatar.WIND);
                                    break;
                            case 3 : game.rackets[0].setPowerup(Misc.Avatar.WATER);
                                    break;
                            case 4 : game.rackets[0].setPowerup(Misc.Avatar.EARTH);
                                    break;
                            case 5 : game.rackets[0].setPowerup(Misc.Avatar.FIRE);
                                    break;
                            default: break;
                        }
                    } else {
                        game.rackets[i].hp = init_hp;
                        switch (rand.nextInt(5)) {
                            case 1 : game.rackets[i].setPowerup(Misc.Avatar.EARTH);
                                break;
                            case 2 : game.rackets[i].setPowerup(Misc.Avatar.FIRE);
                                break;
                            case 3 : game.rackets[i].setPowerup(Misc.Avatar.WIND);
                                break;
                            case 4 : game.rackets[i].setPowerup(Misc.Avatar.WATER);
                                break;
                            default: break;
                        }
                    }
                }

                //Setup the pause functionality
                game.f_frame.addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent  e) {
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

    //Call the functions as per the key that is currently pressed

    public void pressed(int e) {
        if (e == KeyMap.exit)
            pause();
        else if (e == KeyMap.resume)
            if (!pause_flag)
                pause();
    }

    //Handle the pause functionality

    void pause() {
        if(!gOver)
        {
            if (pause_flag)
            {
                pause_flag = false;
                if (game != null) game.pause();
                out.println("paused");
            }
            else
            {
                pause_flag = true;
                if (game != null) game.resume();
                out.println("resuming");
            }
        }

    }

    public void startQuest()
    {
        startLevel(aiLevel);
        pause();
    }

    public void successQuest() {
        out.println("Questcompleted");
        pause_flag = false;
        game.pause();
        gOver = true;
        success = true;
        endQuest();
    }

    public void failedQuest() {
        out.println("Questfailed");
        pause_flag = false;
        game.pause();
        gOver = true;
        endQuest();
    }

    public void endQuest()
    {
        if(gameOverListener != null)
        {
            gameOverListener.onGameOver(success,score);
        }
        game.shutdown();
    }

    public static void main(String[] args)
    {
        QuestMode nq = new QuestMode();
        nq.startQuest();
    }

}
