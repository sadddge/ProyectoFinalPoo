package com.example.proyectofinalpoov2;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.Objects;

public class Door extends Obstacle{
    private final GameController gameController;
    private final double maze;
    public Door(double x, double y, double width, double height, double flat, double maze,double scale, GameController gameController) {
        super(x, y, width, height);
        String name = flat == 1 ? "doorFlat.png" : "door.png";
        getImageView().setImage(new Image(Objects.requireNonNull
                (getClass().getResourceAsStream(name))));
        getImageView().setScaleY(scale);
        this.gameController = gameController;
        this.maze = maze;
    }

    public void activate(Player player) {
        Label message = new Label();
        message.setTextFill(Color.RED);
        message.setLayoutX(500);
        message.setLayoutY(350);
        message.setFont(Font.font(40));

        if (maze == 0) {
            player.setLevel(player.getLevel()+1);
        }
        if (player.getLevel() + 1 < maze){
            message.setText("Locked Level");
        } else if (player.getCoinCont() == gameController.getMazeActual().getCOINS()) {
            gameController.changeScene(maze);
            player.setCoinCont(0);
        } else {
            message.setText("Collect All Coins");
        }

        Platform.runLater(() -> gameController.getGamePane().getChildren().add(message));
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(2),
                ae -> {
                    // Remover el mensaje después de 2 segundos
                    Platform.runLater(() -> gameController.getGamePane().getChildren().remove(message));
                }));

        // Iniciar la línea de tiempo
        timeline.play();
    }
    @Override
    public void detectColision(Shape shape) {
    }

    @Override
    public void handleColision(Shape shape) {

    }


    @Override
    public void update() {

    }

    @Override
    public void repaint() {

    }
}
