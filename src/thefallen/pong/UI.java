package thefallen.pong;

import com.sun.glass.ui.EventLoop;
import com.sun.javafx.font.freetype.HBGlyphLayout;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.json.JSONObject;
import sun.rmi.runtime.Log;

import java.io.File;
import thefallen.pong.Resources;
import java.util.prefs.Preferences;

import static com.surelogic.Part.Static;
import static java.lang.System.err;
import static java.lang.System.out;

public class UI extends Application {

    private static final String PLAYER_NAME = "player_name";
    private static final String ELEMENT = "element";
    private static final String MUSIC_VOLUME = "music_volume";
    private static final String SFX_VOLUME = "sfx_volume";
    private static final String FULLSCREEN = "fullscreen";
    MediaPlayer mediaPlayer;
    private ping pee=null;
    private Scene scene;
    private Stage primaryStage;
    private int full=1;
    public enum gameScreen{
        LANDING,NEWGAME,CREATESERVER,FINDSERVER,SETTINGS,ABOUT,EXIT
    }
    public static String packagePath = "/thefallen/pong";
    public int mode=0;
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage=primaryStage;

        Media sound = new Media(Resources.getResource(Resources.music).toExternalForm());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();

        Preferences prefs = Preferences.userRoot().node(packagePath);
        mediaPlayer.setVolume(((double)Integer.valueOf(prefs.get(MUSIC_VOLUME,"10")))/10);

        this.primaryStage.setTitle("Ping Pong!");
        scene = new Scene(getLandingScene(), 800,600);
        this.primaryStage.setScene(scene);

        if(prefs.get(FULLSCREEN,"1").equals("0")){
            this.primaryStage.setFullScreen(true);
        }

