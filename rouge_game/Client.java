import java.io.IOException;
import java.awt.*;
import java.net.Socket;
import java.util.HashMap;
import java.io.*;
import java.awt.event.*;
import javax.swing.JFrame;

public class Client extends JFrame {
    DataInputStream dis;
    static DataOutputStream dos;
    PrintWriter outputStream;
    GameWindow gameWindow;
    Player player;
    HashMap<String, Player> players = new HashMap<String, Player>();

    public Client() {
        super("Polari");
        // Try connecting or die
        System.out.println("Starting up client...");
        try {
            final Socket socket = new Socket("localhost", 2222);

            // Setup input stream
            dis = new DataInputStream(socket.getInputStream());

            // If connected display game
            initializeGame();

            // Setup output stream
            System.out.println("Setting up output stream...");
            dos = new DataOutputStream(socket.getOutputStream());
            System.out.println("Output stream done! ");

            // Setup keylistner
            addKeyListener(new KeyInput());

            // Setup reciever
            String recived;
            while (true) {
                recived = dis.readUTF();
                handleEvent(recived);
                gameWindow.repaint();
            }

        } catch (final IOException ioe) {
            System.out.println("An error occurred " + ioe);
            System.exit(400);
        }
    }

    public static void main(final String[] args) {
        final Client client = new Client();
    }

    public void initializeGame() throws IOException {
        String playerName = dis.readUTF();

        // Setting up initial position for all players
        String stringPlayers = dis.readUTF();
        String[] stringListPlayers = stringPlayers.split(">");
        for (String s : stringListPlayers) {
            String[] playerInfo = s.split("-");
            String name = playerInfo[0];
            int x = Integer.parseInt(playerInfo[1]);
            int y = Integer.parseInt(playerInfo[2]);
            boolean hasSword = Boolean.parseBoolean(playerInfo[3]);

            Player p = new Player(name, x, y, 10, hasSword);
            players.put(name, p);
        }
        player = players.get(playerName);
        gameWindow = new GameWindow(players, player);

        // Setting up initial position for all swords
        String stringSwords = dis.readUTF();
        String[] stringListSwords = stringSwords.split(">");
        for (String s : stringListSwords) {
            String[] position = s.split("-");
            gameWindow.swordSpawn(Integer.parseInt(position[0]), Integer.parseInt(position[1]));
        }
        gameWindow.repaint();

        setLayout(new BorderLayout());
        add(gameWindow, BorderLayout.CENTER);

        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    dos.writeUTF("EXIT");
                } catch (IOException eIO) {
                    eIO.printStackTrace();
                }

            }
        });
    }

    public static void gameOverPlayer(String id) throws IOException {
        dos.writeUTF("GAME_OVER");
    }

    private class KeyInput extends KeyAdapter {

        @Override
        public void keyPressed(final KeyEvent e) {
            try {
                switch (e.getKeyChar()) {
                    case 'w':
                        dos.writeUTF("MOVE_UP");
                        break;
                    case 'a':
                        dos.writeUTF("MOVE_LEFT");
                        break;
                    case 's':
                        dos.writeUTF("MOVE_DOWN");
                        break;
                    case 'd':
                        dos.writeUTF("MOVE_RIGHT");
                        break;
                    case 'e':
                        dos.writeUTF("ATTACK");
                        break;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleEvent(String event) {
        String[] commands = event.split(",");
        String command = commands[0];
        String playerName = commands[1];
        int xPos = 20;
        int yPos = 20;

        switch (command) {

            case "MOVE_UP":
                players.get(playerName).move_up();
                break;

            case "MOVE_DOWN":
                players.get(playerName).move_down();
                break;

            case "MOVE_LEFT":
                players.get(playerName).move_left();
                break;

            case "MOVE_RIGHT":
                players.get(playerName).move_right();
                break;

            case "ATTACK":
                System.out.println("succesfull attack for " + playerName);
                players.get(playerName).attack();
                break;

            case "PICK_UP":
                int index = Integer.parseInt(commands[2]);
                System.out.println(playerName + " picked up an item");
                players.get(playerName).playerGotSword();
                gameWindow.removeSword(index);
                break;

            case "SWORD_SPAWN":
                xPos = Integer.parseInt(commands[1]);
                yPos = Integer.parseInt(commands[2]);
                gameWindow.swordSpawn(xPos, yPos);
                break;

            case "LOSE_HEALTH":
                String atkPlayer = commands[2];
                players.get(atkPlayer).playerLostSword();
                players.get(playerName).loseHealth();
                System.out.println(playerName + " lost health, total healt: " + players.get(playerName).getHealth());
                break;

            case "PLAYER_JOINED":
                System.out.println(playerName + " has Joined");
                xPos = Integer.parseInt(commands[2]);
                yPos = Integer.parseInt(commands[3]);
                Player p = new Player(playerName, xPos, yPos, 10, false);
                players.put(playerName, p);
                gameWindow.addPlayer(p);
                break;

            case "GAME_OVER":
                System.out.println("game over for player " + playerName);
                players.get(playerName).dead();
                break;

            case "PLAYER_WON":
                p = players.get(playerName);
                if (!p.gameOver) {
                    System.out.println(p.getName() + " won");
                    p.playerWon();
                }
                break;
        }
    }
}
