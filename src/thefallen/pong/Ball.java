package thefallen.pong;

/**
 * Handles everything related to changes in the ball from the rendering at position to the motion and collisions
 */

import org.jdesktop.core.animation.timing.Animator;
import org.json.JSONObject;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

import static java.lang.Math.*;
import static java.lang.System.err;

public class Ball {

    // Basic Global variable Declarations

    double x, y, vx, vy, ax, ay, omega, theta, mu = 0.3, r = 1;
    int imageIndex;
    float dt = 1, ddt = 0.0f;
    Animator animator;
    Polygon base;
    Point2D center;
    Racket[] rackets;
    int N, diff;
    private boolean isGDecreasing = true;
    boolean twoP;
    float gravity = 0;
    onDiedListener diedListener;
    SinglePlayer lol;
    ping master;


    /*
        name : Ball
        input : dif - int
        output : void
        function : Constructor
     */

    Ball(int dif)
    {
        diff = dif;
    }


    void setGravity()
    {
        gravity=1;
        if(ddt==0)
            ddt=0.01f;
        else
            ddt=0;
    }

    public static void getVel()
    {
        Random r = new Random();
        int lel = r.nextInt(19)+1;
        if(lel == 10) lel = 9;
        double v = 12;
        double theta = 2 * PI * lel / 20;
        Misc.INITballvx = v * cos(theta);
        Misc.INITballvy = v * sin(theta);
    }
    /*
        name : setVel
        input : void
        output : void
        function : randomly initialises the direction of the velocity of the ball
     */

    public void setVel()
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

    /*
        name : getSideOfPolygon
        input : void
        output : int
        function : returns the sector of the polygon in which the ball currently is
     */

    public int getSideOfPolygon()
    {
        double angleInRadians = Math.atan2(center.getY() - y, x - center.getX());
        int n;

        angleInRadians -= PI/2;

        if (N % 2 == 0) angleInRadians += PI / N;

        if(angleInRadians<0) angleInRadians+=2*PI;

        n = (int) (N * angleInRadians / (2 * PI));

        n += (N + 1) / 2;
        n %= N;

        return n;
    }

    /*
        name : onDiedListener
        input : index - int
        output : void
        function : Listener that can be called once a racket has died
     */

    interface onDiedListener{
        void onDied(int index);
    }

    /*
        name : update
        input : void
        output : void
        function : calculates the next position of the ball and also determines whether a racket will lose health
     */

    public void update(){

        vx += ax * dt;
        vy += ay * dt;
        theta += omega * dt * 0.05;

        double theta = -Math.atan2(y - center.getY(), x - center.getX()), f = 0.9;

        if (gravity + ddt > 1) isGDecreasing = true;

        else if (gravity - ddt < -1) isGDecreasing = false;

        if (isGDecreasing) gravity -= ddt;

        else gravity += ddt;

        ay = -1 * gravity * 0.1 * sin(theta);
        ax = gravity * 0.1 * cos(theta);

        double modV = vx * vx + vy * vy;

        if(modV>500) {
            vx *= f;
            vy *= f;
        }

        if (!base.contains(x + vx * dt, y + vy * dt)) {

            int n = getSideOfPolygon();

            if (twoP) {
                if ((n == 1 && vx > 0) || (n == 3 && vx < 0)) {

                    vx = -vx;
                    return;
                }

                n /= 2;
            }


            if (!rackets[n].safe && (rackets[n].sentient || rackets[n].user)) {

                if (rackets[n].hp >= 0) {

                    if (n == 0 && master != null) {

                        master.broadcastToGroup((new JSONObject().accumulate("command", Misc.Command.SyncHP).accumulate("HP", rackets[n].hp - 20)).toString());
                    }

                    rackets[n].hp -= rackets[n].hp_dec;

                } else if (diedListener != null) {

                    diedListener.onDied(n);
                    rackets[n].diedOnce = true;

                } else {
                    if(n==0&&master!=null)
                        master.broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.Died)).toString());
                    err.println("dead " + n);
                }

            }

            double angleInRadians = Math.atan2(-vy, vx);
            double normal = 2 * PI * n / N - PI / 2;

            if (twoP) normal = 2 * PI * 2 / N - PI / 2;

//            double toRotate = 2 * (angleInRadians - normal) + PI;
            double vn, vt, vn_, vt_;

            if (angleInRadians - normal < PI / 2) {

                vn = vx * cos(normal) - vy * sin(normal);
                vt = vx * sin(normal) + vy * cos(normal);
                vn_ = -vn;
                vt_ = vt;
                omega = omega + mininum(vt - omega * r, 2 * mu * vn) / r;
                vx = (vt_ * sin(normal) + vn_ * cos(normal));
                vy = (vt_ * cos(normal) - vn_ * sin(normal));
            }
        }

        x += vx * dt;
        y += vy * dt;

    }

    /*
        name : padCollision
        input : state - int, i - int
        output : void
        function : determines the ball's velocity after collision with a paddle
     */

    public void padCollision(int state,int i) {

        int num;
        if (twoP) num = 2;
        else num = N;

        double vy_ = vy, vx_ = vx;
        double theta = (2 * PI * i) / num - (3 * PI * state) / 10;
        double temp = (vx_ * sin(theta) + vy_ * cos(theta));
        double alpha = atan2(-vy_, vx_);
        double delta = 2 * (theta - alpha);

        if (temp < 0) return;

        if (!rackets[i].sentient && !rackets[i].user) return;

        if (num == 2 || i % 2 == 0) {

            vx = vx_ * cos(delta) + vy_ * sin(delta);
            vy = rackets[i].e * (vy_ * cos(delta) - vx_ * sin(delta));
        } else {

            vx = rackets[i].e * (vx_ * cos(delta) + vy_ * sin(delta));
            vy = vy_ * cos(delta) - vx_ * sin(delta);
        }

        if (i == 0 && master != null){

            Misc.pop2();
            master.broadcastToGroup((new JSONObject().accumulate("command", Misc.Command.SyncBall)
                    .accumulate("vx", vx)
                    .accumulate("vy", vy)
                    .accumulate("x", x)
                    .accumulate("y", y)
            ).toString());
        } else if(master==null) Misc.pop2();

    }

    /*
        name : pos
        input : a - double
        output : double
        function : returns mode of double value
     */

    public double pos(double a)
    {
        if(a<0) return -a;
        return a;
    }

    /*
        name : minimum
        input : a - double, b - double
        output : double
        function : returns the minimum absolute value of the two inputs
     */

    public double mininum(double a, double b)
    {
        if(a>0) return min(a,pos(b));
        else return -min(-a,pos(b));
    }
}
