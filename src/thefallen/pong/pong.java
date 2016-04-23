package thefallen.pong;
import static java.lang.Integer.min;
import static java.lang.Math.*;
import static java.lang.System.err;
import static java.lang.System.out;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
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

/**
 * This demonstration is a variant of the demonstration by Chet Haase at JavaOne
 * 2008. Chet discussed the problem in the original Timing Framework where, by
 * default, each animation used its own {@code javax.swing.Timer}. This did not
 * scale and, as balls were added to the demonstration, the multiple timers
 * caused noticeable performance problems.
 * <p>
 * This version of the Timing Framework allows setting a default timer on the
 * {@link , thus making it easy for client code to avoid this
 * problem. This design was inspired by the JavaOne 2008 talk.
 * <p>
 * By default this program uses passive rendering. To use active rendering set
 * the <tt>org.jdesktop.renderer.active</tt> system property to any value. For
 * example place <tt>-Dorg.jdesktop.renderer.active=true</tt> on the java
 * command line.
 *
 * @author Tim Halloran
 */
public class pong implements JRendererTarget<GraphicsConfiguration, Graphics2D> {

    /**
     * Used to update the FPS display once a second.
     */
    static final TimingSource f_infoTimer = new SwingTimerTimingSource(1, SECONDS);

  /*
   * EDT methods and state
   */

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
    double padding = 0.05;
    SinglePlayer lol;
    ping master;
    static int diff = 1;

    public static void main(String args[]){
        System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                game = new pong(7,diff);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        game.addBall();
                        game.ball.vx = 3 + diff;
                    }});
            }
        });
    }
    final double r;
    final Point2D center;

    public pong(int n,int dif) {

        final String rendererType = JRendererFactory.useActiveRenderer() ? "Active" : "Passive";
        f_frame = new JFrame("ping pong " + rendererType + " Rendering");
        f_frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        f_frame.setResizable(false);

        f_frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                f_infoTimer.dispose();
                f_renderer.getTimingSource().dispose();
                f_renderer.shutdown();
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
        f_panel.setPreferredSize(new Dimension(800,600));
//        f_panel.setPreferredSize(new Dimension(1366 ,768));
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
        double scale=2;

        if(n%2==1)
        {
            scale = 1;//+ .5 * exp((-n + 3)*10);
        }
        else if (n>2)
        {
            scale = 1;//+ exp((-n + 4)*20);
        }
        else if (n == 2)
        {
            scale = 1;
        }

        r=(floor(min(f_frame.getHeight(),f_frame.getWidth())/200)*100-50)*scale;
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
        addBall();
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


    void addBall() {
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
        if(master!=null)
            master.broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.BallReady)).toString());
    }


    @Override
    public void renderSetup(GraphicsConfiguration gc) {
        f_ballImages = new BufferedImage[thefallen.pong.Resources.SPHERES.length];
        int index = 0;
        for (String resourceName : thefallen.pong.Resources.SPHERES) {
            try {
                f_ballImages[index++] = ImageIO.read(thefallen.pong.Resources.getResource(thefallen.pong.Resources.PONG_SPHERE));
            } catch (IOException e) {
                throw new IllegalStateException("Unable to load image: " + resourceName, e);
            }
        }
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

        for(int i=0;i<rackets.length;i++)
        {
            Rectangle pad = new Rectangle();
            Rectangle pad2 = new Rectangle();
            Rectangle pad3 = new Rectangle();
            pad.setRect(rackets[i].x + base.xpoints[N/ 2], rackets[i].y + base.ypoints[N/ 2] - rackets[i].height, rackets[i].width, 2*rackets[i].height);
//            pad3.setRect(rackets[i].x+ base.xpoints[N/ 2] - (padding/2) * rackets[i].width, rackets[i].y + base.ypoints[N/ 2] - rackets[i].height, rackets[i].width*(1+padding), 2*rackets[i].height*(1+padding/2));
            pad2.setRect(rackets[i].x + base.xpoints[N/ 2] + 5, rackets[i].y + base.ypoints[N/ 2] - rackets[i].height/2, rackets[i].width*(rackets[i].hp+20)*9/1000, rackets[i].height/2);
            g2d.setPaint(Color.black);

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
//
//            Shape s3 = (AffineTransform.getRotateInstance(
//                    PI/20*rackets[i].state, pad.getCenterX(), pad.getCenterY())
//                    .createTransformedShape(pad3));
//
//            s3 = (AffineTransform.getRotateInstance(
//                    th, center.getX(), center.getY())
//                    .createTransformedShape(s3));

            if(lol != null) {
                g2d.setColor(Color.white);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2d.drawString("TIMER BITCH!!", 600, 100);
                if (lol.time != null) {
                    g2d.drawString(lol.time, 600, 120);
                }
            }

            g2d.setPaint(Color.black);

            if(s.contains(ball.getX(),ball.getY()))
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
            g2d.setPaint(Color.lightGray);
            g2d.fill(s2);
            Point2D ballpos = new Point((int)ball.x,(int)ball.y);
            AffineTransform.getRotateInstance(
                    2*PI*i/(N), center.getX(), center.getY())
                    .transform(ballpos,ballpos);
            g2d.fillOval((int)ballpos.getX(),(int)ballpos.getY(),5,5);
//            out.println("ping"+i+" "+(2*PI*i/(N))+" "+ballpos.getX()+" "+ballpos.getY());
            th-=2*PI*N/(N_*N);

        }

        g2d.setPaint(Color.darkGray);


            g2d.drawOval((int)ball.getX()-25, (int)ball.getY()-25,50,50);
            g2d.fillOval((int)ball.getX()-5, (int)ball.getY()-5,10,10);
//            g2d.setPaint(Color.white);
//            g2d.fillOval((int)ball.getX()+(int)(25*cos(ball.theta))-5, (int)ball.getY()+(int)(25*sin(ball.theta))-5,10,10);
//            g2d.setPaint(new Color(ball.gravity/2+0.5f,ball.gravity/2+0.5f,ball.gravity/2+0.5f));
            g2d.fillOval((int)center.getX()-5, (int)center.getY()-5,10,10);

        }

    @Override
    public void renderShutdown() {
        // Nothing to do
    }


}

