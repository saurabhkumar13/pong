package thefallen.pong;

/**
 * Handles the creation and rendering of a game board
 */

import static java.lang.Integer.min;
import static java.lang.Math.*;
import static java.lang.System.err;
import static java.lang.System.out;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.awt.*;
import java.awt.Font;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.tools.Tool;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.*;
import org.jdesktop.core.animation.rendering.JRenderer;
import org.jdesktop.core.animation.rendering.JRendererTarget;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.Interpolator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingSource.TickListener;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.core.animation.timing.interpolators.SplineInterpolator;
import org.jdesktop.swing.animation.rendering.JRendererFactory;
import org.jdesktop.swing.animation.rendering.JRendererPanel;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;
import org.json.JSONObject;
import sun.rmi.runtime.Log;

public class pong implements JRendererTarget<GraphicsConfiguration, Graphics2D> {

    // Basic variable declarations and initialisations

    static final TimingSource f_infoTimer = new SwingTimerTimingSource(1, SECONDS);
    final JFrame f_frame;
    final JRendererPanel f_panel;
    final JRenderer f_renderer;
    final JLabel f_infoLabel;
    final Racket[] rackets;
    final Polygon base;
    static  pong game;
    final int N,N_;
    Ball ball;
    Ball.onDiedListener onDiedListener;
    SinglePlayer lol;
    ping master;
    static int diff = 5;

    public static void main(String args[]){
        System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                game = new pong(2,diff,false);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        game.addBall(null);
                        game.ball.vx = 2;
                        game.ball.vy = 2;
                        game.rackets[0].setPowerup(Misc.Avatar.FIRE);
                    }});
            }
        });
    }
    final double r;
    final Point2D center;

    public pong(int n,int dif,boolean F) {

        f_frame = new JFrame("ping pong ");
        f_frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        f_frame.setResizable(false);

        f_frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                f_infoTimer.dispose();
                f_renderer.getTimingSource().dispose();
                f_renderer.shutdown();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                if (master != null)
                    master.Stop();

            }
        });

        diff = dif;

        f_frame.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        f_frame.add(topPanel, BorderLayout.NORTH);
        topPanel.setLayout(new BorderLayout());
        f_infoLabel = new JLabel();
        f_frame.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                rackets[0].pressed(e.getKeyCode());

            }

            @Override
            public void keyReleased(KeyEvent e) {
                rackets[0].released(e.getKeyCode());

            }
        });

        f_panel = new JRendererPanel();
        f_frame.add(f_panel, BorderLayout.CENTER);
