import java.io.*;
import java.util.ArrayList;

import java.awt.*;
import java.util.List;

import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import javax.swing.event.*;

class Player implements Serializable {
    private ArrayList<Object> invetory = new ArrayList<>();
    private String name;
    private int health;
    private int xPosition;
    private int yPosition;
    private int speed;
    boolean gameOver;
    boolean gameWon;
    int weaponX;
    int weaponY;
    String positionState;
    boolean hasSword;

    Image playerImage = new ImageIcon("images/playerDown.png").getImage();
    private Image playerRight = new ImageIcon("images/playerRight.png").getImage();
    private Image playerLeft = new ImageIcon("images/playerLeft.png").getImage();
    private Image playerDown = new ImageIcon("images/playerDown.png").getImage();
    private Image playerUp = new ImageIcon("images/playerUp.png").getImage();

    Image weapon = new ImageIcon("images/swordDown.png").getImage();
    private Image weaponDown = new ImageIcon("images/swordDown.png").getImage();
    private Image weaponUp = new ImageIcon("images/swordUp.png").getImage();
    private Image weaponLeft = new ImageIcon("images/swordLeft.png").getImage();
    private Image weaponRight = new ImageIcon("images/swordRight.png").getImage();

    private Image weaponAtkDown = new ImageIcon("images/swordAtkDown.png").getImage();
    private Image weaponAtkUp = new ImageIcon("images/swordAtkUp.png").getImage();
    private Image weaponAtkLeft = new ImageIcon("images/swordAtkLeft.png").getImage();
    private Image weaponAtkRight = new ImageIcon("images/swordAtkRight.png").getImage();

    private Image fullHeart = new ImageIcon("images/fullHeart.png").getImage();
    private Image emptyHeart = new ImageIcon("images/emptyHeart.png").getImage();
    Image heart1;
    Image heart2;
    Image heart3;
    Image heart4;

    List<ChangeListener> listeners = new ArrayList<ChangeListener>();

    public Player(String name, int xPos, int yPos, int speed, boolean hasSword) {
        this.name = name;
        this.health = 4;
        this.xPosition = xPos;
        this.yPosition = yPos;
        this.speed = speed;
        this.weaponX = xPos;
        this.weaponY = yPos + 30;
        this.positionState = "down";
        this.hasSword = hasSword;
        this.heart1 = this.fullHeart;
        this.heart2 = this.fullHeart;
        this.heart3 = this.fullHeart;
        this.heart4 = this.fullHeart;
        this.gameOver = false;
        this.gameWon = false;
    }

    public void move_right() {
        this.xPosition = this.xPosition + speed;
        this.weaponX = this.xPosition + speed + 15;
        this.weaponY = this.yPosition - 15;
        this.playerImage = this.playerRight;
        this.weapon = this.weaponRight;
        this.positionState = "right";
    }

    public void move_left() {
        this.xPosition = this.xPosition - speed;
        this.weaponX = this.xPosition - speed - 30;
        this.weaponY = this.yPosition + 25;
        this.playerImage = this.playerLeft;
        this.weapon = this.weaponLeft;
        this.positionState = "left";
    }

    public void move_down() {
        this.yPosition = this.yPosition + speed;
        this.weaponY = this.yPosition + speed + 20;
        this.weaponX = this.xPosition + 25;
        this.playerImage = this.playerDown;
        this.weapon = this.weaponDown;
        this.positionState = "down";
    }

    public void move_up() {
        this.yPosition = this.yPosition - speed;
        this.weaponY = this.yPosition - speed - 35;
        this.weaponX = this.xPosition + 25;
        this.playerImage = this.playerUp;
        this.weapon = this.weaponUp;
        this.positionState = "up";
    }

    public void attack() {
        if (hasSword) {
            switch (this.positionState) {
                case "right":
                    this.weapon = this.weaponAtkRight;
                    break;
                case "left":
                    this.weapon = this.weaponAtkLeft;
                    break;
                case "up":
                    this.weapon = this.weaponAtkUp;
                    break;
                case "down":
                    this.weapon = this.weaponAtkDown;
                    break;
            }
        }
    }

    public void loseHealth() {
        this.health--;
        switch (this.health) {
            case 3:
                this.heart1 = this.emptyHeart;
                break;
            case 2:
                this.heart2 = this.emptyHeart;
                break;
            case 1:
                this.heart3 = this.emptyHeart;
                break;
            case 0:
                this.heart4 = this.emptyHeart;
                this.gameOver = true;
                break;

        }
    }

    public void dead(){
        this.playerImage = new ImageIcon("images/deadPlayer.png").getImage();
    }

    public void playerWon(){
        this.gameWon = true;
    }

    public void playerGotSword(){
        this.hasSword = true;
    }

    public void playerLostSword(){
        this.hasSword = false;
    }

    public int getX() {
        return this.xPosition;
    }

    public int getY() {
        return this.yPosition;
    }

    public String getName() {
        return this.name;
    }

    public int getHealth() {
        return this.health;
    }
}