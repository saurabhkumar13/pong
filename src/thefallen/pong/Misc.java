package thefallen.pong;

import org.json.JSONObject;

/**
 * Created by saurabh on 4/21/2016.
 */
public class Misc {
    public static JSONObject findServer = new JSONObject().accumulate("command",Command.FIND);
    public static JSONObject join = new JSONObject().accumulate("command",Command.JOIN);
    public static JSONObject Slavejoined = new JSONObject().accumulate("command",Command.JOINedslave);
    public enum Command{
        START,STOP,REPLY,GAMING,UpKey,DownKey,ReleaseKey,RequestBall,GotBall,FIND,FINDreply,JOIN,JOINedslave,INITBall,JOINack
    }
    public enum Modes{
        DEATHMATCH
    }
    public enum state {
        INIT,WAITmaster,WAITslave
    }
    static double INITballvx,INITballvy;
}


