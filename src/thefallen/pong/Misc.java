package thefallen.pong;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by saurabh on 4/21/2016.
 */

public class Misc {
    public static JSONObject findServer = new JSONObject().accumulate("command",Command.FIND);
    public static JSONObject join = new JSONObject().accumulate("command",Command.JOIN);
    public static JSONObject Slavejoined = new JSONObject().accumulate("command",Command.JOINedslave);
    static int Port = 6969;
    static Media popSound = new Media(new File("src/res/pop.wav").toURI().toString());
    static void pop ()
    {
        (new MediaPlayer(popSound)).play();
    }
    static Media popSound2 = new Media(new File("src/res/pop2.wav").toURI().toString());
    static void pop2 ()
    {

        try {
            (new MediaPlayer(popSound2)).play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum Avatar{
        EARTH,WIND,FIRE,WATER
    }

    public enum Command{
        START,ACTION,L,R,LT,RT,ReleaseKeyT,ReleaseKeyV,SyncHP,SyncBall,FIND,FINDreply,JOIN,JOINedslave,INITBall,JOINack,BallReady
    }
    public enum Modes{
        DEATHMATCH,NORMAL
    }
    public enum state {
        INIT,WAITmaster,WAITslave,GAMING
    }
    static double INITballvx = 7,INITballvy = 5;
}


