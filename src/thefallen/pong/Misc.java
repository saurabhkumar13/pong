package thefallen.pong;

import org.json.JSONObject;

/**
 * Created by saurabh on 4/21/2016.
 */

public class Misc {
    public static JSONObject findServer = new JSONObject().accumulate("command",Command.FIND);
    public static JSONObject join = new JSONObject().accumulate("command",Command.JOIN);
    public static JSONObject Slavejoined = new JSONObject().accumulate("command",Command.JOINedslave);
    static int Port = 6969;
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


