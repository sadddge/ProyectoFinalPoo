package com.example.proyectofinalpoov2;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Main extends Application {

    private final Pane root = new Pane();

    private Parent createContent() {
        root.setPrefSize(1280, 720);
        return root;
    }

    @Override
    public void start(Stage stage) {

        GameController controller = new GameController();

        root.getChildren().add(controller.getGamePane());

        Scene scene = new Scene(createContent());

        controller.startGameThread();

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();


    }

    public static void main(String[] args) {
        launch(args);
    }


}
