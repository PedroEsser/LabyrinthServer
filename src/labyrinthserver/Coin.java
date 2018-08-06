
package labyrinthserver;

public class Coin {

        private int index;
        private int i, j;
        
        public Coin(int index){
                this.index = index;
        }
        
        public int getIndex(){
                return index;
        }
        
        public void setIndex(int index){
                this.index = index;
        }
        
        public void setI(int i){
                this.i = i;
        }
        
        public int getI(){
                return i;
        }
        
        public void setJ(int j){
                this.j = j;
        }
        
        public int getJ(){
                return j;
        }
}
