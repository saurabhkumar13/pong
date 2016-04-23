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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.json.JSONObject;
import sun.rmi.runtime.Log;

import java.util.prefs.Preferences;

import static com.surelogic.Part.Static;
import static java.lang.System.out;

public class UI extends Application {

    private static final String PLAYER_NAME = "player_name";
    private static final String MUSIC_VOLUME = "music_volume";
    private static final String SFX_VOLUME = "sfx_volume";
    private static final String FULLSCREEN = "fullscreen";

    private ping pee=null;
    public Stage stage;
    public enum gameScreen{
        LANDING,NEWGAME,CREATESERVER,FINDSERVER,SETTINGS
    }
    public static String packagePath = "/thefallen/pong";
    public int mode=0;
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        this.stage=primaryStage;

//        primaryStage.setFullScreen(true);
        primaryStage.setTitle("Ping Pong!");
        primaryStage.setScene(getLandingScene());

        primaryStage.show();
    }

    public Scene getLandingScene(){
        GridPane landingHeader = getheader("PING PONG");

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

        BorderPane border = new BorderPane();
        border.setTop(landingHeader);
        border.setCenter(grid);
        border.setStyle("-fx-background-color: #000000");

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        Scene scene = new Scene(border, bounds.getWidth(), bounds.getHeight());
        return scene;
    }

    public Scene getCreateServerScene(){
        GridPane createserverheader = getheader("CREATE SERVER");

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
                String element = "shiittt";
                String server_name = userTextField.getText();
                String password = passwordField.getText();
                String maxplayers = maxplayersField.getText();

                Scene sc = getcreatingserver(server_name,element,password,maxplayers);
                stage.setScene(sc);
            }
        });
        grid.add(btn,1,4);


        final ImageView LogoView = new ImageView();
        final Image logoPNG = new Image(UI.class.getResourceAsStream("../../res/back.png"));
        LogoView.setImage(logoPNG);
        LogoView.setFitWidth(50);
        LogoView.setFitHeight(50);
        LogoView.setTranslateX(-30);
        LogoView.setTranslateY(-50);
        LogoView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                Scene sc= getLandingScene();
                stage.setScene(sc);
            }
        });

        BorderPane border = new BorderPane();
        border.setTop(createserverheader);
        border.setCenter(grid);
        border.setRight(LogoView);
        border.setStyle("-fx-background-color: #000000");

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        Scene scene = new Scene(border, bounds.getWidth(), bounds.getHeight());
        return scene;
    }

    public Scene getcreatingserver(String server_name, String element,String password,String maxplayers){
        GridPane createserverheader = getheader("CREATE SERVER");
        Platform.setImplicitExit(false);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Preferences prefs = Preferences.userRoot().node(packagePath);
        String player_name= prefs.get(PLAYER_NAME,"");
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
                pee.createserver(server_name,password,Integer.valueOf(maxplayers),gamemode,player_name,element);
                pee.joinListener = new ping.onJoinListener() {
                    @Override
                    public void onjoin(String name, String element, String ip) {
                        Platform.runLater(() -> grid.add(getplayerview(name,element), 0, 1));
                        System.err.print("\n"+"shit\n"+name+"\n");
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
        final Image logoPNG = new Image(UI.class.getResourceAsStream("../../res/back.png"));
        LogoView.setImage(logoPNG);
        LogoView.setFitWidth(50);
        LogoView.setFitHeight(50);
        LogoView.setTranslateX(-30);
        LogoView.setTranslateY(-50);
        LogoView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                Scene sc = getLandingScene();
                stage.setScene(sc);
                if(pee!=null) pee.Stop();
            }
        });


        Button btn = getButton("START GAME",gameScreen.LANDING);
        btn.setTranslateX(-60);
        btn.setTranslateY(30);
        btn.setMinWidth(100);
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

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        Scene scene = new Scene(border, bounds.getWidth(), bounds.getHeight());
        return scene;
    }
    public Scene getFindServerScene(){
        GridPane createserverheader = getheader("Finding Servers");
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
                pee.findserver();
                pee.joinListener = new ping.onJoinListener() {
                    @Override
                    public void onjoin(String name, String element, String ip) {
                    }

                    @Override
                    public void onfind(String name, String password, int maxPlayers, String mode,String IP) {
                        Platform.runLater(() -> grid.add(getServerView(name,mode,password,maxPlayers,IP), 0, 1));
                        System.err.print("\n"+"shit\n"+name+"\n");
                    }
                };
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        final ImageView LogoView = new ImageView();
        final Image logoPNG = new Image(UI.class.getResourceAsStream("../../res/back.png"));
        LogoView.setImage(logoPNG);
        LogoView.setFitWidth(50);
        LogoView.setFitHeight(50);
        LogoView.setTranslateX(-30);
        LogoView.setTranslateY(-50);
        LogoView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                Scene sc = getLandingScene();
                stage.setScene(sc);
                if(pee!=null) pee.Stop();
            }
        });


        BorderPane border = new BorderPane();
        border.setTop(createserverheader);
        border.setCenter(grid);
        border.setRight(LogoView);
        border.setStyle("-fx-background-color: #000000");

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        Scene scene = new Scene(border, bounds.getWidth(), bounds.getHeight());
        return scene;
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

        Label Pass = new Label(pass.toUpperCase());
        Pass.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        Pass.setPadding(new Insets(0,50,0,0));
        Pass.setTextFill(Color.valueOf("#B4B0AB"));

        Label Players = new Label(maxPlayers+"");
        Players.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        Players.setPadding(new Insets(0,50,0,0));
        Players.setTextFill(Color.valueOf("#B4B0AB"));

        Button btn = getButton("START GAME",gameScreen.LANDING);
        btn.setTranslateX(-60);
        btn.setTranslateY(30);
        btn.setMinWidth(100);
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                pee.joinserver("name","elemen",IP);
            }
        });

        HBox hBox = new HBox();
        hBox.getChildren().addAll(Name,Mode,Pass,Players,btn);
        return hBox;
    }
    public Scene getSettingsScene(){
        GridPane settingsHeader = getheader("SETTINGS");

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
            }
        });

        offFullScreen.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                offFullScreen.setTextFill(Color.valueOf("#B4B0AB"));
                onFullScreen.setTextFill(Color.valueOf("#333333"));
                prefs.put(FULLSCREEN,"1");
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

                    Scene sc = getLandingScene();
                    stage.setScene(sc);
            }
        });
        grid.add(btn,1,4);

        BorderPane border = new BorderPane();
        border.setTop(settingsHeader);
        border.setCenter(grid);
        border.setStyle("-fx-background-color: #000000");

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        Scene scene = new Scene(border, bounds.getWidth(), bounds.getHeight());
        return scene;
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
                    }
                    if(vol_type==1){
                        Preferences prefs = Preferences.userRoot().node(packagePath);
                        prefs.put(SFX_VOLUME,t+"");
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
                    Scene sc = getSettingsScene();
                    stage.setScene(sc);
                }
                if(gsc==gameScreen.LANDING){
                    Scene sc = getLandingScene();
                    stage.setScene(sc);
                }
                if(gsc==gameScreen.CREATESERVER){
                    Scene sc = getCreateServerScene();
                    stage.setScene(sc);
                }
                if(gsc==gameScreen.FINDSERVER){
                    Scene sc = getFindServerScene();
                    stage.setScene(sc);
                }


//                stage.sizeToScene();
//                stage.setFullScreen(false);
//                stage.setFullScreen(true);
//                stage.setMaximized(true);
//                stage.setFullScreen(true);
            }
        });
        return btn;
    }

    public GridPane getheader(String title){

        GridPane header = new GridPane();
        header.setPadding(new Insets(5));
        header.setHgap(10);
        header.setVgap(10);

        final ImageView LogoView = new ImageView();
        final Image logoPNG = new Image(UI.class.getResourceAsStream("../../res/logo_small.png"));
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

