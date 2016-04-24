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


