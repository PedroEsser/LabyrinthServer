
package labyrinthserver;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Player{
  
        
        private Lobby lobbyServer;
        private Game gameServer;
        private BufferedReader read = null;
        private PrintWriter write = null;
        private Square currentSquare;
        private int index = 0;
        private Colour color;
        private String name;
        private int coins = 0;
        private int score = 0;
        private boolean hasWon;

        public Player(Lobby server, Socket socket) {
                try {
                        this.lobbyServer = server;
                        read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        write = new PrintWriter(socket.getOutputStream());
                        listen();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public void send(String buffer) {
                try {
                        write.write(buffer + "\n");
                        write.flush();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        private void listen() {
                new Thread(() ->{
                        try {
                                while (true) {
                                        String info = read.readLine();
                                        switch(info.charAt(0)){
                                                case 'm':lobbyServer.broadcastMessage(info.split(",")[1], this);
                                                break;
                                                case 'u':gameServer.movePlayer(this, info.split(",")[1]);
                                                break;
                                                case 'c':lobbyServer.swapColors(this, Integer.parseInt(info.split(",")[1]));
                                                break;
                                                case 'n':
                                                        if(info.split(",").length > 2){
                                                                name = ",";
                                                        }else{
                                                                name = info.split(",")[1];
                                                        }
                                                break;
                                                default:
                                                break;
                                        }
                                }
                        } catch (Exception e) {
                                //e.printStackTrace();
                                lobbyServer.removePlayer(index);
                        }
                }).start();
                
        }
        
        public void setIndex(int index){
                this.index = index;
        }
        
        public int getIndex(){
                return index;
        }
        
        public Square getCurrentSquare(){
                return currentSquare;
        }
        
        public void setSquare(Square s){
                this.currentSquare = s;
        }
        
        public void setColor(Colour c){
                this.color = c;
        }
        
        public Colour getColor(){
                return color;
        }
        
        public String getName(){
                return name;
        }
        
        public int getCoins(){
                return coins;
        }
        
        public void setGameServer(Game gameServer){
                this.gameServer = gameServer;
        }
        
        public void setWon(boolean won){
                this.hasWon = won;
        }
        
        public boolean hasWon(){
                return hasWon;
        }
}