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

    static String aboutDesc = "Game Modes :";

    // Avatar Descriptions

    static String Water = "";
    static String Wind =  "";
    static String Earth = "";
    static String Fire =   "  Most destructive of the elements, a force \n" +
                           "  of nature springing from celestial sources";

    static String Void =  "";

    // Music and SFX managing functions and variables

    static int sfxvol=3;

    // Button Click sound

    static Media popSound = new Media(thefallen.pong.Resources.getResource("pop.wav").toExternalForm());

    static void pop ()
    {
        MediaPlayer mp=new MediaPlayer(popSound);
        mp.play();
        mp.setVolume(((double)Integer.valueOf(sfxvol))/10);
    }

    // Ball paddle collision sound

    static Media popSound2 = new Media(thefallen.pong.Resources.getResource("pop2.wav").toExternalForm());

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

    // Sound when a player dies

    static Media youDiedSound = new Media(thefallen.pong.Resources.getResource("youdied.mp3").toExternalForm());

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
        START,ACTION,L,R,LT,RT,ReleaseKeyT,ReleaseKeyV,SyncHP,SyncBall,FIND,FINDreply,JOIN,JOINedslave,INITBall,BallReady,Died,Disconnect
    }

    public enum Modes{
        DEATHMATCH,NORMAL
    }

    public enum state {
        INIT,WAITmaster,WAITslave,GAMING
    }

    static double INITballvx = 7,INITballvy = 5;
}


