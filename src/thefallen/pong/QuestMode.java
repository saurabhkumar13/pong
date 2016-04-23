package thefallen.pong;

import javax.swing.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static java.lang.System.err;
import static java.lang.System.out;

/**
 * Created by mayank on 23/04/16.
 */

public class QuestMode {

    //Declaring and initialising variables

    pong game;
    static final int num_ai = 1, init_hp = 80;
    int userHp = 80, score = 0, aiLevel = 1;
    boolean uDied = false, aiDied = false, pause_flag = false, constructing = false;
    QuestMode quest;

    public QuestMode() {
        quest = this;
    }

    // Performs tasks on death of one of the paddles.
    // If user dies ends the game, if AI dies either the quest is completed or a new level is started

    Ball.onDiedListener diedListener = new Ball.onDiedListener() {

        @Override
        public void onDied(int index, SinglePlayer lol) {
            if (index == 0) {
                uDied = true;
                out.println("USER DIED");
                endQuest();
            } else {
                aiDied = true;
                out.println("AI DIED");

                if (!constructing && aiLevel < 5) {
                    out.println("Constructing new AI");
                    userHp = game.rackets[0].hp;
                    startLevel(aiLevel + 1);
                    constructing = true;
                } else if (aiLevel == 5) {
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
                    game.pause();
                    game.f_frame.setVisible(false);
                }

                game = new pong(num_ai + 1, level);

                game.ball.diedListener = diedListener;
                pause();

                constructing = false;
                err.println("constructing done");

                //Give initial hp bvalues to all paddles
                for (int i = 0; i < num_ai + 1; i++) {
                    if (i == 0) {
                        game.rackets[0].hp = userHp;
                    } else {
                        game.rackets[i].hp = init_hp;
                    }
                }

                //Setup the pause functionality
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

    public void startQuest()
    {
        startLevel(1);
        pause();
    }

    public void successQuest() {
    }

    public void endQuest() {
    }

    public static void main(String[] args)
    {
        QuestMode nq = new QuestMode();
        nq.startQuest();
    }

}