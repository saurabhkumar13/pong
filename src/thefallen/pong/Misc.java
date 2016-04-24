package thefallen.pong;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.json.JSONObject;

import java.io.File;

/**
 * Used for miscellaneous declarations, enumerations etc.
 * Created by saurabh on 4/21/2016.
 */

public class Misc {

    static int Port = 6969;

    static String Water = "The element of calmness and protectiveness gives you the advantage of a longer paddle and helps calm the game down a little by slowing the ball down on rebound.";
    static String Wind = "Rising from the speedy nature of the wind this element imparts your paddle with the special power of moving faster than everyone else.";
    static String Earth = "The personification of strength itself , this elemant gives an extra moment of respite in tis cruel world of ping pong, gives you an extra HP.";
    static String Fire = "The fiercest element of the game true to it's name spices up the game by speeding up the ball every time it the paddle meets it.";
    static String Void = "The all encompassing power of space might just do the trick for you.Without any special powers or handicaps experience the power of simplicity ";

    static int sfxvol=3;
    static Media popSound = new Media(new File("src/res/pop.wav").toURI().toString());
    static void pop ()
    {
        MediaPlayer mp=new MediaPlayer(popSound);
        mp.play();
        mp.setVolume(((double)Integer.valueOf(sfxvol))/10);

    }

    static Media popSound2 = new Media(new File("src/res/pop2.wav").toURI().toString());
    static void pop2 ()
    {
        try {
            MediaPlayer mp=new MediaPlayer(popSound2);
            mp.play();
            mp.setVolume(((double)Integer.valueOf(sfxvol))/10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static Media youDiedSound = new Media(new File("src/res/youdied.mp3").toURI().toString());
    static void youDied ()
    {
        try {
            MediaPlayer mp=new MediaPlayer(youDiedSound);
            mp.play();
            mp.setVolume(((double)Integer.valueOf(sfxvol))/10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum Avatar{
        VOID,EARTH,WIND,FIRE,WATER
    }

    public enum Command{
        START,ACTION,L,R,LT,RT,ReleaseKeyT,ReleaseKeyV,SyncHP,SyncBall,FIND,FINDreply,JOIN,JOINedslave,INITBall,BallReady,Died
    }

    public enum Modes{
        DEATHMATCH,NORMAL
    }

    public enum state {
        INIT,WAITmaster,WAITslave,GAMING
    }

    static double INITballvx = 7,INITballvy = 5;
}


