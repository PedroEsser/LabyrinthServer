
package labyrinthserver;

public class Wall {
  
        private final int x1, x2, y1, y2;
        private boolean broken = false;

        public Wall(int x1, int y1, int x2, int y2){
                this.x1 = x1;
                this.x2 = x2;
                this.y1 = y1;
                this.y2 = y2;
        }

        public int getX1(){
                return x1;
        }

        public int getX2(){
                return x2;
        }

        public int getY1(){
                return y1;
        }

        public int getY2(){
                return y2;
        }

        public void destroy(){
                broken = true;
        }

        public boolean isBroken(){
                return broken;
        }
        
        public String getInfo(){
                return x1 + "," + y1 + "," + x2 + "," + y2;
        }
}
