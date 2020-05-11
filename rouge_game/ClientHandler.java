import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class ClientHandler extends Thread {
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    String playerID;
    Game game;
    int xPos;
    int yPos;
    private boolean done;

    // Constructor
    public ClientHandler(String playerID, Socket s, DataInputStream dis, DataOutputStream dos, int xPos, int yPos)
            throws IOException {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.playerID = playerID;
        this.game = Server.game;
        this.xPos = xPos;
        this.yPos = yPos;
        this.done = false;
    }

    @Override
    public void run() {
        String received;
        game.addPlayer(new Player(playerID, xPos, yPos, 10, false));
        String allplayers = game.getPlayers();
        String allSwords = game.getSwords();

        try {
            dos.writeUTF(playerID);
            dos.writeUTF(allplayers);
            dos.writeUTF(allSwords);
            eventHandler("PLAYER_JOINED");
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {

                received = dis.readUTF();

                if (received.equals("EXIT")) {
                    System.out.println("Client " + this.playerID + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.s.close();
                    Server.removeThread(playerID);
                    Server.sendAll("GAME_OVER," + this.playerID);
                    System.out.println("Connection closed");
                    break;
                }
                if (received.equals("GAME_OVER") && !this.done) {
                    String winnerName = "none";
                    System.out.println("Game over for " + this.playerID);
                    Server.sendAll("GAME_OVER," + this.playerID);
                    HashMap<String, Player> playerObj = game.getAllPlayerObj();
                    int i = 0;
                    for (Map.Entry<String, Player> p : playerObj.entrySet()) {
                        if(!p.getValue().gameOver){
                            i++;
                            winnerName = p.getValue().getName();
                        }
                    }
                    if (i == 1){ Server.sendAll("PLAYER_WON," + winnerName); }
                    this.done = true;
                     
                }
                if (!game.getPlayer(playerID).gameOver) {
                    eventHandler(received);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            // closing resources
            this.dis.close();
            this.dos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void eventHandler(String event) throws IOException {
        Player p;
        String command;
        int xTmp;
        int yTmp;
        switch (event) {
            case "PLAYER_JOINED":
                System.out.println(playerID + " joined the game");
                command = "PLAYER_JOINED," + playerID + "," + xPos + "," + yPos;
                Server.sendAll(command);
                break;

            case "MOVE_UP":
                p = game.getPlayer(playerID);
                if (validMove(p.getX(), p.getY() - 10)) {
                    p.move_up();
                    moveEventHelper("MOVE_UP,", p);
                }
                break;

            case "MOVE_DOWN":
                p = game.getPlayer(playerID);
                if (validMove(p.getX(), p.getY() + 10)) {
                    p.move_down();
                    moveEventHelper("MOVE_DOWN,", p);
                }
                break;

            case "MOVE_RIGHT":
                p = game.getPlayer(playerID);
                if (validMove(p.getX() + 10, p.getY())) {
                    p.move_right();
                    moveEventHelper("MOVE_RIGHT,", p);
                }
                break;

            case "MOVE_LEFT":
                p = game.getPlayer(playerID);
                if (validMove(p.getX() - 10, p.getY())) {
                    p.move_left();
                    moveEventHelper("MOVE_LEFT,", p);
                }
                break;

            case "ATTACK":
                p = game.getPlayer(playerID);
                validAtk(p);
                p.attack();
                break;
        }

    }

    private void moveEventHelper(String cmdWord, Player p) throws IOException {
        String command;
        game.updatePlayer(p);
        command = cmdWord + playerID;
        Server.sendAll(command);
    }

    private boolean validMove(int posX, int posY) {
        HashMap<String, Player> playerList = game.getAllPlayerObj();
        Player player =  game.getPlayer(playerID);
        String posState = player.positionState;

        for (Map.Entry<String, Player> p : playerList.entrySet()) {

            if (p.getValue().getName() != playerID) {

                int x2 = p.getValue().getX();
                int y2 = p.getValue().getY();

                if (posState.equals("right") && posX + 20 >= x2 && posX < x2 + 40 && posY >= y2-50 && posY <= y2 + 40) {
                    System.out.println("invalid move");
                    return false;

                 } else if (posState.equals("left") && posX <= x2 + 40 && posX + 20 > x2 &&  posY >= y2-50 && posY <= y2 + 40) {
                    System.out.println("invalid move");
                    return false;

                } else if (posState.equals("down") && posY <= y2 && posY > y2 - 20 && posX >= x2 -50 && posX <= x2 + 50) {
                    System.out.println("invalid move");
                    return false;

                }  else if (posState.equals("up") && posY < y2 + 20 && posY >= y2 && posX >= x2 -50 && posX <= x2 + 50) {
                    System.out.println("invalid move");
                    return false;

                }
            }
        }

        if (posX > 1 && posX < 969 && posY > 67 && posY < 653) {
            return true;
        } else {
            System.out.println("invalid move");
            return false;
        }
    }

    private void validAtk(Player p) throws IOException {
        int x = p.getX();
        int y = p.getY();
        String posState = p.positionState;

        if (p.hasSword) {
            HashMap<String, Player> playerObj = game.getAllPlayerObj();
            for (Map.Entry<String, Player> otherP : playerObj.entrySet()) {
                int x2 = otherP.getValue().getX();
                int y2 = otherP.getValue().getY();

                if (otherP.getValue().getName() != playerID) {
                    if (posState.equals("right") && x2 <= x + 70 && x2 >= x && y2 <= y + 50 && y2 >= y - 45) {
                        atkHelper(otherP.getValue(), p);
                        break;
                    } else if (posState.equals("left") && x2 >= x - 50 && x2 <= x && y2 <= y + 50 && y2 >= y - 45) {
                        atkHelper(otherP.getValue(), p);
                        break;
                    } else if (posState.equals("up") && x2 <= x + 60 && x2 >= x - 10 && y2 >= y - 70 && y2 <= y) {
                        atkHelper(otherP.getValue(), p);
                        break;
                    } else if (posState.equals("down") && x2 <= x + 60 && x2 >= x - 10 && y2 <= y + 70 && y2 >= y) {
                        atkHelper(otherP.getValue(), p);
                        break;
                    }
                }
            }
            System.out.println(playerID + " attacked");
            String command = "ATTACK," + playerID;
            Server.sendAll(command);
        } else {
            ArrayList<ArrayList<Integer>> swordObj = game.getAllSwordObj();
            int index = 0;
            for (ArrayList<Integer> s : swordObj) {
                int xS = s.get(0);
                int yS = s.get(1);

                if (posState.equals("right") && xS <= x + 70 && xS >= x && yS <= y + 50 && yS >= y - 45) {
                    pickupItem(index, p);
                    break;
                } else if (posState.equals("left") && xS >= x - 50 && xS <= x && yS <= y + 50 && yS >= y - 45) {
                    pickupItem(index, p);
                    break;
                } else if (posState.equals("up") && xS <= x + 60 && xS >= x - 10 && yS >= y - 70 && yS <= y) {
                    pickupItem(index, p);
                    break;
                } else if (posState.equals("down") && xS <= x + 60 && xS >= x - 10 && yS <= y + 70 && yS >= y) {
                    pickupItem(index, p);
                    break;
                }
                index++;
            }
        }
        game.updatePlayer(p);
    }

    public void atkHelper(Player otherP, Player p) throws IOException {
        otherP.loseHealth();
        game.updatePlayer(otherP);
        p.playerLostSword();
        System.out.println(playerID + " ATTACKED " + otherP.getName() + " total healt:" + otherP.getHealth());
        String command = "LOSE_HEALTH," + otherP.getName() + "," + p.getName();
        Server.sendAll(command);

    }

    public void pickupItem(int index, Player p) throws IOException {
        game.removeSword(index);
        p.playerGotSword();
        System.out.println(p.getName() + " picked up an item");
        String command = "PICK_UP," + p.getName() + "," + index;
        Server.sendAll(command);
    }

    public void send(String event) throws IOException {
        dos.writeUTF(event);
    }

}