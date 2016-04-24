package thefallen.pong;
import java.net.URL;

import com.surelogic.Utility;

/**
 * Manages all the resources used by demonstration programs. This avoids
 * duplication of resources if they are used by more than one demonstration
 * program.
 *
 * @author Tim Halloran
 */
@Utility
public final class Resources {

    public static final String BLUE_SPHERE = "blue-sphere.png";
    public static final String GRAY_SPHERE = "gray-sphere.png";
    public static final String GREEN_SPHERE = "green-sphere.png";
    public static final String RED_SPHERE = "red-sphere.png";
    public static final String YELLOW_SPHERE = "yellow-sphere.png";
    public static final String PONG_SPHERE = "ping_pong_png10383.png";
    public static final String FONT1 = "fonts/ZinHenaBokuryu-RCF.ttf";
    public static final String FONT2 = "fonts/GoodDogPlain.ttf";
    public static final String LOGO = "logo_small.png";
    public static final String BACK = "back.png";
    public static final String music = "bg.mp3";

    public static final String[] SPHERES = { BLUE_SPHERE, GRAY_SPHERE, GREEN_SPHERE, RED_SPHERE, YELLOW_SPHERE};

    private static final String PREFIX = "res/";

    /**
     * Gets the passed resource in the classpath.
     *
     * @param name
     *          the resource name.
     * @return a reference to the resource that can be used to load it.
     */
    public static URL getResource(String name) {
        final URL result = Thread.currentThread().getContextClassLoader().getResource(PREFIX +  name);
        if (result == null)
            throw new IllegalStateException("Unable to load resource: " + name);
        else
            return result;
    }
}
