
package labyrinthserver;

import java.awt.Point;
import java.util.LinkedList;

public class Game {
        
        private LinkedList <Player> players;
        private Lobby lobby;
        private Labyrinth labyrinth;
        private boolean gameIsRunning = false;
        private int time;
        private Player winner;
        private LinkedList <Player> playersWon;

        public Game(Lobby lobby, LinkedList <Player> players){
                this.players = players;
                this.lobby = lobby;
                this.labyrinth = new Labyrinth(31,31);
                this.time = labyrinth.getTime();
                this.playersWon = new LinkedList <>();
                setPlayerInformation();
                startCountdown();
        }
        
        private void setPlayerInformation(){
                String labyrinthInformation = labyrinth.getLabyrinthAsString();
                broadcastInformation("g:" + labyrinthInformation);
                broadcastInformation(getTimerFormat());
                String playerPositions = "p";
                for(int i = 0 ; i < players.size() ; i++){
                        Player p = players.get(i);
                        p.setGameServer(this);
                        p.setSquare(getStartSquare(p));
                        p.setWon(false);
                        playerPositions += ":" + p.getCurrentSquare().getInfo();
                        
                }
                
                broadcastInformation(playerPositions);
                
                for(int i = 0 ; i < players.size() ; i++){
                        players.get(i).send("v," + i + ",true");
                }
        }
        
        public void removePlayer(Player player) {
                players.remove(player);
        }
        
        public void startCountdown(){
                for(int i = 3 ; i > 0 ; i--){
                        broadcastInformation("h," + i);
                        delay(1000);
                }
                broadcastInformation("k,start");
                gameIsRunning = true;
                startTimer();
        }
        
        private void startTimer(){
                new Thread(() ->{
                        while(time > 0){
                                broadcastInformation(getTimerFormat());
                                delay(1000);
                                time--;
                        }
                        broadcastInformation(getTimerFormat());
                        endGame();
                }).start();
        }
        
        private String getTimerFormat(){
                String timeFormat = "t,";
                int minutes = time/60;
                int seconds = time%60;
                if(minutes < 10){
                        timeFormat += 0;
                }
                timeFormat += minutes + ":";
                if(seconds < 10){
                        timeFormat += 0;
                }
                timeFormat += seconds;
                return timeFormat;
        }
        
        private void endGame(){
                broadcastInformation("k,end");
                if(winner != null){
                        broadcastInformation("h," + winner.getName() + " won the game!");
                }else{
                        broadcastInformation("h,No one won the game");
                }
                
                delay(3000);
                
                broadcastInformation("x");
                
                this.lobby.endGame();
        }
        
        public void movePlayer(Player player, String direction) {
                
                if(gameIsRunning && !player.hasWon()){
                        if(validDirection(player, direction)){

                                Square lastPosition = player.getCurrentSquare();
                                Point dir;
                                switch(direction){
                                        case "right" : dir = new Point(1,0);
                                        break;
                                        case "left" : dir = new Point(-1,0);
                                        break;
                                        case "up" : dir = new Point(0,-1);
                                        break;
                                        case "down" : dir = new Point(0,1);
                                        break;
                                        default: dir = new Point();
                                        break;
                                }

                                Square newSquare = labyrinth.getSquareAt(lastPosition.getI() + dir.x, lastPosition.getJ() + dir.y);
                                if(labyrinth.checkGoal(newSquare)){
                                        if(winner == null){
                                                winner = player;
                                        }
                                        player.setWon(true);
                                        playersWon.add(player);
                                        broadcastInformation("v," + player.getIndex() + ",false");
                                }else{
                                        if(newSquare.hasCoin()){
                                                broadcastInformation("o," + newSquare.getCoin().getIndex());
                                                labyrinth.removeCoin(newSquare);
                                        }
                                        player.setSquare(newSquare);
                                        String newPosition = newSquare.getInfo();
                                        broadcastInformation("u," + player.getIndex() + "," + newPosition);
                                }
                        }
                }
        }
        
        private boolean validDirection(Player player, String direction){
                Square s = player.getCurrentSquare();
                switch(direction){
                        case "right" : return s.getRightWall().isBroken();
                        case "left" : return s.getLeftWall().isBroken();
                        case "up" : return s.getUpWall().isBroken();
                        case "down" : return s.getDownWall().isBroken();
                        default : return false;
                }
        }
        
        private Square getStartSquare(Player p){
                
                if(players.size() <= 4){
                        
                        switch (p.getIndex()) {
                        case 0: return labyrinth.getSquareAt(0,0);
                        case 1: return labyrinth.getSquareAt(labyrinth.getWidth() - 1, 0);
                        case 2: return labyrinth.getSquareAt(0,labyrinth.getHeight() - 1);
                        case 3: return labyrinth.getSquareAt(labyrinth.getWidth() - 1, labyrinth.getHeight() - 1);
                        default: return null; 
                        }
                }else{
                        switch (p.getIndex()) {
                        case 0: return labyrinth.getSquareAt(labyrinth.getWidth()/4 - 1,0);
                        case 1: return labyrinth.getSquareAt(labyrinth.getWidth()*3/4 - 1, 0);
                        case 2: return labyrinth.getSquareAt(labyrinth.getWidth() - 1, labyrinth.getHeight()/4 - 1);
                        case 3: return labyrinth.getSquareAt(labyrinth.getWidth() - 1, labyrinth.getHeight()*3/4 - 1);
                        case 4: return labyrinth.getSquareAt(labyrinth.getWidth()*3/4, labyrinth.getHeight() - 1);
                        case 5: return labyrinth.getSquareAt(labyrinth.getWidth()/4 - 1, labyrinth.getHeight() - 1);
                        case 6: return labyrinth.getSquareAt(0, labyrinth.getHeight()*3/4 - 1);
                        case 7: return labyrinth.getSquareAt(0, labyrinth.getHeight()/4 - 1);
                        default: return null; 
                        }
                }
        }
        
        private void broadcastInformation(String info){
                for(int i = 0 ; i < players.size() ; i++){
                        players.get(i).send(info);
                }
        }
        
        private void delay(int mili){
                try {
                        Thread.sleep(mili);
                } catch (InterruptedException ex) {
                }
        }
}