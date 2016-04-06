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
import java.awt.image.BufferedImage;
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
    final int N,N_;
    public static void main(String args[]){
        System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                pong game = new pong(3);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        game.addBall();}});
            }
        });
    }
    final double r;
    final Point2D center;
    public pong(int n) {
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
        f_frame.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        f_frame.add(topPanel, BorderLayout.NORTH);
        topPanel.setLayout(new BorderLayout());
        f_infoLabel = new JLabel();
        topPanel.add(f_infoLabel, BorderLayout.EAST);
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
        f_panel.setBackground(Color.white);
        f_panel.setPreferredSize(new Dimension(800 ,600));
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
        r=floor(min(f_frame.getHeight(),f_frame.getWidth())/200)*100-50;
        center = new Point(f_frame.getWidth()/2,f_frame.getHeight()/2);
        for(int i=0;i<N_;i++) {
            rackets[i] = new Racket();
            rackets[i].width = (int) (r * sin(PI / N) / 2);
            final Racket racket =rackets[i];
            final TimingTarget circularMovement = new TimingTargetAdapter() {
                @Override
                public void timingEvent(Animator source, double fraction) {
                    racket.update();
                }
            };
            racket.animator = new Animator.Builder().setDuration(4, SECONDS).addTarget(circularMovement)
                    .setRepeatCount(Animator.INFINITE).setRepeatBehavior(Animator.RepeatBehavior.LOOP).build();
            racket.animator.start();

        }
        setupBase();
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
        base.xpoints=xpoints;
        base.ypoints=ypoints;
        base.translate(f_frame.getWidth()/2,f_frame.getHeight()/2);
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

//    z
    void addBall() {
        final Ball ball = new Ball();
        ball.imageIndex = f_die.nextInt(5);
        ball.base=base;
        ball.center=center;
        ball.N=N;
        BufferedImage ballImage = f_ballImages[ball.imageIndex];
        if (initBALLproperties==null)
        {
            ball.setX(center.getX());
            ball.setY(center.getY());
            ball.vx=f_die.nextInt(10);
            ball.vy=-f_die.nextInt(10);
        }
        else
        {
            ball.setX(initBALLproperties.getInt("x"));
            ball.setY(initBALLproperties.getInt("y"));
            ball.vx=initBALLproperties.getInt("vx");
            ball.vy=initBALLproperties.getInt("vy");
        }
        err.println("init: "+initBALLproperties);
        ball.frameW = f_panel.getWidth() - ballImage.getWidth();
        ball.frameH = f_panel.getHeight() - ballImage.getHeight();

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

        f_balls.add(ball);
    }

    void removeBall() {
        if (f_balls.isEmpty())
            return;

        Ball ball = f_balls.remove(0);
        if (ball != null) {
            ball.animator.stop();
        }
    }

    final List<Ball> f_balls = new ArrayList<>();
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
            if(i==1)
                g2d.setPaint(Color.green);
            else
            g2d.setPaint(Color.black);
            pad.setRect(rackets[i].getX() + base.xpoints[N/ 2], rackets[i].getY() + base.ypoints[N/ 2] - rackets[i].height, rackets[i].width, 2*rackets[i].height);
            g2d.fill(AffineTransform.getRotateInstance(
                    th, center.getX(), center.getY())
                    .createTransformedShape(pad));
            th-=2*PI*N/(N_*N);
//            g2d.fill(pad);
        }
        g2d.setPaint(Color.darkGray);

        for (Ball ball : f_balls) {
            g2d.drawOval((int)ball.getX()-25, (int)ball.getY()-25,50,50);
            g2d.fillOval((int)ball.getX()-5, (int)ball.getY()-5,10,10);
        }    }

    @Override
    public void renderShutdown() {
        // Nothing to do
    }


}

