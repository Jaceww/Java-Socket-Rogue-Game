import java.io.*;
import java.util.*;

public class Game {
    private HashMap<String, Player> players;
    private ArrayList<ArrayList<Integer>> swords;

    public Game() {
        this.players = new HashMap<String, Player>();
        this.swords = new ArrayList<ArrayList<Integer>>();
        Timer timer = new Timer();
        timer.schedule(new SwordSpawn(), 0, 8000);
    }

    public void addPlayer(Player p) {
        players.put(p.getName(), p);
    }

    public String getPlayers() {
        String allPlayers = "";
        for (Map.Entry<String, Player> p : players.entrySet()) {
            allPlayers += p.getValue().getName() + "-" + p.getValue().getX() + "-" + p.getValue().getY() + "-"
                    + p.getValue().hasSword + ">";
        }
        allPlayers = allPlayers.substring(0, allPlayers.length() - 1);
        return allPlayers;
    }

    public String getSwords() {
        String allSwords = "";
        for (ArrayList<Integer> s : swords) {
            allSwords += s.get(0) + "-" + s.get(1) + ">";
        }
        allSwords = allSwords.substring(0, allSwords.length() - 1);
        return allSwords;
    }

    public HashMap<String, Player> getAllPlayerObj() {
        return this.players;
    }

    public ArrayList<ArrayList<Integer>> getAllSwordObj() {
        return this.swords;
    }

    public Player getPlayer(String name) {
        return players.get(name);
    }

    public void updatePlayer(Player p) {
        players.put(p.getName(), p);
    }

    public void removePlayer(String playerID) {
        players.remove(playerID);
    }

    public void addSword(ArrayList<Integer> pos) {
        swords.add(pos);
    }

    public void removeSword(int index) {
        swords.remove(index);
    }
}

class SwordSpawn extends TimerTask {
    public void run() {
        if (Server.game.getAllSwordObj().size() < 5) {
            Random r = new Random();
            ArrayList<Integer> pos = new ArrayList<Integer>();
            int xPos = r.nextInt(890) + 10;
            int yPos = r.nextInt(590) + 60;
            pos.add(xPos);
            pos.add(yPos);
            Server.game.addSword(pos);
            System.out.println("sword spawn at " + xPos + ":" + yPos);
            String command = "SWORD_SPAWN," + xPos + "," + yPos;
            try {
                Server.sendAll(command);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}