//        f_panel.setPreferredSize(new Dimension(800,600));
        f_frame.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        if (F)
        {
            f_frame.setUndecorated(true);
//        f_frame.setExtendedState(f_frame.getExtendedState() | JFrame.MAXIMIZED_HORIZ);
        }
        f_renderer = JRendererFactory.getDefaultRenderer(f_panel, this, false);

        f_infoTimer.addTickListener(new TickListener() {
            @Override
            public void timingSourceTick(TimingSource source, long nanoTime) {
                updateBallCount();
            }
        });
        f_infoTimer.init();

        f_frame.pack();
        f_frame.setVisible(true);
        if(n==2) N=4;
        else N=n;
        N_=n;
        rackets = new Racket[N_];
        base = new Polygon();
        base.npoints=N+1;
        r=400;
        center = new Point(f_frame.getWidth()/2,f_frame.getHeight()/2);
        System.out.println(f_frame.getHeight());
        System.out.println(f_frame.getWidth());

        for(int i=0;i<N_;i++) {

            rackets[i] = new Racket(diff);
            rackets[i].n = i;
            rackets[i].N = N;
            rackets[i].center = center;
            rackets[i].width = (int) (r * sin(PI / N) / 2);

            final Racket racket = rackets[i];
            final int index = i;

            final TimingTarget circularMovement = new TimingTargetAdapter() {
                @Override
                public void timingEvent(Animator source, double fraction) {
                    racket.update(index);
                }
            };
            rackets[0].sentient = false;

            racket.animator = new Animator.Builder().setDuration(4, SECONDS).addTarget(circularMovement)
                    .setRepeatCount(Animator.INFINITE).setRepeatBehavior(Animator.RepeatBehavior.LOOP).build();
            racket.animator.start();

        }

        rackets[0].user=true;
        rackets[0].sentient=false;
        setupBase();
        addBall(null);
    }

    void setupBase()
    {
        double th=PI,x,y;
        int n = base.npoints-1;
        int[] xpoints=new int[n+1],ypoints=new int[n+1];
        if(n%2==0) th-=PI/n;
        for(int i=0;i<=n;i++)
        {
            x=r*sin(th);
            y=r*cos(th);
            xpoints[i]=(int)x;
            ypoints[i]=(int)y;
            th+=2*PI/n;
        }
        out.println(r);
        base.xpoints=xpoints;
        base.ypoints=ypoints;
        base.translate(f_frame.getWidth()/2,f_frame.getHeight()/2);
        for (Racket racket : rackets)
        {
            racket.initx = base.xpoints[N/ 2];
            racket.inity = base.ypoints[N/ 2] - racket.height;
            racket.frame = (int)(2*r*sin(PI/n));
        }
    }

    void updateBallCount() {
        f_infoLabel.setText("    FPS: " + f_renderer.getFPS());
        f_frame.validate();
    }

  /*
   * Renderer thread methods and state (may be the EDT if passive rendering is
   * being used).
   */

    private final Random f_die = new Random();
    private BufferedImage[] f_ballImages;


    private static final Interpolator ACCEL_4_4 = new AccelerationInterpolator(0.4, 0.4);
    private static final Interpolator SPLINE_0_1_1_0 = new SplineInterpolator(0.00, 1.00, 1.00, 1.00);
    private static final Interpolator SPLINE_1_0_1_1 = new SplineInterpolator(1.00, 0.00, 1.00, 1.00);
    JSONObject initBALLproperties = null;

    void pause()
    {
        ball.dt = 0;
        rackets[0].dt=0;
    }
    boolean paused()
    {
        return ball.dt == 0;
    }

    void resume()
    {
        ball.dt = 1;
        rackets[0].dt = 0.5f;
    }


    void addBall(ping master2)
    {
        ball = new Ball(diff);
        ball.lol = lol;

        for(int i=0;i<N_;i++)
            rackets[i].ball=ball;

        out.println("ball added "+(onDiedListener==null));

        ball.rackets=rackets;
        ball.imageIndex = f_die.nextInt(5);
        ball.base=base;
        ball.center=center;
        ball.N=N;

        if(N_==2) ball.twoP=true;

        ball.omega=0.1;
        ball.setX(center.getX());
        ball.setY(center.getY());

        final int duration = 4;

        final TimingTarget circularMovement = new TimingTargetAdapter() {
            @Override
            public void timingEvent(Animator source, double fraction) {
                if(ball!=null)
                ball.update();
            }
        };

        /*
         * Sometimes go at a constant rate, sometimes accelerate and decelerate.
         */

        final Interpolator i = f_die.nextBoolean() ? ACCEL_4_4 : null;

        ball.animator = new Animator.Builder().setDuration(duration, SECONDS).addTarget(circularMovement)
                .setRepeatCount(Animator.INFINITE).setRepeatBehavior(Animator.RepeatBehavior.LOOP).setInterpolator(i).build();
        ball.animator.start();

        ball.master=master2;
        if(master2!=null)
            master2.broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.BallReady)).toString());
    }


    @Override
    public void renderSetup(GraphicsConfiguration gc) {
    }

    @Override
    public void renderUpdate() {
        // Nothing to do
    }

    float scale = 1.5f;

    @Override
    public void render(Graphics2D g2d, int width, int height) {

        g2d.setBackground(Color.black);
        g2d.clearRect(0, 0, width, height);
        g2d.setStroke( new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                new float[] { 3, 1 }, 0));
        g2d.setPaint(Color.gray);


        g2d.fillPolygon(base);
        g2d.setPaint(Color.black);
        double th = 0;

        g2d.setColor(Color.white);

        g2d.setFont(new Font("Tahoma", Font.BOLD, 12));
        switch(diff) {

            case 1 : g2d.drawString("DIFFICULTY : EASY", 50, height/10);
                break;
            case 2 : g2d.drawString("DIFFICULTY : MEDIUM",50, height/10);
                break;
            case 3 : g2d.drawString("DIFFICULTY : HARD",50, height/10);
                break;
            case 4 : g2d.drawString("DIFFICULTY : HARDER", 50, height/10);
                break;
            case 5 : g2d.drawString("DIFFICULTY : HARDEST", 50, height/10);
                break;
            default: break;
        }

        for(int i=0;i<rackets.length;i++)
        {
            Rectangle pad = new Rectangle();
            Rectangle pad2 = new Rectangle();
            Rectangle pad3 = new Rectangle();
            pad.setRect(rackets[i].x + base.xpoints[N/ 2], rackets[i].y + base.ypoints[N/ 2] - 2 * rackets[i].height, rackets[i].width, 2*rackets[i].height);
            pad2.setRect(rackets[i].x + base.xpoints[N/ 2] + 5, rackets[i].y + base.ypoints[N/ 2] - rackets[i].height, rackets[i].width*(rackets[i].hp+20)*9/(10*(rackets[i].hp_max+20)), rackets[i].height/2);
            pad3.setRect(rackets[i].x+ base.xpoints[N/ 2], rackets[i].y + base.ypoints[N/ 2] - 2 * rackets[i].height, rackets[i].width, 3*rackets[i].height);

            // Decide Color of the paddle based on the powerup

            if(rackets[i].form != null)
            {
                switch(rackets[i].form)
                {
                    case EARTH : g2d.setPaint(Color.decode("0x006400"));
                                break;
                    case WIND : g2d.setPaint(Color.decode("0xadd8e6"));
                                break;
                    case FIRE : g2d.setPaint(Color.decode("0xce2029"));
                                break;
                    case WATER : g2d.setPaint(Color.decode("0x000080"));
                                break;
                    default: break;
                }
            }
            else
            {
                g2d.setPaint(Color.black);
            }

            Shape s = (AffineTransform.getRotateInstance(
                    PI/20*rackets[i].state, pad.getCenterX(), pad.getCenterY())
                    .createTransformedShape(pad));

            s = (AffineTransform.getRotateInstance(
                    th, center.getX(), center.getY())
                    .createTransformedShape(s));


            Shape s2 = (AffineTransform.getRotateInstance(
                    PI/20*rackets[i].state, pad.getCenterX(), pad.getCenterY())
                    .createTransformedShape(pad2));

            s2 = (AffineTransform.getRotateInstance(
                    th, center.getX(), center.getY())
                    .createTransformedShape(s2));

            Shape s3 = (AffineTransform.getRotateInstance(
                    PI/20*rackets[i].state, pad.getCenterX(), pad.getCenterY())
                    .createTransformedShape(pad3));

            s3 = (AffineTransform.getRotateInstance(
                    th, center.getX(), center.getY())
                    .createTransformedShape(s3));

            if(s3.contains(ball.getX(),ball.getY()))
            {
                ball.padCollision(rackets[i].state,i);
                rackets[i].safe = true;
            }
            else
            {
                rackets[i].safe = false;
            }

            if(rackets[i].diedOnce) {
                g2d.setPaint(Color.red);
                rackets[i].diedOnce=false;
            }

            g2d.fill(s);
            if(rackets[i].form != null)
            {
                switch(rackets[i].form)
                {
                    case EARTH : g2d.setPaint(Color.black);
                        break;
                    case WIND : g2d.setPaint(Color.black);
                        break;
                    case FIRE : g2d.setPaint(Color.lightGray);
                        break;
                    case WATER : g2d.setPaint(Color.lightGray);
                        break;
                    default: break;
                }
            }
            else
            {
                g2d.setPaint(Color.lightGray);
            }

            g2d.fill(s2);
            th -= 2 * PI  / N_;
        }

        g2d.setPaint(Color.darkGray);


//            g2d.drawOval((int)ball.getX()-25, (int)ball.getY()-25,50,50);
            g2d.fillOval((int)ball.getX()-5, (int)ball.getY()-5,10,10);
//            g2d.setPaint(Color.white);
//            g2d.fillOval((int)ball.getX()+(int)(25*cos(ball.theta))-5, (int)ball.getY()+(int)(25*sin(ball.theta))-5,10,10);
//            g2d.setPaint(new Color(ball.gravity/2+0.5f,ball.gravity/2+0.5f,ball.gravity/2+0.5f));
//            g2d.fillOval((int)center.getX()-5, (int)center.getY()-5,10,10);

        }

    @Override
    public void renderShutdown() {
        // Nothing to do
    }


}