        this.primaryStage.show();
    }

    public BorderPane getLandingScene() {
        GridPane landingHeader = getheader("PING PONG",false);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Button btn = getButton("NEW GAME",gameScreen.NEWGAME);
        grid.add(btn, 0, 0);

        Button btn2 = getButton("CREATE SERVER",gameScreen.CREATESERVER);
        grid.add(btn2, 0, 1);

        Button btn3 = getButton("FIND SERVERS",gameScreen.FINDSERVER);
        grid.add(btn3, 0, 2);

        Button btn4 = getButton("SETTINGS",gameScreen.SETTINGS);
        grid.add(btn4, 0, 3);

        Button btn5 = getButton("ABOUT",gameScreen.ABOUT);
        grid.add(btn5, 0, 4);

        Button btn6 = getButton("EXIT",gameScreen.EXIT);
        grid.add(btn6, 0, 5);

        BorderPane border = new BorderPane();
        border.setTop(landingHeader);
        border.setCenter(grid);
        border.setStyle("-fx-background-color: #000000");

        return border;
    }

    public BorderPane getCreateServerScene(){
        GridPane createserverheader = getheader("CREATE SERVER",false);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label userName = new Label("SERVER NAME             :");
        userName.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        userName.setTextFill(Color.valueOf("#B4B0AB"));
        grid.add(userName, 0, 0);

        Label back = new Label("");
        back.setStyle("-fx-background-color: #B4B0AB");
        back.setMinWidth(200);
        back.setMinHeight(40);
        grid.add(back, 1, 0);

        TextField userTextField = new TextField();
        userTextField.setStyle("-fx-background-color: #7C7E7C; -fx-text-fill: #333333");
        userTextField.setPadding(new Insets(0,5,0,5));
        userTextField.setAlignment(Pos.CENTER);
        userTextField.setMaxHeight(30);
        userTextField.setTranslateX(20);
        userTextField.setMaxWidth(160);
        userTextField.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        grid.add(userTextField, 1, 0);

        Label password= new Label("PASSWORD(OPTIONAL)     :");
        password.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        password.setTextFill(Color.valueOf("#B4B0AB"));
        grid.add(password,0,1);

        Label back2 = new Label("");
        back2.setStyle("-fx-background-color: #B4B0AB");
        back2.setMinWidth(200);
        back2.setMinHeight(40);
        grid.add(back2, 1, 1);

        TextField passwordField = new TextField();
        passwordField.setStyle("-fx-background-color: #7C7E7C; -fx-text-fill: #333333");
        passwordField.setPadding(new Insets(0,5,0,5));
        passwordField.setAlignment(Pos.CENTER);
        passwordField.setMaxHeight(30);
        passwordField.setTranslateX(20);
        passwordField.setMaxWidth(160);
        passwordField.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        grid.add(passwordField, 1, 1);

        Label maxplayers= new Label("MAX PLAYERS(OPTIONAL)  :");
        maxplayers.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        maxplayers.setTextFill(Color.valueOf("#B4B0AB"));
        grid.add(maxplayers,0,2);

        Label back3 = new Label("");
        back3.setStyle("-fx-background-color: #B4B0AB");
        back3.setMinWidth(200);
        back3.setMinHeight(40);
        grid.add(back3, 1, 2);

        TextField maxplayersField = new TextField();
        maxplayersField.setStyle("-fx-background-color: #7C7E7C; -fx-text-fill: #333333");
        maxplayersField.setPadding(new Insets(0,5,0,5));
        maxplayersField.setAlignment(Pos.CENTER);
        maxplayersField.setMaxHeight(30);
        maxplayersField.setTranslateX(20);
        maxplayersField.setMaxWidth(160);
        maxplayersField.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        grid.add(maxplayersField, 1, 2);

        Label fullscreen= new Label("GAME MODE               :");
        fullscreen.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        fullscreen.setTextFill(Color.valueOf("#B4B0AB"));
        grid.add(fullscreen, 0, 3);

        Button normalmode = getsettingsButton("NORMAL",0);
        Button deathmatchmode= getsettingsButton("/DEATHMATCH",1);

        normalmode.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                normalmode.setTextFill(Color.valueOf("#B4B0AB"));
                deathmatchmode.setTextFill(Color.valueOf("#333333"));
                mode=0;
            }
        });

        deathmatchmode.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                deathmatchmode.setTextFill(Color.valueOf("#B4B0AB"));
                normalmode.setTextFill(Color.valueOf("#333333"));
                mode=1;
            }
        });

        HBox hbox3 = new HBox();
        hbox3.setPadding(new Insets(5,5,5,20));
        hbox3.getChildren().addAll(normalmode,deathmatchmode);
        grid.add(hbox3,1,3);

        Button btn = getButton("CREATE SERVER",gameScreen.LANDING);
        btn.setTranslateX(-140);
        btn.setTranslateY(40);
        btn.setMinWidth(100);
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                String server_name = userTextField.getText();
                String password = passwordField.getText();
                String maxplayers = maxplayersField.getText();

                BorderPane bp = getcreatingserver(server_name,password,maxplayers);
                scene.setRoot(bp);
                Misc.pop();
            }
        });
        grid.add(btn,1,4);

        final ImageView LogoView = new ImageView();
        final Image logoPNG = new Image(thefallen.pong.Resources.getResource("back.png").toExternalForm());
        LogoView.setImage(logoPNG);
        LogoView.setFitWidth(50);
        LogoView.setFitHeight(50);
        LogoView.setTranslateX(-30);
        LogoView.setTranslateY(-50);
        LogoView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                BorderPane bp = getLandingScene();
                scene.setRoot(bp);
                Misc.pop();

            }
        });

        BorderPane border = new BorderPane();
        border.setTop(createserverheader);
        border.setCenter(grid);
        border.setRight(LogoView);
        border.setStyle("-fx-background-color: #000000");

        return border;
    }

    public BorderPane getcreatingserver(String server_name,String password,String maxplayers){
        GridPane createserverheader = getheader("Waiting",true);
        Platform.setImplicitExit(false);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Button btn = getButton("START GAME",gameScreen.LANDING);

        Preferences prefs = Preferences.userRoot().node(packagePath);
        String player_name= prefs.get(PLAYER_NAME,"");
        String element= prefs.get(ELEMENT,"");
        grid.add(getplayerview(player_name,element), 0, grid1I++);
        Misc.Modes gamemode;
        if(mode==0)
            gamemode= Misc.Modes.NORMAL;
        else
            gamemode= Misc.Modes.DEATHMATCH;
        String ip = ping.getmyIP();
        try {
            if(ip.equals("")) {
                out.println("Could not get host .. Are You connected to a network?");
            }
            else {
                pee = new ping(ip, Misc.Port);
                pee.listener=gameOverListener;
                int MaxPlayers = -1;
                try {
                    if(!maxplayers.equals("")) MaxPlayers = Integer.parseInt(maxplayers);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                pee.createserver(server_name,password,MaxPlayers,gamemode,player_name,element);
                pee.joinListener = new ping.onJoinListener() {
                    @Override
                    public void onjoin(String name, String element, String ip) {
                        grid1I++;
                        Platform.runLater(() -> grid.add(getplayerview(name,element), 0, grid1I));
                        if (grid1I > 0) Platform.runLater(() -> btn.setVisible(true));
                    }

                    @Override
                    public void onfind(String name, String password, int maxPlayers, String mode,String IP) {
                    }
                };
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        final ImageView LogoView = new ImageView();
        final Image logoPNG = new Image(thefallen.pong.Resources.getResource("back.png").toExternalForm());
        LogoView.setImage(logoPNG);
        LogoView.setFitWidth(50);
        LogoView.setFitHeight(50);
        LogoView.setTranslateX(-30);
        LogoView.setTranslateY(-50);
        LogoView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                BorderPane bp = getLandingScene();
                scene.setRoot(bp);
                Misc.pop();
                if(pee!=null) pee.Stop();
                grid1I=0;
            }
        });


        btn.setTranslateX(-60);
        btn.setTranslateY(30);
        btn.setMinWidth(100);
        btn.setVisible(false);
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                pee.broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.START)).toString());
            }
        });
        grid.add(btn,1,4);

        BorderPane border = new BorderPane();
        border.setTop(createserverheader);
        border.setCenter(grid);
        border.setRight(LogoView);
        border.setStyle("-fx-background-color: #000000");

        return border;
    }

    GridPane grid2;
    int grid1I=0,grid2I=0,grid3I=0;
    QuestMode quest1;
    SinglePlayer2 quest2;
    public BorderPane getNewGameScene(){
        GridPane settingsHeader = getheader("NEW GAME",false);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label userName = new Label("Number of bots :");
        userName.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        userName.setTextFill(Color.valueOf("#B4B0AB"));
        grid.add(userName, 0, 0);

        Label back = new Label("");
        back.setStyle("-fx-background-color: #B4B0AB");
        back.setMinWidth(200);
        back.setMinHeight(40);
        grid.add(back, 1, 0);

        TextField userTextField = new TextField();
        userTextField.setText("1");
        userTextField.setStyle("-fx-background-color: #7C7E7C; -fx-text-fill: #333333");
        userTextField.setPadding(new Insets(0,5,0,5));
        userTextField.setAlignment(Pos.CENTER);
        userTextField.setMaxHeight(30);
        userTextField.setTranslateX(20);
        userTextField.setMaxWidth(160);
        userTextField.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        grid.add(userTextField, 1, 0);

        //test

        Label modelabel= new Label("MODE     :");
        modelabel.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        modelabel.setTextFill(Color.valueOf("#B4B0AB"));
        grid.add(modelabel, 0, 1);

        Preferences prefs = Preferences.userRoot().node(packagePath);
        prefs.put("mode","1");

        Button quest  = getsettingsButton("Quest",0);
        Button broyal = getsettingsButton("/Battle Royale",1);
        quest.setTextFill(Color.valueOf("#B4B0AB"));

        quest.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                quest.setTextFill(Color.valueOf("#B4B0AB"));
                broyal.setTextFill(Color.valueOf("#333333"));
                prefs.put("mode","1");
            }
        });

        broyal.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                broyal.setTextFill(Color.valueOf("#B4B0AB"));
                quest.setTextFill(Color.valueOf("#333333"));
                prefs.put("mode","2");
            }
        });

        HBox hBox4 = new HBox();
        hBox4.setPadding(new Insets(5,5,5,20));
        hBox4.getChildren().addAll(quest,broyal);
        grid.add(hBox4,1,1);

        //end
        int off=0;
        int on=1;
        Button onFullScreen = getsettingsButton("ON",on);
        Button offFullScreen= getsettingsButton("/0FF",off);

        Label characterlabel= new Label("CHARACTER     :");
        characterlabel.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        characterlabel.setTextFill(Color.valueOf("#B4B0AB"));
        grid.add(characterlabel, 0, 2);

        prefs.put("diff","0");

        String ch=prefs.get(ELEMENT,"void");
        Button voidbutton  = getsettingsButton("Easy",0);
        Button earthbutton= getsettingsButton("/Medium",1);
        Button waterbutton= getsettingsButton("/Hard",1);
        Button windbutton= getsettingsButton("/Harder",1);
        Button firebutton= getsettingsButton("/Hardest",1);

        voidbutton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                voidbutton.setTextFill(Color.valueOf("#B4B0AB"));
                earthbutton.setTextFill(Color.valueOf("#333333"));
                windbutton.setTextFill(Color.valueOf("#333333"));
                waterbutton.setTextFill(Color.valueOf("#333333"));
                firebutton.setTextFill(Color.valueOf("#333333"));
                prefs.put("diff","0");
            }
        });

        earthbutton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                earthbutton.setTextFill(Color.valueOf("#B4B0AB"));
                voidbutton.setTextFill(Color.valueOf("#333333"));
                waterbutton.setTextFill(Color.valueOf("#333333"));
                windbutton.setTextFill(Color.valueOf("#333333"));
                firebutton.setTextFill(Color.valueOf("#333333"));
                prefs.put("diff","1");
            }
        });

        waterbutton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                waterbutton.setTextFill(Color.valueOf("#B4B0AB"));
                voidbutton.setTextFill(Color.valueOf("#333333"));
                earthbutton.setTextFill(Color.valueOf("#333333"));
                windbutton.setTextFill(Color.valueOf("#333333"));
                firebutton.setTextFill(Color.valueOf("#333333"));
                prefs.put("diff","2");
            }
        });

        windbutton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                windbutton.setTextFill(Color.valueOf("#B4B0AB"));
                voidbutton.setTextFill(Color.valueOf("#333333"));
                waterbutton.setTextFill(Color.valueOf("#333333"));
                earthbutton.setTextFill(Color.valueOf("#333333"));
                firebutton.setTextFill(Color.valueOf("#333333"));
                prefs.put("diff","3");
            }
        });

        firebutton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                firebutton.setTextFill(Color.valueOf("#B4B0AB"));
                voidbutton.setTextFill(Color.valueOf("#333333"));
                waterbutton.setTextFill(Color.valueOf("#333333"));
                windbutton.setTextFill(Color.valueOf("#333333"));
                earthbutton.setTextFill(Color.valueOf("#333333"));
                prefs.put("diff","4");
            }
        });

        HBox hBox8 = new HBox();
        hBox8.setPadding(new Insets(5,5,5,20));
        hBox8.getChildren().addAll(voidbutton,earthbutton,waterbutton,windbutton,firebutton);
        grid.add(hBox8,1,2);

        //end
        Label gravity = new Label("Gravity :");
        gravity.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        gravity.setTextFill(Color.valueOf("#B4B0AB"));
        grid.add(gravity, 0, 3);

        prefs.put("gravity","1");

        onFullScreen.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                onFullScreen.setTextFill(Color.valueOf("#B4B0AB"));
                offFullScreen.setTextFill(Color.valueOf("#333333"));
                prefs.put("gravity","0");
            }
        });

        offFullScreen.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                offFullScreen.setTextFill(Color.valueOf("#B4B0AB"));
                onFullScreen.setTextFill(Color.valueOf("#333333"));
                prefs.put("gravity","1");
            }
        });

        HBox hbox3 = new HBox();
        hbox3.setPadding(new Insets(5,5,5,20));
        hbox3.getChildren().addAll(onFullScreen,offFullScreen);
        grid.add(hbox3,1,3);

        Button btn = getButton("Start",gameScreen.LANDING);
        btn.setTranslateX(-60);
        btn.setTranslateY(30);
        btn.setMinWidth(100);
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                Misc.pop();
                boolean G=false;
                if(prefs.get("gravity","1").equals("0"))
                    G=true;
                int diff = Integer.valueOf(prefs.get("diff", "1"));
                int bots = 1;
                try {
                    bots = Integer.valueOf(userTextField.getText());
                } catch (NumberFormatException e1) {
                    e1.printStackTrace();
                }
                String element = prefs.get(ELEMENT, Misc.Avatar.VOID.toString());
                Misc.Avatar ele = Misc.Avatar.VOID;
                if(element.equals(Misc.Avatar.EARTH.toString()))
                    ele = Misc.Avatar.EARTH;
                else if(element.equals(Misc.Avatar.FIRE.toString()))
                    ele = Misc.Avatar.FIRE;
                else if(element.equals(Misc.Avatar.WIND.toString()))
                    ele = Misc.Avatar.WIND;
                else if(element.equals(Misc.Avatar.WATER.toString()))
                    ele = Misc.Avatar.WATER;

                if(prefs.get("mode","1").equals("2"))
                {
                    quest2 = new SinglePlayer2();
                    quest2.Diff = diff;
                    quest2.full=prefs.get(FULLSCREEN,"1").equals("0");
                    quest2.startQuest(G, bots + 1, ele);
                    quest2.listener = gameOverListener;
                }
                else
                {
                    quest1 = new QuestMode();
                    quest1.max_ai = diff+1;
                    quest1.num_ai = bots;
                    quest1.G = G;
                    quest1.full=prefs.get(FULLSCREEN,"1").equals("0");
                    quest1.startQuest();
                    quest1.gameOverListener = gameOverListener;
                }
            }
        });
        grid.add(btn,1,5);

        final ImageView LogoView = new ImageView();
        final Image logoPNG = new Image(thefallen.pong.Resources.getResource("back.png").toExternalForm());
        LogoView.setImage(logoPNG);
        LogoView.setFitWidth(50);
        LogoView.setFitHeight(50);
        LogoView.setTranslateX(-30);
        LogoView.setTranslateY(-50);
        LogoView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                BorderPane bp = getLandingScene();
                scene.setRoot(bp);
                Misc.pop();

            }
        });

        BorderPane border = new BorderPane();
        border.setTop(settingsHeader);
        border.setCenter(grid);
        border.setRight(LogoView);
        border.setStyle("-fx-background-color: #000000");

        return border;
    }

    QuestMode.onGameOverListener gameOverListener = new QuestMode.onGameOverListener() {
        @Override
        public void onGameOver(boolean success, int score)
        {
                scene.setRoot(getGameOverScene(success));
                if(pee!=null){pee.Stop();pee=null;}
        }
    };

    public BorderPane getGameOverScene(boolean success){
        String res="polebunny.gif";
        if(!success)
            res = "youdied.png";
        if(!success)
            Misc.youDied();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label userName = new Label("YOU WON, HERE'S A POLE DANCING BUNNY FOR YOU HAWTIE (n˘v˘•)¬\nCLICK ON DA HAWT BUNNY TO GO TO HOME SCREEN");
//        userName.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        userName.setFont(Font.font("Tahoma",24));
        userName.setTextFill(Color.valueOf("#B4B0AB"));
        grid.add(userName, 0, 0);

        final Image logoPNG2 = new Image(thefallen.pong.Resources.getResource(res).toExternalForm());
        final ImageView LogoView2 = new ImageView();
        LogoView2.setImage(logoPNG2);
        LogoView2.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                BorderPane bp = getLandingScene();
                scene.setRoot(bp);
                Misc.pop();
            }
        });
        grid.add(LogoView2,0,1);

        BorderPane border = new BorderPane();
        if(success)border.setCenter(grid);
        else border.setCenter(LogoView2);
        border.setStyle("-fx-background-color: #000000");

        return border;
    }
    public BorderPane getwaitingserver(){
        GridPane createserverheader = getheader("Waiting",true);
        Platform.setImplicitExit(false);

        grid2 = new GridPane();
        grid2.setAlignment(Pos.CENTER);
        grid2.setHgap(10);
        grid2.setVgap(10);
        grid2.setPadding(new Insets(25, 25, 25, 25));

        Preferences prefs = Preferences.userRoot().node(packagePath);
        String player_name= prefs.get(PLAYER_NAME,"");
        Misc.Modes gamemode;
        final ImageView LogoView = new ImageView();
        final Image logoPNG = new Image(thefallen.pong.Resources.getResource("back.png").toExternalForm());
        LogoView.setImage(logoPNG);
        LogoView.setFitWidth(50);
        LogoView.setFitHeight(50);
        LogoView.setTranslateX(-30);
        LogoView.setTranslateY(-50);
        LogoView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                BorderPane bp = getLandingScene();
                scene.setRoot(bp);
                Misc.pop();
                if(pee!=null) pee.Stop();
                grid2=null;
            }
        });

        BorderPane border = new BorderPane();
        border.setTop(createserverheader);
        border.setCenter(grid2);
        border.setRight(LogoView);
        border.setStyle("-fx-background-color: #000000");

        return border;
    }

    public BorderPane getFindServerScene(){
        GridPane createserverheader = getheader("FIND SERVER",true);
        Platform.setImplicitExit(false);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Preferences prefs = Preferences.userRoot().node(packagePath);
        String player_name= prefs.get(PLAYER_NAME,"");
        String ip = ping.getmyIP();
        try {
            if(ip.equals("")) {
                out.println("Could not get host .. Are You connected to a network?");
            }
            else {
                pee = new ping(ip, Misc.Port);
                pee.listener=gameOverListener;
                pee.findserver();
                pee.joinListener = new ping.onJoinListener() {
                    @Override
                    public void onjoin(String name, String element, String ip) {
                        if(grid2==null)
                        {
                            grid2I=0;
                            Platform.runLater(() -> scene.setRoot(getwaitingserver()));
                            Misc.pop();
                        }
                        Platform.runLater(() -> Platform.runLater(() -> grid2.add(getplayerview(name, element), 0, grid2I++)));
                    }
                    @Override
                    public void onfind(String name, String password, int maxPlayers, String mode,String IP) {
                        Platform.runLater(() -> grid.add(getServerView(name,mode,password,maxPlayers,IP), 0, grid3I++));
                        System.err.print("\n"+"shit\n"+name+"\n");
                    }
                };
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        final ImageView LogoView = new ImageView();
        final Image logoPNG = new Image(thefallen.pong.Resources.getResource("back.png").toExternalForm());
        LogoView.setImage(logoPNG);
        LogoView.setFitWidth(50);
        LogoView.setFitHeight(50);
        LogoView.setTranslateX(-30);
        LogoView.setTranslateY(-50);
        LogoView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                scene.setRoot(getLandingScene());
                Misc.pop();

                if(pee!=null) pee.Stop();
                grid3I=0;
            }
        });


        BorderPane border = new BorderPane();
        border.setTop(createserverheader);
        border.setCenter(grid);
        border.setRight(LogoView);
        border.setStyle("-fx-background-color: #000000");

        return border;
    }

    public HBox getplayerview(String name, String element){

        Label userName = new Label(name.toUpperCase());
        userName.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        userName.setPadding(new Insets(0,50,0,0));
        userName.setTextFill(Color.valueOf("#B4B0AB"));

        Label userName2 = new Label(element.toUpperCase());
        userName2.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        userName2.setTextFill(Color.valueOf("#B4B0AB"));

        HBox hBox = new HBox();
        hBox.getChildren().addAll(userName,userName2);
        return hBox;
    }

    public HBox getServerView(String name, String mode,String pass,int maxPlayers,String IP){

        Label Name = new Label(name.toUpperCase());
        Name.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        Name.setPadding(new Insets(0,50,0,0));
        Name.setTextFill(Color.valueOf("#B4B0AB"));

        Label Mode = new Label(mode.toUpperCase());
        Mode.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        Mode.setPadding(new Insets(0,50,0,0));
        Mode.setTextFill(Color.valueOf("#B4B0AB"));


        TextField passField = new TextField();
        passField.setStyle("-fx-background-color: #7C7E7C; -fx-text-fill: #333333");
        passField.setPadding(new Insets(0,5,0,5));
        passField.setAlignment(Pos.CENTER);
        passField.setMaxHeight(30);
        passField.setTranslateX(20);
        passField.setMaxWidth(160);
        passField.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));

        Label Players = new Label(maxPlayers+"");
        Players.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        Players.setPadding(new Insets(0,50,0,50));
        Players.setTextFill(Color.valueOf("#B4B0AB"));

        Button btn = getButton("CONNECT",gameScreen.LANDING);
