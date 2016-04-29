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

    static String br_desc = "\n\nBATTLE ROYALE\nWut? Just kill 'em all ";

    static String controls ="\n\nCONTROLS\nLeft Arrow  : left movement\n" +
            "Right Arrow : right movement\n" +
            "      A     : left tilt\n"+
            "      D     : right tilt\n" +
            "      Esc   : Pause/Resume\n" +
            "      Space : Resume";

    static String cites = "\n\nCREDITS\nFundamentum  - Soundtrack by Ridiculon\n" +
            "Ping-Pong Pan Taisou - Soundtrack by Morning Musume\n" +
            "SFX - Dark Souls, www.freesoundeffects.com";

    static String copyright = "\n\n\t\t\t\t © The Fallen Games\n\t\t\t\tDevelopers - L, Flife and Light";

    static String aboutDesc =   "QUEST\nThe world was at peace.The four nations Earth, Water, Air and Fire lived in harmony. " +
                                "\nBut everything changed when the Fire nation attacked. Only the avatar, the master of all " +
                                "\n4 elements could restore balance to the world but he disappeared. This world needs you." + br_desc + controls + cites + copyright;

    // Avatar Descriptions

    static String Water =  "  Water extinguishes the flame, by stretching\n" +
                           "  forth and challenging the heavens    ";
    static String Wind =   "  the manifestation of movement, freshness,  \n" +
                           "  communication and of the intelligence   ";
    static String Earth =  "  the element of stability, foundations and  \n" +
                           "  of the body ";
    static String Fire =   "  Most destructive of the elements, a force \n" +
                           "  of nature springing from celestial sources";

    static String Void =   "   \"nihil fit ex nihilo\" Out of nothing   \n" +
                           "   nothing becomes";


    // Music and SFX managing functions and variables

    static int sfxvol=10;

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


