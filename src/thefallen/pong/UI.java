package thefallen.pong;

import com.sun.glass.ui.EventLoop;
import com.sun.javafx.font.freetype.HBGlyphLayout;
import javafx.application.Application;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class UI extends Application {

    public Stage stage;
    public enum gameScreen{
        LANDING,NEWGAME,CREATESERVER,FINDSERVER,SETTINGS
    }
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

//        Text scenetitle = new Text("Welcome");
//        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
//        grid.add(scenetitle, 0, 0, 2, 1);
//
//        Label userName = new Label("User Name:");
//        grid.add(userName, 0, 1);
//
//        TextField userTextField = new TextField();
//        grid.add(userTextField, 1, 1);
//
//        Label pw = new Label("Password:");
//        grid.add(pw, 0, 2);
//
//        PasswordField pwBox = new PasswordField();
//        grid.add(pwBox, 1, 2);

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

        TextField userTextField = new TextField();
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

        HBox hBox = getVolumeHBox();
        hBox.setPadding(new Insets(5,5,5,20));
        grid.add(hBox,1,1);

        Label sfxvol= new Label("SFX VOLUME    :");
        sfxvol.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        sfxvol.setTextFill(Color.valueOf("#B4B0AB"));
        grid.add(sfxvol, 0, 2);

        HBox hBox2 = getVolumeHBox();
        hBox2.setPadding(new Insets(5,5,5,20));
        grid.add(hBox2,1,2);

        Label fullscreen= new Label("FULLSCREEN     :");
        fullscreen.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        fullscreen.setTextFill(Color.valueOf("#B4B0AB"));
        grid.add(fullscreen, 0, 3);

        Button onFullScreen = getsettingsButton("ON");
        Button offFullScreen= getsettingsButton("/0FF");

        HBox hbox3 = new HBox();
        hbox3.setPadding(new Insets(5,5,5,20));
        hbox3.getChildren().addAll(onFullScreen,offFullScreen);
        grid.add(hbox3,1,3);

        onFullScreen.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                    onFullScreen.setTextFill(Color.valueOf("#B4B0AB"));
                    offFullScreen.setTextFill(Color.valueOf("#333333"));
            }
        });

        offFullScreen.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                offFullScreen.setTextFill(Color.valueOf("#B4B0AB"));
                onFullScreen.setTextFill(Color.valueOf("#333333"));
            }
        });


        Button btn = getButton("BACK",gameScreen.LANDING);
        btn.setTranslateX(-60);
        btn.setTranslateY(30);
        btn.setMinWidth(100);
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

    public HBox getVolumeHBox(){
        Button[] buttons=new Button[10];

        for (int i = 0; i < 10; i++) {
            buttons[i] = getsettingsButton("I");
        }
        for (int i = 0; i < 10; i++) {
            final int t=i;
            buttons[i].setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent e) {
                    for (int j = 0; j <= t; j++) {
                        buttons[j].setTextFill(Color.valueOf("#B4B0AB"));
                    }
                    for (int j = t+1; j <10 ; j++) {
                        buttons[j].setTextFill(Color.valueOf("#333333"));
                    }
                }
            });
        }
        HBox hBox = new HBox();
        hBox.getChildren().addAll(buttons);
        return hBox;
    }

    public Button getsettingsButton(String str){
        Button btn = new Button(str);
        btn.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        btn.setStyle("-fx-background-color: transparent;");
        btn.setPadding(new Insets(0,3,0,0));
        btn.setMaxHeight(10);
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

