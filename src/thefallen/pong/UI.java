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

        GridPane landingHeader= getheader("Ping Pong");
        //test
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Welcome");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        Scene sc = new Scene(grid, 600, 330,Color.AQUA);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("Sign in button pressed");
                primaryStage.setScene(sc);
            }
        });

        //test

        BorderPane border = new BorderPane();
        border.setTop(landingHeader);
        border.setCenter(grid);
        border.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #dc143c, #661a33)");

        Scene scene = new Scene(border, 600, 330,Color.AQUA);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public GridPane getheader(String title){
        GridPane header = new GridPane();
        header.setPadding(new Insets(5));
        header.setHgap(10);
        header.setVgap(10);

        final ImageView imv = new ImageView();
        final Image image2 = new Image(UI.class.getResourceAsStream("res/logo_small.png"));
        imv.setImage(image2);
        Text tit = new Text(title);
        tit.setFont(Font.font("ZinHenaBokuryu-RCF", FontWeight.NORMAL, 20));

        final HBox pictureRegion = new HBox();

        pictureRegion.getChildren().addAll(imv,tit);
        header.add(pictureRegion, 1, 1);
        return header;
    }
}

