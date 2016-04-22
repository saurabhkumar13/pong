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
    int x, y;
    ping master;
    boolean sentient=true,safe=false,user=false,diedOnce;
    float v,a,dt=.5f,f=.2f,speed=20,ai_speed=10;
    int initx,frame;
    int width=100,height=10,state=0,hp=3,n,N;
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
                        2 * PI * n / (N), center.getX(), center.getY())
                        .transform(ballpos, ballpos);

//                x = (int) ballpos.getX() - initx - width / 2;//-200-40*(N-3);
                int dir = (int) ballpos.getX() - initx - width / 2;//-200-40*(N-3);
                if (dir - x > width / 2) dir = 1;
                else if (dir - x < - width / 2) dir = -1;
                else dir = 0;
                x+=ai_speed*dir*dt;
                if(x+width>frame) x = frame - width;
                else if(x<0) x=0;
//                y =(int)ballpos.getY();//-200-40*(N-3);
//                out.println("racket"+n+" "+(2*PI*n/(N))+" "+ballpos.getX()+" "+ballpos.getY());
//                out.println("RACKET"+frame+" "+width);
            }
            else
            {
                if(x+v*dt>frame) x = frame;
                else if(x+v*dt<-width) x = -width;
                else x+=v*dt;
            }

        }
    }
    public void pressed(int e) {
        if (e == KeyMap.left && v != -speed) {
            v = -speed;
            if (master != null) {
                master.broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.ACTION).accumulate("action",Misc.Command.L)).toString());
            }
        } else if (e == KeyMap.right && v != speed) {
            v = speed;
            if (master != null) {
                master.broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.ACTION).accumulate("action",Misc.Command.R)).toString());
            }
        }
        if (e == KeyMap.tiltLeft){
            if (master != null) {
                master.broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.ACTION).accumulate("action",Misc.Command.LT)).toString());
            }
            state = 1;
        }
        else if(e==KeyMap.tiltRight){
            if (master != null) {
                master.broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.ACTION).accumulate("action",Misc.Command.RT)).toString());
            }
        state=-1;
    }

        else state=0;

    }
    public void typed(int e)
    {}
    public void released(int e)
    {
        if(v!=0) {
            v=0;
            if (master!=null) {
                master.broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.ACTION).accumulate("action",Misc.Command.ReleaseKey)).toString());
            }
        }
    }
}
