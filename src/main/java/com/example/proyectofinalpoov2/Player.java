package com.example.proyectofinalpoov2;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;

import java.util.*;

public class Player extends Entity {

    private Set<KeyCode> pressedKeys;
    private double mouseX;
    private double mouseY;
    private int coinCont;
    private int level;

    public Player(double x, double y, double radio) {
        super(x, y, radio);
        Image im = new Image(Objects.requireNonNull(getClass().getResourceAsStream("ogPlayer.png")));
        getImageView().setImage(im);
        getImageView().setScaleX(0.05);
        getImageView().setScaleY(0.05);
        setImgX(getX()-im.getWidth()/2);
        setImgY(getY()-im.getHeight()/2);
        level = 0;

        coinCont = 0;
        pressedKeys = new HashSet<>();
        setWeapon(new Weapon(20, this));
    }

    public void updateMouse(double mouseX, double mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
    @Override
    public void update() {

        setRadius(getRad());

        if (getHp() <= 0) {
            setDead(true);
        }

        if (pressedKeys.contains(KeyCode.W)) {
            if (isCanWalkW()) {
                moveUp();
            }
        }
        if (pressedKeys.contains(KeyCode.S)) {
            if (isCanWalkS()) {
                moveDown();
            }
        }
        if (pressedKeys.contains(KeyCode.A)) {
            if (isCanWalkA()) {
                moveLeft();
            }
        }
        if (pressedKeys.contains(KeyCode.D)) {
            if (isCanWalkD()) {
                moveRight();
            }
        }


        if (pressedKeys.contains(KeyCode.F) ) {
            shot();
        }

        getWeapon().update();
    }

    @Override
    public void repaint() {
        Platform.runLater(() -> {
            setCenterX(getX());
            setCenterY(getY());
            getImageView().setX(getX());
            getImageView().setY(getY());
            getWeapon().repaint();
        });
    }

    private void shot() {
        getWeapon().shot(mouseX, mouseY);
    }



    public void shotWithMouse(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            shot();
        }
    }


    @Override
    public void detectColision(Shape shape) {
        if (shape instanceof Projectile || shape instanceof Coin){
            if (shape.getBoundsInParent().intersects(getBoundsInParent())) {
                handleColision(shape);
            }
        }

        if (shape.contains(getX(),getY())) {
            handleColision(shape);
        }
    }

    @Override
    public void handleColision(Shape shape) {
        if (shape instanceof Coin coin) {
            if (!coin.isDead()) {
                coin.die();
                coinCont++;
            }
        } else if (shape instanceof Door door) {
            door.activate(this);
        } else if (shape instanceof Projectile projectile) {
            if (!projectile.getSource().getOwner().equals(this)) {
                setHp(getHp()-(projectile.getSource().getDamage()/25));
                projectile.setVelocity(0,0);
                projectile.moveTo(2000,2000);
            }
        }
    }

    public void setPressedKeys(Set<KeyCode> pressedKeys) {
        this.pressedKeys = pressedKeys;
    }

    public int getCoinCont() {
        return coinCont;
    }

    public void setCoinCont(int coinCont) {
        this.coinCont = coinCont;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

}