//        btn.setTranslateX(-60);
//        btn.setTranslateY(30);
        Preferences prefs = Preferences.userRoot().node(packagePath);
        String player_name= prefs.get(PLAYER_NAME,"Player1");
        String element = prefs.get(ELEMENT,"");

        btn.setMinWidth(100);
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                if(passField.getText().equals(pass))
                    pee.joinserver(player_name,element,IP,mode);
            }
        });
        HBox hBox = new HBox();
        hBox.getChildren().addAll(Name,Mode);
        if(!pass.equals(""))
            hBox.getChildren().add(passField);
        if(maxPlayers!=-1)
            hBox.getChildren().add(Players);
        hBox.getChildren().add(btn);
        return hBox;
    }

    public BorderPane getAboutScene(){
        return null;
    }

    public BorderPane getSettingsScene(){
        GridPane settingsHeader = getheader("SETTINGS",false);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label userName = new Label("PLAYER'S NAME :");
        userName.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        userName.setTextFill(Color.valueOf("#B4B0AB"));
        grid.add(userName, 0, 0);

        Label back = new Label("");
        back.setStyle("-fx-background-color: #B4B0AB");
        back.setMinWidth(200);
        back.setMinHeight(40);
        grid.add(back, 1, 0);

        Preferences prefs = Preferences.userRoot().node(packagePath);

        TextField userTextField = new TextField();
        userTextField.setText(prefs.get(PLAYER_NAME,""));
        userTextField.setStyle("-fx-background-color: #7C7E7C; -fx-text-fill: #333333");
        userTextField.setPadding(new Insets(0,5,0,5));
        userTextField.setAlignment(Pos.CENTER);
        userTextField.setMaxHeight(30);
        userTextField.setTranslateX(20);
        userTextField.setMaxWidth(160);
        userTextField.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        grid.add(userTextField, 1, 0);

        //test

        Label characterlabel= new Label("CHARACTER     :");
        characterlabel.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        characterlabel.setTextFill(Color.valueOf("#B4B0AB"));
        grid.add(characterlabel, 0, 4);

        String ch=prefs.get(ELEMENT,"void");
        int[] charcols= {1,1,1,1,1};
        if(ch.equals(Misc.Avatar.VOID.toString()))charcols[0]=0;
        else if(ch.equals(Misc.Avatar.EARTH.toString()))charcols[1]=0;
        else if(ch.equals(Misc.Avatar.WIND.toString()))charcols[2]=0;
        else if(ch.equals(Misc.Avatar.WATER.toString()))charcols[3]=0;
        else if(ch.equals(Misc.Avatar.FIRE.toString()))charcols[4]=0;

        Button voidbutton  = getsettingsButton("VOID",charcols[0]);
        Button earthbutton= getsettingsButton("/EARTH",charcols[1]);
        Button windbutton= getsettingsButton("/WIND",charcols[2]);
        Button waterbutton= getsettingsButton("/WATER",charcols[3]);
        Button firebutton= getsettingsButton("/FIRE",charcols[4]);

        Label description = new Label("DESCRIPTION :");
        description.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        description.setTextFill(Color.valueOf("#B4B0AB"));
        grid.add(description, 1, 5);

        String desc = "LOL";

        if(charcols[0] == 0) desc = Misc.Void;
        else if (charcols[1] == 0) desc = Misc.Earth;
        else if (charcols[2] == 0) desc = Misc.Wind;
        else if (charcols[3] == 0) desc = Misc.Water;
        else if (charcols[4] == 0) desc = Misc.Fire;

        description.setText(desc);

        voidbutton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                voidbutton.setTextFill(Color.valueOf("#B4B0AB"));
                earthbutton.setTextFill(Color.valueOf("#333333"));
                windbutton.setTextFill(Color.valueOf("#333333"));
                waterbutton.setTextFill(Color.valueOf("#333333"));
                firebutton.setTextFill(Color.valueOf("#333333"));
                prefs.put(ELEMENT,Misc.Avatar.VOID.toString());
                description.setText(Misc.Void);
            }
        });

        earthbutton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                earthbutton.setTextFill(Color.valueOf("#B4B0AB"));
                voidbutton.setTextFill(Color.valueOf("#333333"));
                waterbutton.setTextFill(Color.valueOf("#333333"));
                windbutton.setTextFill(Color.valueOf("#333333"));
                firebutton.setTextFill(Color.valueOf("#333333"));
                prefs.put(ELEMENT,Misc.Avatar.EARTH.toString());
                description.setText(Misc.Earth);
            }
        });

        waterbutton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                waterbutton.setTextFill(Color.valueOf("#B4B0AB"));
                voidbutton.setTextFill(Color.valueOf("#333333"));
                earthbutton.setTextFill(Color.valueOf("#333333"));
                windbutton.setTextFill(Color.valueOf("#333333"));
                firebutton.setTextFill(Color.valueOf("#333333"));
                prefs.put(ELEMENT,Misc.Avatar.WATER.toString());
                description.setText(Misc.Water);
            }
        });

        windbutton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                windbutton.setTextFill(Color.valueOf("#B4B0AB"));
                voidbutton.setTextFill(Color.valueOf("#333333"));
                waterbutton.setTextFill(Color.valueOf("#333333"));
                earthbutton.setTextFill(Color.valueOf("#333333"));
                firebutton.setTextFill(Color.valueOf("#333333"));
                prefs.put(ELEMENT,Misc.Avatar.WIND.toString());
                description.setText(Misc.Wind);
            }
        });

        firebutton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                firebutton.setTextFill(Color.valueOf("#B4B0AB"));
                voidbutton.setTextFill(Color.valueOf("#333333"));
                waterbutton.setTextFill(Color.valueOf("#333333"));
                windbutton.setTextFill(Color.valueOf("#333333"));
                earthbutton.setTextFill(Color.valueOf("#333333"));
                prefs.put(ELEMENT,Misc.Avatar.FIRE.toString());
                description.setText(Misc.Fire);
            }
        });

        HBox hBox4 = new HBox();
        hBox4.setPadding(new Insets(5,5,5,20));
        hBox4.getChildren().addAll(voidbutton,waterbutton,earthbutton,windbutton,firebutton);
        grid.add(hBox4,1,4);

        //end

        Label musicvol = new Label("MUSIC VOLUME  :");
        musicvol.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        musicvol.setTextFill(Color.valueOf("#B4B0AB"));
        grid.add(musicvol, 0, 1);

        HBox hBox = getVolumeHBox(0,Integer.valueOf(prefs.get(MUSIC_VOLUME,"0")));
        hBox.setPadding(new Insets(5,5,5,20));
        grid.add(hBox,1,1);

        Label sfxvol= new Label("SFX VOLUME    :");
        sfxvol.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        sfxvol.setTextFill(Color.valueOf("#B4B0AB"));
        grid.add(sfxvol, 0, 2);

        HBox hBox2 = getVolumeHBox(1,Integer.valueOf(prefs.get(SFX_VOLUME,"0")));
        hBox2.setPadding(new Insets(5,5,5,20));
        grid.add(hBox2,1,2);

        Label fullscreen= new Label("FULLSCREEN     :");
        fullscreen.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        fullscreen.setTextFill(Color.valueOf("#B4B0AB"));
        grid.add(fullscreen, 0, 3);

        int on=Integer.valueOf(prefs.get(FULLSCREEN,"1"));
        int off=0;
        if(on==0)off=1;
        Button onFullScreen = getsettingsButton("ON",on);
        Button offFullScreen= getsettingsButton("/0FF",off);

        onFullScreen.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                onFullScreen.setTextFill(Color.valueOf("#B4B0AB"));
                offFullScreen.setTextFill(Color.valueOf("#333333"));
                prefs.put(FULLSCREEN,"0");
                primaryStage.setFullScreen(true);
            }
        });

        offFullScreen.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                offFullScreen.setTextFill(Color.valueOf("#B4B0AB"));
                onFullScreen.setTextFill(Color.valueOf("#333333"));
                prefs.put(FULLSCREEN,"1");
                primaryStage.setFullScreen(false);
                full=1;
            }
        });

        HBox hbox3 = new HBox();
        hbox3.setPadding(new Insets(5,5,5,20));
        hbox3.getChildren().addAll(onFullScreen,offFullScreen);
        grid.add(hbox3,1,3);

        Button btn = getButton("BACK",gameScreen.LANDING);
        btn.setTranslateX(-60);
        btn.setTranslateY(30);
        btn.setMinWidth(100);
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                String player_name = userTextField.getText();
                Preferences prefs = Preferences.userRoot().node(packagePath);
                prefs.put(PLAYER_NAME,player_name);

                scene.setRoot(getLandingScene());
                Misc.pop();

            }
        });
        grid.add(btn,1,6);

        BorderPane border = new BorderPane();
        border.setTop(settingsHeader);
        border.setCenter(grid);
        border.setStyle("-fx-background-color: #000000");

        return border;
    }

    public HBox getVolumeHBox(int vol_type, int vol){
        Button[] buttons=new Button[11];

        buttons[0]= getsettingsButton(" ",0);
        for (int i = 1; i <= vol; i++) {
            buttons[i] = getsettingsButton("I",0);
        }

        for (int i = vol+1; i < 11; i++) {
            buttons[i] = getsettingsButton("I",1);
        }

        for (int i = 0; i < 11; i++) {
            final int t=i;
            buttons[i].setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent e) {
                    for (int j = 0; j <= t; j++) {
                        buttons[j].setTextFill(Color.valueOf("#B4B0AB"));
                    }
                    for (int j = t+1; j <11 ; j++) {
                        buttons[j].setTextFill(Color.valueOf("#333333"));
                    }
                    if(vol_type==0){
                        Preferences prefs = Preferences.userRoot().node(packagePath);
                        prefs.put(MUSIC_VOLUME,t+"");
                        mediaPlayer.setVolume(((double)t)/10);
                    }
                    if(vol_type==1){
                        Preferences prefs = Preferences.userRoot().node(packagePath);
                        prefs.put(SFX_VOLUME,t+"");
                        Misc.sfxvol=t;
                    }
                }
            });
        }
        HBox hBox = new HBox();
        hBox.getChildren().addAll(buttons);
        return hBox;
    }

    public Button getsettingsButton(String str,int color){
        Button btn = new Button(str);
        btn.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        btn.setStyle("-fx-background-color: transparent;");
        btn.setPadding(new Insets(0,3,0,0));
        btn.setMaxHeight(10);
        if (color==0)
            btn.setTextFill(Color.valueOf("#B4B0AB"));
        else
            btn.setTextFill(Color.valueOf("#333333"));

        return btn;
    }

    public Button getButton(String text,gameScreen gsc){
        Button btn = new Button(text);
        btn.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        btn.setMinWidth(250);
        btn.setTextFill(Color.BLACK);
        btn.setStyle("-fx-base: #B4B0AB;");
        btn.setDefaultButton(false);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

                if(gsc==gameScreen.SETTINGS){
                    scene.setRoot(getSettingsScene());
                    Misc.pop();
                }
                if(gsc==gameScreen.LANDING){
                    scene.setRoot(getLandingScene());
                    Misc.pop();
                }
                if(gsc==gameScreen.CREATESERVER){
                    scene.setRoot(getCreateServerScene());
                    Misc.pop();
                }
                if(gsc==gameScreen.FINDSERVER){
                    scene.setRoot(getFindServerScene());
                    Misc.pop();
                }
                if(gsc == gameScreen.NEWGAME){
                    scene.setRoot(getNewGameScene());
                    Misc.pop();
                }
                if(gsc == gameScreen.ABOUT) {
                    scene.setRoot(getAboutScene());
                    Misc.pop();
                }
                if(gsc == gameScreen.EXIT){
                    Misc.pop();
                    Platform.exit();
                }
            }
        });
        return btn;
    }

    public GridPane getheader(String title,boolean loading){

        GridPane header = new GridPane();
        header.setPadding(new Insets(5));
        header.setHgap(10);
        header.setVgap(10);

        final ImageView LogoView = new ImageView();
        String res = "logo_small.png";
        if(loading) res = "477.gif";
        final Image logoPNG = new Image(thefallen.pong.Resources.getResource(res).toExternalForm());
        LogoView.setImage(logoPNG);

        final Text titleField = new Text(title);
        titleField.setStyle("-fx-fill: #B4B0AB");
        titleField.setTranslateY(10);
        titleField.setTranslateX(10);
        titleField.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT2).toString(), 42));
        final HBox pictureRegion = new HBox();

        pictureRegion.getChildren().addAll(LogoView,titleField);
        header.add(pictureRegion, 1, 1);
        return header;
    }
}

