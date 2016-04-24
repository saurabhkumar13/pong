package thefallen.pong;


import org.jdesktop.core.animation.timing.Animator;
import org.json.JSONObject;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Random;

import static java.lang.Math.*;
import static java.lang.System.err;
import static java.lang.System.out;

public class Racket {

    int x, y;

    ping master;

    boolean sentient = true, safe = false, user = false, diedOnce, decided = true;

    float v, a, dt=.5f, f=.2f, speed=20, ai_speed=10, e = 1;

    int initx,inity,frame,num,hp_dec = 20,hp_max = 80;

    int width = 100, height = 10, state = 0, hp = 80, n, N;

    double bvx, bvy;

    int difficulty = 1;

    Animator animator;

    Ball ball;

    Point2D center;

    Misc.Avatar form;

    public Racket(int dif)
    {
        difficulty = dif;
    }

    // SEts values of parameters according to the powerup assigned
    public void setPowerup(Misc.Avatar id) {


        form = id;

        switch (id)
        {
            case EARTH : hp += 20;
                        hp_max += 20;
                        break;
            case WIND : speed = 30;
                        break;
            case FIRE : e = 1.5f;
                        break;
            case WATER : width = 150;
                        speed = 18;
                        e = 0.8f;
                        break;
            default: break;
        }

    }

    public void update(int index){

        v += a * dt;

        if(ball != null)
        {
            if(sentient)
            {
                Point2D ballpos = new Point((int)ball.x,(int)ball.y);
                ai_speed = 10 + 2*difficulty;

                if(ball.twoP)
                    num = 2;
                else
                    num = N;

                double vx_ = ball.vx;
                double vy_ = ball.vy;
                double theta = 2 * PI * index / num;

                bvx = vx_ * cos(theta) - vy_ * sin(theta);
                bvy = vx_ * sin(theta) + vy_ * cos(theta);

                AffineTransform.getRotateInstance(
                        2 * PI * n / (num), center.getX(), center.getY())
                        .transform(ballpos, ballpos);

                int dir_x = (int) ballpos.getX() - initx - width / 2;//-200-40*(N-3);
                int dir_y = (int) ballpos.getY();

                Random rand = new Random();
                int t1 = 2;

                if (dir_x - x > width * t1 / 20)
                    dir_x = 1;

                else if (dir_x - x < - width * t1 / 20)
                    dir_x = -1;

                else dir_x = 0;

                if(dir_y - inity + 2 * bvy > - (20 + difficulty * 30))
                {
                    x += ai_speed * (1+(float)(difficulty/15)) * dir_x * dt;
                }

                int t2 = rand.nextInt(30);

                if(dir_y - inity + 2 * bvy > - 40)
                {
                    if(!decided)
                    {
                        if(t2 % (8 - difficulty) == 0) state = 1;
                        else if (t2 % (8 - difficulty) == 1) state = -1;
                        else state = 0;
                        decided = true;
                    }
                }
                else
                {
                    decided = false;
                    state = 0;
                }

                if (x + width > frame)
                    x = frame - width;
                else if (x < 0)
                    x = 0;
            }

            else

            {
                if (x + v * dt > frame - width) x = frame - width;
                else if (x + v * dt < 0) x = 0;
                else x += v * dt;
            }

        }
    }

    public void pressed(int e) {
        err.println(e+" pressed");
        if (e == KeyMap.left && v != -speed) {
            v = -speed;
            if (master != null) {
                master.broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.ACTION).accumulate("action",Misc.Command.L).accumulate("x",x)).toString());
            }
        } else if (e == KeyMap.right && v != speed) {
            v = speed;
            if (master != null) {
                master.broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.ACTION).accumulate("action",Misc.Command.R).accumulate("x",x)).toString());
            }
        }
        if (e == KeyMap.tiltLeft && state!=1){
            state = 1;
            if (master != null) {
                master.broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.ACTION).accumulate("action",Misc.Command.LT).accumulate("x",x)).toString());
            }
        }
        else if(e==KeyMap.tiltRight && state!=-1){
            state=-1;
            if (master != null) {
                master.broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.ACTION).accumulate("action",Misc.Command.RT).accumulate("x",x)).toString());
            }
        }

    }

    public void typed(int e)
    {

    }

    public void released(int e)
    {
        err.println(e+" released");
        if (master!=null&&(v!=0||state!=0)) {
            if(e==KeyMap.tiltLeft||e==KeyMap.tiltRight)
                master.broadcastToGroup((new JSONObject().accumulate("command", Misc.Command.ACTION).accumulate("action", Misc.Command.ReleaseKeyT).accumulate("x",x)).toString());
            else if(e==KeyMap.left||e==KeyMap.right)
                master.broadcastToGroup((new JSONObject().accumulate("command", Misc.Command.ACTION).accumulate("action", Misc.Command.ReleaseKeyV).accumulate("x",x)).toString());

        }
        if(e==KeyMap.tiltLeft||e==KeyMap.tiltRight)
            state=0;
        else if(e==KeyMap.left||e==KeyMap.right)
                v=0;
    }
}
