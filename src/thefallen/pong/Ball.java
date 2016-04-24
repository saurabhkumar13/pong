package thefallen.pong;


import com.sun.javafx.geom.Vec2d;
import org.jdesktop.core.animation.timing.Animator;
import org.json.JSONObject;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

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
        Random r = new Random();
        int lel = r.nextInt(19)+1;
        if(lel == 10) lel = 9;
        double v = 10 + diff;
        double theta = 2 * PI * lel / 20;
        vx = v * cos(theta);
        vy = v * sin(theta);

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

        if(modv>100) {vx*=f;vy*=f;}

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

//            out.println("Condition of the Racket "+n+" "+rackets[n].safe);

            if (!rackets[n].safe&&(rackets[n].sentient||rackets[n].user))
            {
//                out.println(n+" "+rackets[n].hp);

                if (rackets[n].hp >= 0)
                {
//                    err.println(n+" "+(master==null));
                    if (n == 0 && master != null)
                    {
                        master.broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.SyncHP).accumulate("HP",rackets[n].hp-20)).toString());
                    }
                    rackets[n].hp -= rackets[n].hp_dec;
                }

                else if(diedListener!=null)
                {

                    diedListener.onDied(n, lol);
                    rackets[n].diedOnce = true;
                }
                else
                {
                    err.println("dead "+n);
                }

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
                vn = vx*cos(normal)-vy*sin(normal);
                vt = vx*sin(normal)+vy*cos(normal);
                vn_=-vn;
                vt_ = vt;
                  omega = omega + mininum(vt-omega*r,2*mu*vn)/r;
                vx = (vt_*sin(normal) + vn_*cos(normal));
                vy = (vt_*cos(normal) - vn_*sin(normal));
            }
        }

        x += vx * dt;
        y += vy * dt;

    }

    void padCollision(int state,int i) {
        int num;
        if(twoP) num = 2;
        else num = N;

        double vy_ = vy, vx_ = vx;
        double theta = (2 * PI * i) / num - (3 * PI * state) / 10;
        double temp = (vx_ * sin(theta) + vy_ * cos(theta));
        double alpha = atan2(-vy_, vx_);
        double delta = 2 * (theta - alpha);

//        Misc.pop2();

        if (temp < 0) return;

        if(!rackets[i].sentient&&!rackets[i].user) return;
        if(num == 2 || i%2 == 0)
        {
            vx = vx_ * cos(delta) + vy_ * sin(delta);
            vy = rackets[i].e * (vy_ * cos(delta) - vx_ * sin(delta));
        }
        else
        {
            vx = rackets[i].e * (vx_ * cos(delta) + vy_ * sin(delta));
            vy = vy_ * cos(delta) - vx_ * sin(delta);
        }
//        err.println(i+" "+N+state+" "+(vx_-vx)+" "+(vy_-vy));

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
