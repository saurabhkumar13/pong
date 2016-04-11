package thefallen.pong;


import org.jdesktop.core.animation.timing.Animator;
import org.json.JSONObject;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import static java.lang.Math.PI;
import static java.lang.System.out;

public class Racket {
    int x, y,UPkey=KeyEvent.VK_LEFT,DOWNkey=KeyEvent.VK_RIGHT;
//    ping master;
    boolean sentient=true,safe=false,user=false;
    float v,a,dt=.5f,f=.2f,speed=30;
    int initx,frame;
    int width=100,height=10,state=0,hp=10,n,N;
    Animator animator;
    Ball ball;
    Point2D center;

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
        if(ball!=null)
        {
            if(sentient&&!user)
            {
                Point2D ballpos = new Point((int)ball.x,(int)ball.y);
                AffineTransform.getRotateInstance(
                        2*PI*n/(N), center.getX(), center.getY())
                        .transform(ballpos,ballpos);
                x =(int)ballpos.getX()-initx-width/2;//-200-40*(N-3);
//                y =(int)ballpos.getY();//-200-40*(N-3);
//                out.println("racket"+n+" "+(2*PI*n/(N))+" "+ballpos.getX()+" "+ballpos.getY());
//                out.println("RACKET"+frame+" "+width);
            }

        }
        x+=v*dt;
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
        if(e==KeyEvent.VK_UP)
            state=1;
        else if(e==KeyEvent.VK_DOWN)
            state=-1;
        else state=0;

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
