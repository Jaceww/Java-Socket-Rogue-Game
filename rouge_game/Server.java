import java.io.*;
import java.net.*;
import java.util.*;

import javax.sound.sampled.SourceDataLine;

public class Server {
    static HashMap<String,ClientHandler> allPlayers;
    static Game game = new Game();

    public static void main(String[] args) throws IOException {
        Random r = new Random();
        ServerSocket ss = new ServerSocket(2222);
        allPlayers = new HashMap<String,ClientHandler>();
        int ID = 0;
        String playerID;

        while (true) {
            Socket s = null;
            try {
                System.out.println("Waiting for players to join");
                s = ss.accept();
                ID++;
                playerID = "player " + ID;
                System.out.println("A newplayer is connected : " + s);

                // obtaining input and out streams
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                System.out.println("Assigning new thread for this client");

                // position for player
                int xPos = r.nextInt(890) + 10;
                int yPos = r.nextInt(580) + 60;
                System.out.println(xPos + ":" + yPos);
                // create a new thread object
                ClientHandler t = new ClientHandler(playerID, s, dis, dos, xPos, yPos);
                allPlayers.put(playerID, t);

                // Invoking the start() method
                t.start();

            } catch (Exception e) {
                s.close();
                e.printStackTrace();
            }
        }
    }

    public static void sendAll(String event) throws IOException {
        if (allPlayers != null && !allPlayers.isEmpty()) {
            for (Map.Entry<String, ClientHandler> t : allPlayers.entrySet()) {
                t.getValue().send(event);
            }
        }
    }

    public static void removeThread(String pID){
        allPlayers.remove(pID);
    }

}