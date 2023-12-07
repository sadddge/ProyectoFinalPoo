package com.example.proyectofinalpoov2;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

public class Maze extends Pane implements IUpdate{

    private List<Obstacle> obstacles;
    private List<Entity> entities;
    private List<Line> lines;
    private Player player;
    private int COINS;
    private final double startX;
    private final double startY;

    public Maze(double startX, double startY) {
        this.startX = startX;
        this.startY = startY;
        lines = new ArrayList<>();
        COINS = 0;
    }

    public void setObstacles(List<Obstacle> obstacles) {
        this.obstacles = obstacles;
        obstacles.forEach(obstacle -> {
            getChildren().add(obstacle);
            getChildren().add(obstacle.getImageView());
        });
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
        if (entities != null) {
            entities.forEach(entity -> {
                getChildren().add(entity.getImageView());
                if (entity instanceof Coin) {
                    COINS++;
                }
            });
        }
    }

    public int getCOINS() {
        return COINS;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }
    @Override
    public void update() {
        updateDead();
        updateEntidades();
        updateProjectiles();
        checkCollisions();
    }
    private void updateDead() {
        if (player.isDead()) {
            player.moveTo(startX,startY);
            player.setHp(100);
            player.setDead(false);
        }
    }
    @Override
    public void repaint() {
        repaintEntidades();
    }

    public void updateEntidades() {
        if (entities != null) {
            entities.stream().filter(entity -> !entity.isDead()).forEach(Entity::update);
            entities.stream().filter(entity -> !entity.isDead()).forEach(entity -> {
                if (entity instanceof Enemy enemy) {
                    enemy.updateCanWalk(obstacles);
                    enemy.getTriangles().forEach(triangle -> Platform.runLater(() -> getChildren().remove(triangle)));
                    enemy.updateRays(lines);
                    enemy.checkPlayerInSight(player);
                }
            });
        }
    }
    public void repaintEntidades(){
        if (entities != null) {
            entities.stream().filter(entity -> !entity.isDead()).forEach(Entity::repaint);
            entities.stream().filter(entity -> !entity.isDead()).forEach(entity -> {
                if (entity instanceof Enemy enemy) {
                    enemy.getTriangles().forEach(triangle -> Platform.runLater(() -> getChildren().add(triangle)));
                }
            });
            entities.stream().filter(Entity::isDead).forEach(entity ->
                Platform.runLater(()-> {
                    getChildren().remove(entity);
                    if (entity instanceof Enemy enemy) {
                        getChildren().removeAll((enemy.getTriangles()));
                        getChildren().removeAll(entity.getWeapon().getProjectiles());
                    }
                }));
        }
    }
    private void updateProjectiles() {
        Platform.runLater(() -> getChildren().removeAll(player.getWeapon().getProjectiles()));

        entities.stream().filter(entity -> entity instanceof Enemy)
                .forEach(entity -> Platform.runLater(() ->
                        getChildren().removeAll(entity.getWeapon().getProjectiles())));

        Platform.runLater(() -> getChildren().addAll(player.getWeapon().getProjectiles()));

        entities.stream().filter(entity -> entity instanceof Enemy)
                .forEach(entity -> Platform.runLater(() ->
                        getChildren().addAll(entity.getWeapon().getProjectiles())));
    }

    private void checkCollisions() {
        obstacles.forEach(player::detectColision);
        player.getWeapon().getProjectiles().forEach(projectile -> {
            obstacles.forEach(projectile::detectColision);
            entities.forEach(entity -> entity.detectColision(projectile));
        });

        entities.forEach(entity -> {
            obstacles.forEach(obstacle -> {
                entity.detectColision(obstacle);
                if (entity instanceof Enemy) {
                    entity.getWeapon().getProjectiles().forEach(projectile -> {
                        projectile.detectColision(obstacle);
                        player.detectColision(projectile);
                    });
                }
            });

            entity.detectColision(player);
            player.detectColision(entity);
        });
    }

    public double getStartX() {
        return startX;
    }
    public double getStartY() {
        return startY;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }
}
