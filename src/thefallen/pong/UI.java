package thefallen.pong;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class UI extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Ping Pong!");

        primaryStage.setScene(getLandingScene(primaryStage));

        primaryStage.show();
    }

    public Scene getLandingScene(Stage stage){

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

        Button btn = getButton("NEW GAME");
        grid.add(btn, 0, 0);

        Button btn2 = getButton("CREATE SERVER");
        grid.add(btn2, 0, 1);

        Button btn3 = getButton("FIND SERVERS");
        grid.add(btn3, 0, 2);

        Button btn4 = getButton("SETTINGS");
        grid.add(btn4, 0, 3);

        BorderPane border = new BorderPane();
        border.setTop(landingHeader);
        border.setCenter(grid);
        border.setStyle("-fx-background-color: #000000");

        Scene scene = new Scene(border, 700, 400,Color.AQUA);
        return scene;
    }

    public Button getButton(String text){
        Button btn4 = new Button(text);
        btn4.setFont(Font.loadFont(thefallen.pong.Resources.getResource(thefallen.pong.Resources.FONT1).toString(), 26));
        btn4.setMinWidth(250);
        btn4.setTextFill(Color.BLACK);
        btn4.setStyle("-fx-base: #B4B0AB;");
        btn4.setDefaultButton(false);

        btn4.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
//                Scene sc = new Scene(grid, 600, 330,Color.AQUA);
//                stage.setScene(sc);
            }
        });
        return btn4;
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
//        titleField.setFont(Font.font("ZinHenaBokuryu-RCF",FontWeight.BOLD,20));
        final HBox pictureRegion = new HBox();

        pictureRegion.getChildren().addAll(LogoView,titleField);
        header.add(pictureRegion, 1, 1);
        return header;
    }
}

