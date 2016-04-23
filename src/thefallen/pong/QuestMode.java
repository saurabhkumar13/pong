package thefallen.pong;

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

    Ball.onDiedListener diedListener = new Ball.onDiedListener() {

        @Override
        public void onDied(int index,SinglePlayer lol)
        {
            if(index == 0)
            {
                uDied = true;
                out.println("USER DIED");
            }
            else
            {
                aiDied = true;
                out.println("AI DIED");

                if(!constructing && aiLevel<5)
                {
                    startLevel(aiLevel+1);
                }
            }
        }
    };

    public void startLevel(int level)
    {}

}
