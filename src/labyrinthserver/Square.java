
package labyrinthserver;

public class Square {

        private Wall up, right, down, left;
        private final int i, j;
        private boolean visited = false;
        private boolean hasCoin = false;
        private Coin coin;
        
        public Square(int x, int y){
                this.i = x;
                this.j = y;
        }
        
        public int getI(){
                return i;
        }
        
        public int getJ(){
                return j;
        }
        
        public void setUpWall(Wall w){
                this.up = w;
        }
        
        public void setRightWall(Wall w){
                this.right = w;
        }
        
        public void setDownWall(Wall w){
                this.down = w;
        }
        
        public void setLeftWall(Wall w){
                this.left = w;
        }
        
        public Wall getUpWall(){
                return up;
        }
        
        public Wall getRightWall(){
                return right;
        }
        
        public Wall getDownWall(){
                return down;
        }
        
        public Wall getLeftWall(){
                return left;
        }
        
        public void setVisited(){
                visited = true;
        }
        
        public void setUnvisited(){
                visited = false;
        }
        
        public boolean wasVisited(){
                return visited;
        }
        
        public String getInfo(){
           return i + "," + j;
        }
        
        public void setCoin(Coin coin){
                hasCoin = true;
                this.coin = coin;
                this.coin.setI(i);
                this.coin.setJ(j);
        }
        
        public Coin getCoin(){
                return coin;
        }
        
        public boolean hasCoin(){
                return hasCoin;
        }
        
        public void removeCoin(){
                hasCoin = false;
                this.coin = null;
        }
}
