package thefallen.pong;


import org.jdesktop.core.animation.timing.Animator;
import org.json.JSONObject;

import java.awt.event.KeyEvent;

import static java.lang.System.out;

public class Racket {
    int x, y,UPkey=KeyEvent.VK_UP,DOWNkey=KeyEvent.VK_DOWN;
    ping master;
    boolean sentient;
    float v,a,dt=.5f,f=.2f;
    boolean vertical;
    int imageIndex,frame;
    int width=10,height=100;
    Animator animator;
    JSONObject msg;

    public Racket(ping master,boolean sentient){
        if(master!=null) this.master=master;
        this.sentient=sentient;
    }
    public int getX() {
        if(!vertical) return y; return x;
    }
    public int getY() {
        if(!vertical) return x; return y;
    }

    public int getH() {
        if(!vertical) return width; return height;
    }
    public int getW() {
        if(!vertical) return height; return width;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void update(){
        v+=a*dt;
        if(!(x+v*dt+height>frame||x+v*dt<0))
        x+=v*dt;
        else {v=0;}
    }
    public void pressed(int e)
    {
        if(e==UPkey&&v!=-30) {
             v=-30;
            if (sentient) {
                msg = new JSONObject(ping.ingame.toString());
                master.broadcastToGroup(msg.accumulate("key",ping.Command.UpKey.ordinal()).toString());
            }
        }
        else if(e==DOWNkey&&v!=30) {
            v=30;
            if (sentient) {
                msg = new JSONObject(ping.ingame.toString());
                master.broadcastToGroup(msg.accumulate("key",ping.Command.DownKey.ordinal()).toString());
            }
        }
    }
    public void typed(int e)
    {}
    public void released(int e)
    {
        if(v!=0) {
            v=0;
            if (sentient) {
                msg = new JSONObject(ping.ingame.toString());
                master.broadcastToGroup(msg.accumulate("key",ping.Command.ReleaseKey.ordinal()).toString());
            }
        }
    }
}
