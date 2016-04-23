package thefallen.pong;


import com.sun.javafx.geom.Vec2d;
import org.jdesktop.core.animation.timing.Animator;
import org.json.JSONObject;

import java.awt.*;
import java.awt.geom.Point2D;

import static java.lang.Math.*;
import static java.lang.System.err;
import static java.lang.System.out;

public class Ball {
    double x, y,vx,vy,ax,ay,omega,theta,mu=0.3,r=1;
    int imageIndex,frameH,frameW;
    float dt=1,ddt=0.001f;
    Animator animator;
    Polygon base;
    Point2D center;
    Racket[] rackets;
    int N,diff;
    boolean isGdecreasing=true,twoP;
    float gravity=0;
    onDiedListener diedListener;
    SinglePlayer lol;
    ping master;

    Ball(int dif)
    {
        diff = dif;

    }

    void setVel()
    {
        vx = 3+diff;
        vy = -(3+diff);
    }

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
        void onDied(int index,SinglePlayer lol);
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
        if(!base.contains(x+vx*dt,y+vy*dt))
        {
            int n=getSideofPolygon();
            if(twoP)
            {
                if((n==1 && vx > 0) || (n==3 && vx <0))
                    {
                        vx = -vx;
                        return;
                    }

                n/=2;
            }

            if (!rackets[n].safe&&(rackets[n].sentient||rackets[n].user))
            {
                out.println(n+" "+rackets[n].hp);
                if (rackets[n].hp > 0){
                    err.println(n+" "+(master==null));
                    if (n == 0 && master != null) {
                        master.broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.SyncHP).accumulate("HP",rackets[n].hp-20)).toString());
                    }
                    rackets[n].hp -= 20;
                }
                else if(diedListener!=null) diedListener.onDied(n,lol);
                rackets[n].diedOnce = true;
            }
//            else
//                rackets[n].safe=false;

            double angleInRadians = Math.atan2(-vy,vx);
            double normal = 2*PI*n/N-PI/2;
            if(twoP) normal = 2*PI*2/N-PI/2;
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
        x += vx * dt;
        y += vy * dt;

    }

    void padCollision(int state,int i) {
        double vy_ = vy, vx_ = vx;
        double theta = (2 * PI * i) / N - (PI * state) / 8;
        double temp = (vx_ * sin(theta) + vy_ * cos(theta));
        double alpha = atan2(-vy_, vx_);
        double delta = 2 * (theta - alpha);

        if (temp < 0) return;
        if(!rackets[i].sentient&&!rackets[i].user) return;
        vx = vx_ * cos(delta) + vy_ * sin(delta);
        vy = vy_ * cos(delta) - vx_ * sin(delta);
        if(i==0&&master!=null)
            master.broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.SyncBall)
            .accumulate("vx",vx)
            .accumulate("vy",vy)
            .accumulate("x",x)
            .accumulate("y",y)
            ).toString());
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
