
package labyrinthserver;

import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

public class Labyrinth {

        private Square[][] grid;
        private final int width, height;
        private Random rand = new Random();
        private Square goal;
        private LinkedList <Wall> standingWalls = new LinkedList <>();
        private LinkedList <Square> allSquares = new LinkedList <>();
        private LinkedList <Coin> allCoins = new LinkedList <>();
        
        public Labyrinth(int width, int height){
                this.width = width;
                this.height = height;
                setMaze();
                setWalls();
                generateMaze();
                destroyXPercentWalls(10);
                addCoins();
        }
        
        private void setMaze(){
                grid = new Square[width][height];
                for(int i = 0 ; i < width ; i++){
                        for(int j = 0 ; j < height ; j++){
                                grid[i][j] = new Square(i, j);
                                allSquares.add(grid[i][j]);
                        }
                }
        }
        
        private void setWalls(){
                for(int i = 0 ; i < width ; i++){
                        Square su = grid[i][0];
                        Wall wu = new Wall(su.getI(), su.getJ(), su.getI() + 1, su.getJ());
                        su.setUpWall(wu);
                        
                        Square sd = grid[i][height - 1];
                        Wall wd = new Wall(sd.getI(), sd.getJ() + 1, sd.getI(), sd.getJ());
                        sd.setDownWall(wd);
                }
                
                for(int i = 0 ; i < height ; i++){
                        Square sl = grid[0][i];
                        Wall wl = new Wall(sl.getI(), sl.getJ(), sl.getI(), sl.getJ() + 1);
                        sl.setLeftWall(wl);
                        
                        Square sr = grid[width - 1][i];
                        Wall wr = new Wall(sr.getI() + 1, sr.getJ(), sr.getI() + 1, sr.getJ() + 1);
                        sr.setRightWall(wr);
                }
                
                for(int i = 0 ; i < width - 1 ; i++){
                        for(int j = 0 ; j < height ; j++){
                                Square s = grid[i][j];
                                Square sr = grid[i + 1][j];
                                Wall r = new Wall(sr.getI(), sr.getJ(), sr.getI(), sr.getJ() + 1);
                                s.setRightWall(r);
                                sr.setLeftWall(r);
                                standingWalls.add(r);
                        }
                }
                
                for(int i = 0 ; i < width ; i++){
                        for(int j = 0 ; j < height - 1 ; j++){
                                Square s = grid[i][j];
                                Square sd = grid[i][j + 1];
                                Wall d = new Wall(sd.getI(), sd.getJ(), sd.getI() + 1, sd.getJ());
                                s.setDownWall(d);
                                sd.setUpWall(d);
                                standingWalls.add(d);
                        }
                }
        }
        
        public void generateMaze(){
        
                int x = rand.nextInt(width);
                int y = rand.nextInt(height);
                
                Stack <Square> backTrack = new Stack <>();
                
                Square currentSquare = grid[x][y];
                currentSquare.setVisited();
                backTrack.add(currentSquare);
                
                while(!backTrack.isEmpty()){
                        
                        LinkedList <Square> neighbourSquares = neighbourSquares(currentSquare);
                        if(neighbourSquares.isEmpty()){
                                currentSquare = backTrack.pop();
                        }else{
                                Square nextSquare = neighbourSquares.get(rand.nextInt(neighbourSquares.size()));
                                standingWalls.remove(destroyWall(currentSquare, nextSquare));
                                backTrack.add(nextSquare);
                                currentSquare = nextSquare;
                                currentSquare.setVisited();
                        }
                }
                
                goal = grid[width/2][height/2];
                allSquares.remove(goal);
        }
        
        private LinkedList <Square> neighbourSquares(Square s){
                
                LinkedList <Square> neighbourSquares = new LinkedList <>();
                
                try{
                        Square su = grid[s.getI()][s.getJ() - 1];
                        if(!su.wasVisited()){
                                neighbourSquares.add(su);
                        }
                }catch(Exception e){
                }
                
                try{
                        Square sr = grid[s.getI() + 1][s.getJ()];
                        if(!sr.wasVisited()){
                                neighbourSquares.add(sr);
                        }
                }catch(Exception e){
                }
                
                try{
                        Square sd = grid[s.getI()][s.getJ() + 1];
                        if(!sd.wasVisited()){
                                neighbourSquares.add(sd);
                        }
                }catch(Exception e){
                }
                
                try{
                        Square sl = grid[s.getI() - 1][s.getJ()];
                        if(!sl.wasVisited()){
                                neighbourSquares.add(sl);
                        }
                }catch(Exception e){
                }
                
                return neighbourSquares;
                
        }
        
        private Wall destroyWall(Square a, Square b){
                if(a.getI() < b.getI()){
                        a.getRightWall().destroy();
                        return a.getRightWall();
                }else if(a.getI() > b.getI()){
                        a.getLeftWall().destroy();
                        return a.getLeftWall();
                }else if(a.getJ() < b.getJ()){
                        a.getDownWall().destroy();
                        return a.getDownWall();
                }else if(a.getJ() > b.getJ()){
                        a.getUpWall().destroy();
                        return a.getUpWall();
                }
                return null;
        }
        
        public void destroyXPercentWalls(int percent){
                int help = 100/percent;
                int w = standingWalls.size()/help;
                for (int i = 0 ; i < w ; i++) {
                        Wall wall = standingWalls.get(rand.nextInt(standingWalls.size()));
                        wall.destroy();
                        standingWalls.remove(wall);
                }
        }
        
        private void addCoins(){
                int coins = allSquares.size()/10;
                for(int i = 0 ; i < coins ; i++){
                        Square s = allSquares.remove(rand.nextInt(allSquares.size()));
                        Coin c = new Coin(i);
                        s.setCoin(c);
                        allCoins.add(c);
                }
        }
        
        public void removeCoin(Square s){
                Coin c  = s.getCoin();
                int index = c.getIndex();
                s.removeCoin();
                allCoins.remove(c);
                for(int i = index ; i < allCoins.size() ; i++){
                        allCoins.get(i).setIndex(i);
                }
        }
        
         public int getWidth(){
                return width;
        }
        
        public int getHeight(){
                return height;
        }
        
        public Square getSquareAt(int i, int j){
                return grid[i][j];
        }
        
        public LinkedList <Wall> getStandingWalls(){
                return standingWalls;
        }
        
        public String getLabyrinthAsString(){
                String string = width + ":" + height + ":" + allCoins.size();
                for(int i = 0 ; i < allCoins.size() ; i++){
                        string += ":" + allCoins.get(i).getI() + "," + allCoins.get(i).getJ();
                }
                
                for(int i = 0 ; i < standingWalls.size() ; i++){
                        string += ":" + standingWalls.get(i).getInfo();
                }
                
                return string;
        }
        
        public int getTime(){
                return (width + height)/2 ;
        }
        
        public boolean checkGoal(Square s){
                return s == goal;
        }
}