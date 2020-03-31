import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameWindow extends JPanel {
    HashMap<String, Player> players;
    ArrayList<ArrayList<Integer>> swords;
    Player player;
    private Image map = new ImageIcon("images/map.png").getImage();
    private Image swordImg = new ImageIcon("images/swordUp.png").getImage();
    private Image youWon = new ImageIcon("images/youWon.png").getImage();
    private Image youLost = new ImageIcon("images/youLost.png").getImage();

    public GameWindow(HashMap<String, Player> players, Player player) {
        this.players = players;
        this.player = player;
        this.swords = new ArrayList<ArrayList<Integer>>(); // Byt till att man det från servern precis som players
    }

    @Override
    protected void paintComponent(Graphics g) {
        Player currentPlayer = players.get(player.getName());
        super.paintComponent(g);
        Graphics2D o = (Graphics2D) g;

        o.drawImage(map, 0, 0, null);

        for (ArrayList<Integer> s : swords) {
            int xPos = s.get(0);
            int yPos = s.get(1);
            o.drawImage(swordImg, xPos, yPos, null);
            o.drawString("Press e to pickup", xPos, yPos - 10);
        }

        for (Map.Entry<String, Player> p : players.entrySet()) {
            o.drawImage(p.getValue().playerImage, p.getValue().getX(), p.getValue().getY(), null);
            if (p.getValue().hasSword) {
                o.drawImage(p.getValue().weapon, p.getValue().weaponX, p.getValue().weaponY, null);
            }
            o.drawString(p.getValue().getName(), p.getValue().getX(), p.getValue().getY() - 10);
        }

        o.drawImage(currentPlayer.heart1, 850, 10, null);
        o.drawImage(currentPlayer.heart2, 875, 10, null);
        o.drawImage(currentPlayer.heart3, 900, 10, null);
        o.drawImage(currentPlayer.heart4, 925, 10, null);

        if (currentPlayer.gameOver) {
            o.drawImage(youLost, 0, 0, null);
            try {
                Client.gameOverPlayer(currentPlayer.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if(currentPlayer.gameWon){
            o.drawImage(youWon, 0, 0, null);
        }
    }

    public void addPlayer(Player p) {
        players.put(p.getName(), p);
    }

    public void updatePlayers(HashMap<String, Player> p) {
        players = p;
    }

    public void swordSpawn(int xPos, int yPos) {
        ArrayList<Integer> tmp = new ArrayList<Integer>();
        tmp.add(xPos);
        tmp.add(yPos);
        swords.add(tmp);
    }

    public void removeSword(int i) {
        System.out.println(i);
        swords.remove(i);
    }
}