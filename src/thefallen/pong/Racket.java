package thefallen.pong;


import org.jdesktop.core.animation.timing.Animator;

import java.awt.event.KeyEvent;

import static java.lang.System.out;

public class Racket {
    int x, y,UPkey=KeyEvent.VK_UP,DOWNkey=KeyEvent.VK_DOWN;
    float v,a,dt=.5f,f=.2f;
    boolean vertical;
    int imageIndex,frame;
    private int width=10,height=100;
    Animator animator;
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
        if(e==UPkey) {
//            if(a==0)
             v=-30;
//            a=-1;
//            update();
        }
        else if(e==DOWNkey) {
//            if(a==0)
                v=30;
//            a=1;
//            update();
        }
    }
    public void typed(int e)
    {}
    public void released(int e)
    {
        v=0;
        a=0;
    }
}
