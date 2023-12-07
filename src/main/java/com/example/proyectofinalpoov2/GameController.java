package com.example.proyectofinalpoov2;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.*;
import java.util.*;

public class GameController implements Runnable{

    private GestorDB gestorDB;
    private User user;
    private Maze mazeActual;
    private Pane pauseMenu;
    private final Player player;
    private final Pane gamePane;
    private final Set<KeyCode> pressedKeys;
    private double mouseX;
    private double mouseY;
    private boolean pause;
    int fps = 60;

    Thread gameThread;

    public GameController() {
        //gestorDB = new GestorDB();

        this.mazeActual = maze(0);
        startPause();

        player = new Player(mazeActual.getStartX(),mazeActual.getStartY(),10);
        mazeActual.setPlayer(player);

        gamePane = new Pane();
        pressedKeys = new HashSet<>();


        gamePane.getChildren().add(mazeActual);
        gamePane.getChildren().add(player);
        gamePane.getChildren().add(player.getImageView());
    }



    private Maze maze(int n) {
        double startX = 640;
        double startY = 360;
        Label maze1 = new Label("MAZE 1");
        Label maze2 = new Label("MAZE 2");
        Label maze3 = new Label("MAZE 3");

        maze1.setFont(Font.font(25));
        maze2.setFont(Font.font(25));
        maze3.setFont(Font.font(25));

        maze1.setLayoutX(50);
        maze1.setLayoutY(340);
        maze2.setLayoutX(597);
        maze2.setLayoutY(42);
        maze3.setLayoutX(1140);
        maze3.setLayoutY(340);

        maze1.setTextFill(Color.WHITE);
        maze2.setTextFill(Color.RED);
        maze3.setTextFill(Color.RED);

        if (user != null) {
            int lvl = user.getLevel();

            if (lvl == 1) {
                maze2.setTextFill(Color.WHITE);
            } else if (lvl == 2){
                maze3.setTextFill(Color.WHITE);
            }
        }
        switch (n) {
            case 1 -> {
                startX = 40;
                startY = 365;
            }
            case 2 -> {
                startX = 35;
                startY = 265;
            }
            case 3 -> {
                startX = 15;
                startY = 675;
            }
        }

        Maze maze = new Maze(startX,startY);
        maze.setObstacles(loadObstacles(n));
        maze.setEntities(loadEntities(n));
        maze.setLines(loadLines(n));

        if (n == 0) {
            maze.getChildren().add(maze1);
            maze.getChildren().add(maze2);
            maze.getChildren().add(maze3);
        }
        return maze;
    }

