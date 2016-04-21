package thefallen.pong;

import org.json.JSONObject;

/**
 * Created by saurabh on 4/21/2016.
 */
public class Misc {
    public static JSONObject findServer = new JSONObject().accumulate("command",Command.FIND);
    public static enum Command{
        START,STOP,REPLY,GAMING,UpKey,DownKey,ReleaseKey,RequestBall,GotBall,FIND,FINDreply
    }
    public static enum Modes{
        DEATHMATCH
    }
}


