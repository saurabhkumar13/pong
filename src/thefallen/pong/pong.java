package thefallen.pong;
import static java.lang.System.err;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.core.animation.rendering.JRenderer;
import org.jdesktop.core.animation.rendering.JRendererTarget;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.Interpolator;
import org.jdesktop.core.animation.timing.KeyFrames;
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
    int f_ballCount = 0;
    final ping f_ping;
    final Racket r1;
    public Racket r2;
    public Racket r3;
    public Racket r4;

    public pong(ping master) {
        final String rendererType = JRendererFactory.useActiveRenderer() ? "Active" : "Passive";
        f_frame = new JFrame("Swing Too Many Balls! - " + rendererType + " Rendering");
        f_frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f_frame.setResizable(false);
        f_ping = master;
        r1 = new Racket(f_ping,true);
        f_frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                f_ping.Stop();
                f_infoTimer.dispose();
                f_renderer.getTimingSource().dispose();
                f_renderer.shutdown();
            }
        });
        f_frame.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        f_frame.add(topPanel, BorderLayout.NORTH);
        topPanel.setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        topPanel.add(buttonPanel, BorderLayout.WEST);
        buttonPanel.setLayout(new FlowLayout());
        final JButton addBall = new JButton("Add Ball");
        addBall.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                f_renderer.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        addBall();
                        initRacket1();
                    }
                });
                f_ballCount++;
                updateBallCount();
            }
        });
        final JButton add10Balls = new JButton("Add 10 Balls");
        add10Balls.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                f_renderer.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        addBall();
                        addBall();
                        addBall();
                        addBall();
                        addBall();
                        addBall();
                        addBall();
                        addBall();
                        addBall();
                        addBall();
                    }
                });
                f_ballCount += 10;
                updateBallCount();
            }
        });
        final JButton removeBall = new JButton("Remove Ball");
        removeBall.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                f_renderer.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        removeBall();
                    }
                });
                if (f_ballCount > 0)
                    f_ballCount--;
                updateBallCount();
            }
        });
        final JButton remove10Balls = new JButton("Remove 10 Balls");
        remove10Balls.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                f_renderer.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        removeBall();
                        removeBall();
                        removeBall();
                        removeBall();
                        removeBall();
                        removeBall();
                        removeBall();
                        removeBall();
                        removeBall();
                        removeBall();
                    }
                });
                f_ballCount -= 10;
                if (f_ballCount < 0)
                    f_ballCount = 0;
                updateBallCount();
            }
        });
        buttonPanel.add(addBall);
        buttonPanel.add(add10Balls);
        buttonPanel.add(removeBall);
        buttonPanel.add(remove10Balls);
        f_infoLabel = new JLabel();
        topPanel.add(f_infoLabel, BorderLayout.EAST);
        addBall.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                r1.typed(e.getKeyCode());
            }

            @Override
            public void keyPressed(KeyEvent e) {
                r1.pressed(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                r1.released(e.getKeyCode());
            }
        });
        f_panel = new JRendererPanel();
        f_frame.add(f_panel, BorderLayout.CENTER);
        f_panel.setBackground(Color.white);
        f_panel.setPreferredSize(new Dimension(600,600));
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
    }

    void updateBallCount() {
        f_infoLabel.setText("Balls: " + f_ballCount + "    FPS: " + f_renderer.getFPS());
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

    void initRacket1(){
        r1.v=1;
        r1.frame=f_panel.getWidth();
        final int duration = 4;
        final TimingTarget circularMovement = new TimingTargetAdapter() {
            @Override
            public void timingEvent(Animator source, double fraction) {
                r1.update();
            }
        };
        r1.animator = new Animator.Builder().setDuration(duration, SECONDS).addTarget(circularMovement)
                .setRepeatCount(Animator.INFINITE).setRepeatBehavior(Animator.RepeatBehavior.LOOP).build();
        r1.animator.start();

    }
    void initRacket2(){
        r2 = new Racket(null,false);
        r2.frame=f_panel.getWidth();
        final int duration = 4;
        final TimingTarget circularMovement = new TimingTargetAdapter() {
            @Override
            public void timingEvent(Animator source, double fraction) {
                r2.update();
            }
        };
        r2.animator = new Animator.Builder().setDuration(duration, SECONDS).addTarget(circularMovement)
                .setRepeatCount(Animator.INFINITE).setRepeatBehavior(Animator.RepeatBehavior.LOOP).build();
        r2.animator.start();
    }
    void addBall() {
        final Ball ball = new Ball();
        ball.imageIndex = f_die.nextInt(5);
        BufferedImage ballImage = f_ballImages[ball.imageIndex];
        if (initBALLproperties==null)
        {
            ball.setX(f_die.nextInt(f_panel.getWidth() - ballImage.getWidth()));
            ball.setY(f_die.nextInt(f_panel.getHeight() - ballImage.getHeight()));
            ball.vx=f_die.nextInt(20);
            ball.vy=f_die.nextInt(20);
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
        g2d.setBackground(Color.white);
        g2d.clearRect(0, 0, width, height);

        for (Ball ball : f_balls) {
            g2d.drawImage(f_ballImages[ball.imageIndex], ball.getX(), ball.getY(), null);
            }
        g2d.fillRect(r1.getY(),r1.getX()+f_panel.getHeight()-r1.width,r1.getW(),r1.getH());
        if(r2!=null)
            g2d.fillRect(f_panel.getWidth()-r2.getY()-r2.height,r2.getX(),r2.getW(),r2.getH());
    }

    @Override
    public void renderShutdown() {
        // Nothing to do
    }


}