    public void startPause() {
        pauseMenu = new Pane();
        ImageView resumeImage = new ImageView();
        ImageView loginImage = new ImageView();
        ImageView login2Image = new ImageView();
        ImageView backToMenuImage = new ImageView();
        TextField userField = new TextField();
        PasswordField passwordField = new PasswordField();
        Label message = new Label();
        message.setTextFill(Color.RED);
        message.setLayoutX(500);
        message.setLayoutY(40);
        message.setFont(Font.font(40));

        userField.setPromptText("User");
        passwordField.setPromptText("Password");

        userField.setPrefWidth(240);
        userField.setPrefHeight(37);

        passwordField.setPrefWidth(240);
        passwordField.setPrefHeight(37);

        userField.setLayoutX(511);
        userField.setLayoutY(250);

        passwordField.setLayoutX(511);
        passwordField.setLayoutY(350);

        Rectangle background = new Rectangle(1280,720);


        background.setOpacity(0.6);

        resumeImage.setFitWidth(320);
        resumeImage.setFitHeight(140);

        loginImage.setFitWidth(320);
        loginImage.setFitHeight(140);

        login2Image.setFitWidth(320);
        login2Image.setFitHeight(140);

        backToMenuImage.setFitWidth(320);
        backToMenuImage.setFitHeight(140);


        resumeImage.setLayoutX(480);
        resumeImage.setLayoutY(100);

        loginImage.setLayoutX(480);
        loginImage.setLayoutY(300);

        login2Image.setLayoutX(480);
        login2Image.setLayoutY(500);

        backToMenuImage.setLayoutX(480);
        backToMenuImage.setLayoutY(500);

        resumeImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("ResumeBotton.png"))));
        loginImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("LoginBotton.png"))));
        login2Image.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("LoginBotton.png"))));
        backToMenuImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("BackToMenuBotton.png"))));

        pauseMenu.getChildren().add(background);
        pauseMenu.getChildren().add(resumeImage);
        pauseMenu.getChildren().add(loginImage);
        pauseMenu.getChildren().add(backToMenuImage);

        resumeImage.setOnMouseClicked(mouseEvent -> {
            pause = false;
            closePause();
        });
        loginImage.setOnMouseClicked(mouseEvent -> {
            if (user == null){
                pauseMenu.getChildren().remove(resumeImage);
                pauseMenu.getChildren().remove(loginImage);
                pauseMenu.getChildren().remove(backToMenuImage);


                pauseMenu.getChildren().add(userField);
                pauseMenu.getChildren().add(passwordField);
                pauseMenu.getChildren().add(login2Image);
            } else {
                message.setText("Already Logged");
                Platform.runLater(() -> getGamePane().getChildren().add(message));
                Timeline timeline = new Timeline(new KeyFrame(
                        Duration.seconds(2),
                        ae -> Platform.runLater(() -> getGamePane().getChildren().remove(message))));

                timeline.play();
            }
        });

        login2Image.setOnMouseClicked(mouseEvent -> {


            if (!(userField.getText().isBlank() || passwordField.getText().isBlank())) {
                if (gestorDB.isUserExists(userField.getText())) {
                    User userDB = gestorDB.findUser(userField.getText());

                    if (passwordField.getText().equals(userDB.getPassword())) {
                        message.setText("Logged");
                        user = userDB;
                        player.setLevel(user.getLevel());
                    } else {
                        message.setText("Wrong Password");
                    }
                } else {
                    gestorDB.addUser(userField.getText(), passwordField.getText());
                    message.setText("Registered Successfully");
                }
                Platform.runLater(() -> getGamePane().getChildren().add(message));
                Timeline timeline = new Timeline(new KeyFrame(
                        Duration.seconds(2),
                        ae -> Platform.runLater(() -> getGamePane().getChildren().remove(message))));

                timeline.play();

                closePause();
                pauseMenu.getChildren().remove(userField);
                pauseMenu.getChildren().remove(passwordField);
                pauseMenu.getChildren().remove(login2Image);
                pauseMenu.getChildren().add(resumeImage);
                pauseMenu.getChildren().add(loginImage);
                pauseMenu.getChildren().add(backToMenuImage);
                openPause();
            }
        });

        backToMenuImage.setOnMouseClicked(mouseEvent -> {
            pause = false;
            closePause();
            changeScene(0);
            player.setCoinCont(0);
            if (user != null) {
                gestorDB.updateUserLevel(user.getName(), user.getLevel());
            }
        });

    }



    public void startGameThread() {
        gamePane.getScene().setOnMouseMoved(this::updateMouse);
        gamePane.getScene().setOnKeyPressed(keyEvent -> pressedKeys.add(keyEvent.getCode()));
        gamePane.getScene().setOnKeyReleased(keyEvent -> pressedKeys.remove(keyEvent.getCode()));


        gameThread = new Thread(this);
        gameThread.start();
    }

    public void updateMouse(MouseEvent mouseEvent) {
        mouseX = mouseEvent.getX();
        mouseY = mouseEvent.getY();
    }

    @Override
    public void run() {
        double drawInterval = (double) 1000000000 / fps;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            while (delta >= 1) {
                if (pause) {
                    updatePause();
                } else {
                    update();
                    repaint();
                }

                delta--;
            }

            if (timer >= 1000000000) {
                timer = 0;
            }

        }
    }

    private void openPause() {
        Platform.runLater(() -> gamePane.getChildren().add(pauseMenu));
    }

    private void closePause() {
        Platform.runLater(() -> gamePane.getChildren().remove(pauseMenu));
    }

    private void updatePause() {
        if (pressedKeys.contains(KeyCode.ESCAPE)) {
            pause = !pause;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Upss, eso no tuvo que haber salido asi");
                e.printStackTrace();
            }
            if (pause) {
                openPause();
            } else {
                closePause();
            }

        }
    }



    private void update() {
        player.updateMouse(mouseX, mouseY);
        player.update();
        player.updateCanWalk(mazeActual.getObstacles());
        player.setPressedKeys(pressedKeys);
        mazeActual.update();
        updatePause();
    }
    private void repaint() {
        player.repaint();
        mazeActual.repaint();
    }

    public void changeScene(double n) {

        Platform.runLater(() -> {
            gamePane.getChildren().remove(this.mazeActual);
            gamePane.getChildren().remove(player);
            mazeActual = maze((int)n);
            mazeActual.setPlayer(player);
            gamePane.getChildren().add(player);
            gamePane.getChildren().add(this.mazeActual);
            player.moveTo(mazeActual.getStartX(),mazeActual.getStartY());
            if (n == 3) player.setRad(5);
            if (n == 0) player.setRad(10);
        });
    }


    public Pane getGamePane() {
        return gamePane;
    }

    public Maze getMazeActual() {
        return mazeActual;
    }

    private List<Line> loadLines(int num) {
        String line;
        List<Line> lines = new ArrayList<>();

        String ruta = "";

        switch (num) {
            case 0 -> ruta = "/com/example/proyectofinalpoov2/menu.txt";
            case 1 -> ruta = "/com/example/proyectofinalpoov2/maze1.txt";
            case 2 -> ruta = "/com/example/proyectofinalpoov2/maze2.txt";
            case 3 -> ruta = "/com/example/proyectofinalpoov2/maze3.txt";
        }


        try {

            InputStream inputStream = getClass().getResourceAsStream(ruta);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            while ((line = br.readLine()) != null){

                String[] parts = line.split(",");

                int tipoObstaculo = Integer.parseInt(parts[0]);

                if (tipoObstaculo == 3) {
                    Line line1 = new Line(Double.parseDouble(parts[1]),
                            Double.parseDouble(parts[2]),
                            Double.parseDouble(parts[3]),
                            Double.parseDouble(parts[4]));
                    line1.setLayoutX(Double.parseDouble(parts[5]));
                    line1.setLayoutY(Double.parseDouble(parts[6]));
                    lines.add(line1);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lines;
    }

    private List<Obstacle> loadObstacles(int num) {
        String line;
        List<Obstacle> obstacles = new ArrayList<>();

        String ruta = "";

        switch (num) {
            case 0 -> ruta = "/com/example/proyectofinalpoov2/menu.txt";
            case 1 -> ruta = "/com/example/proyectofinalpoov2/maze1.txt";
            case 2 -> ruta = "/com/example/proyectofinalpoov2/maze2.txt";
            case 3 -> ruta = "/com/example/proyectofinalpoov2/maze3.txt";
        }


        try {

            InputStream inputStream = getClass().getResourceAsStream(ruta);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            while ((line = br.readLine()) != null){

                String[] parts = line.split(",");

                int tipoObstaculo = Integer.parseInt(parts[0]);

                switch (tipoObstaculo) {
                    case 1 -> // Wall

                            obstacles.add(new Wall(
                                    Double.parseDouble(parts[1]),
                                    Double.parseDouble(parts[2]),
                                    Double.parseDouble(parts[3]),
                                    Double.parseDouble(parts[4])
                            ));

                    case 2 -> // Door

                            obstacles.add(new Door(
                                    Double.parseDouble(parts[1]),
                                    Double.parseDouble(parts[2]),
                                    Double.parseDouble(parts[3]),
                                    Double.parseDouble(parts[4]),
                                    Double.parseDouble(parts[5]),
                                    Double.parseDouble(parts[6]),
                                    Double.parseDouble(parts[7]),
                                    this
                            ));

                    default -> {
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return obstacles;
    }

    private List<Entity> loadEntities(int num) {
        String line;
        List<Entity> entities = new ArrayList<>();

        String ruta = "";

        switch (num) {
            case 0 -> ruta = "/com/example/proyectofinalpoov2/menu.txt";
            case 1 -> ruta = "/com/example/proyectofinalpoov2/maze1.txt";
            case 2 -> ruta = "/com/example/proyectofinalpoov2/maze2.txt";
            case 3 -> ruta = "/com/example/proyectofinalpoov2/maze3.txt";
        }


        try {

            InputStream inputStream = getClass().getResourceAsStream(ruta);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            while ((line = br.readLine()) != null){

                String[] parts = line.split(",");

                int tipoObstaculo = Integer.parseInt(parts[0]);

                switch (tipoObstaculo) {
                    case 4 -> // Enemy

                            entities.add(new Enemy(
                                    Double.parseDouble(parts[1]),
                                    Double.parseDouble(parts[2]),
                                    Double.parseDouble(parts[3]),
                                    Double.parseDouble(parts[4]),
                                    Double.parseDouble(parts[5]),
                                    Double.parseDouble(parts[6])
                            ));
                    case 5-> //Coin

                            entities.add(new Coin(
                                    Double.parseDouble(parts[1]),
                                    Double.parseDouble(parts[2])
                            ));

                    default -> {
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error inesperado ha ocurrido");
            throw new RuntimeException(e);
        }
        return entities;
    }
}
