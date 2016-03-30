package thefallen.pong;


import org.jdesktop.core.animation.timing.Animator;

public class Ball {
    int x, y,vx,vy,ax,ay;
    int imageIndex,frameH,frameW;
    private int dt=1;
    Animator animator;
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void update(){
        vx+=ax*dt;
        vy+=ay*dt;
        if(x+vx*dt>frameW||x+vx<0) vx=-vx;
        if(y+vy*dt>frameH||y+vy<0) vy=-vy;
        x+=vx*dt;
        y+=vy*dt;

    }
}
