package thefallen.pong;


import org.jdesktop.core.animation.timing.Animator;
import org.json.JSONObject;

import java.awt.event.KeyEvent;

import static java.lang.System.out;

public class Racket {
    int x, y,UPkey=KeyEvent.VK_LEFT,DOWNkey=KeyEvent.VK_RIGHT;
//    ping master;
    boolean sentient;
    float v,a,dt=.5f,f=.2f,speed=30;
    int imageIndex,frame;
    int width=100,height=10;
    Animator animator;

//    public Racket(ping master,boolean sentient){
//        if(master!=null) this.master=master;
//        this.sentient=sentient;
//    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public int getH() {
        return height;
    }
    public int getW() {
        return width;
    }


    public void update(){
        v+=a*dt;
//        if(!(x+v*dt+height>frame||x+v*dt<0))
        x+=v*dt;
//        else if(x+v*dt+height>frame)
//        {
//            x=frame-height;
//            v=0;
//        }
//        else {
//            x=0;
//            v=0;
//        }
    }
    public void pressed(int e)
    {
        if(e==UPkey&&v!=-speed) {
             v=-speed;
//            if (sentient) {
//                master.broadcastToGroup(ping.ingame.put("key",ping.Command.UpKey.ordinal()).toString());
//            }
        }
        else if(e==DOWNkey&&v!=speed) {
            v=speed;
//            if (sentient) {
//                master.broadcastToGroup(ping.ingame.put("key",ping.Command.DownKey.ordinal()).toString());
//            }
        }
    }
    public void typed(int e)
    {}
    public void released(int e)
    {
        if(v!=0) {
            v=0;
//            if (sentient) {
//                master.broadcastToGroup(ping.ingame.put("key",ping.Command.ReleaseKey.ordinal()).toString());
//            }
        }
    }
}
