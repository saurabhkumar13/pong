package thefallen.pong;


import com.sun.javafx.geom.Vec2d;
import org.jdesktop.core.animation.timing.Animator;

import java.awt.*;
import java.awt.geom.Point2D;

import static java.lang.Math.*;
import static java.lang.System.out;

public class Ball {
    double x, y,vx,vy,ax,ay,omega,theta,mu=0.3,r=1;
    int imageIndex,frameH,frameW;
    float dt=1,ddt=0.001f;
    Animator animator;
    Polygon base;
    Point2D center;
    Racket[] rackets;
    int N;
    boolean isGdecreasing=true;
    float gravity=1;
    onDiedListener diedListener;
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
    interface onDiedListener{
        void onDied(int index);
    }
    boolean mew;
    public void update(){
        vx+=ax*dt;
        vy+=ay*dt;
        theta+=omega*dt*0.05;
        double theta =- Math.atan2(y - center.getY(), x - center.getX()),f=0.9;
        if(gravity+dt>1) isGdecreasing=true;
        else if(gravity-dt<-1) isGdecreasing=false;
        if(isGdecreasing) gravity-=ddt;
        else gravity+=ddt;
        ax = gravity*.1*cos(theta);
        ay = -1*gravity*.1*sin(theta);
        double modv = vx*vx+vy*vy;
        // - mininum(vt-omega*r,2*mu*vn);
//        out.println(gravity);
        if(modv>100) {vx*=f;vy*=f;}
//
        if(!base.contains(x+vx*dt,y+vy*dt)) {
            int n=getSideofPolygon();
            if (!rackets[n].safe) {
                if (rackets[n].hp > 0) rackets[n].hp--;
                else diedListener.onDied(n);
                rackets[n].diedOnce = true;
            }
            else
                rackets[n].safe=false;
            double angleInRadians = Math.atan2(-vy,vx);
            double normal = 2*PI*n/N-PI/2;
            double toRotate = 2*(angleInRadians-normal)+PI;
            double vn,vt,vn_,vt_;
            if(angleInRadians-normal<PI/2)
            {
//                for(int k=0;k<rackets.length;k++)out.print(rackets[k].hp+" ");
//                out.println();
//                out.println(angleInRadians*180/PI+" "+normal*180/PI+" "+toRotate*180/PI+" "+vx+" "+vy);
                vn = vx*cos(normal)-vy*sin(normal);
                vt = vx*sin(normal)+vy*cos(normal);
                vn_=-vn;
                vt_ = vt;
                  omega = omega + mininum(vt-omega*r,2*mu*vn)/r;
                vx = (vt_*sin(normal) + vn_*cos(normal));
                vy = (vt_*cos(normal) - vn_*sin(normal));
//                out.println(angleInRadians*180/PI+" "+vx+" "+vy);
            }
        }
//        if(x+vx*dt>frameW||x+vx<0) vx=-vx;
//        if(y+vy*dt>frameH||y+vy<0) vy=-vy;
        x+=vx*dt;
        y+=vy*dt;

    }
    void padCollision(int state)
    {
        if (vy<0) return;
        double vx_ = vx, normal=PI/4*state;
        vx = (vx_*cos(normal) + vy*sin(normal));
        vy = (vx_*sin(normal) - vy*cos(normal));
    }

    double pos(double a)
    {
        if(a<0) return -a;
        return a;
    }
    double mininum(double a, double b)
    {
        if(a>0) return min(a,pos(b));
        else return -min(-a,pos(b));
    }
}
