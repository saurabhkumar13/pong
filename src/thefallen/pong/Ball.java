package thefallen.pong;


import com.sun.javafx.geom.Vec2d;
import org.jdesktop.core.animation.timing.Animator;

import java.awt.*;
import java.awt.geom.Point2D;

import static java.lang.Math.*;
import static java.lang.System.out;

public class Ball {
    double x, y,vx,vy,ax,ay,omega,theta,mu=0.3,r=10;
    int imageIndex,frameH,frameW;
    private int dt=1;
    Animator animator;
    Polygon base;
    Point2D center;
    int N;
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getSideofPolygon()
    {
        double angleInRadians = Math.atan2(center.getY() - y, x - center.getX());
        int n;
        angleInRadians-=PI/2;
        if(N%2==0)
            angleInRadians+=PI/N;
        if(angleInRadians<0) angleInRadians+=2*PI;
        n=(int)(N*angleInRadians/(2*PI));
        n+=(N+1)/2;
        n%=N;
        return n;
    }
    boolean mew;
    public void update(){
        vx+=ax*dt;
        vy+=ay*dt;
        theta+=omega*dt*0.1;
        if(!base.contains(x+vx*dt,y+vy*dt)) {
            int n=getSideofPolygon();
            double angleInRadians = Math.atan2(-vy,vx);
            double normal = 2*PI*n/N-PI/2;
            double toRotate = 2*(angleInRadians-normal)+PI;
            double vn,vt,vn_,vt_;
            if(angleInRadians-normal<PI/2)
            {
//                out.println(angleInRadians*180/PI+" "+normal*180/PI+" "+toRotate*180/PI+" "+vx+" "+vy);
                vn = vx*cos(normal)-vy*sin(normal);
                vt = vx*sin(normal)+vy*cos(normal);
                vn_=-vn;
                vt_ = vt - mininum(vt-omega*r,2*mu*vn);
                out.println(omega+"  "+(vt-omega*r)+" "+2*mu*vn);
                omega = omega + mininum(vt-omega*r,2*mu*vn)/r;
                vx = (vt_*sin(normal) + vn_*cos(normal));
                vy = (vt_*cos(normal) - vn_*sin(normal));
//                out.println(angleInRadians*180/PI+" "+vx+" "+vy);
            }
        }
        if(x+vx*dt>frameW||x+vx<0) vx=-vx;
        if(y+vy*dt>frameH||y+vy<0) vy=-vy;
        x+=vx*dt;
        y+=vy*dt;

    }
    double mininum(double a, double b)
    {
        if(a>0&&b>0) return min(a,b);
        else if(a<0&&b<0) return -min(-a,-b);
        else if(a<0) return a;
        else return b;
    }
}
