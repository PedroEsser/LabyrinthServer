
package labyrinthserver;

public class Colour {

        final private int rgb;
        final private String colorName;
        final private int index;
        
        public Colour(int rgb, String colorName, int index){
                this.rgb = rgb;
                this.colorName = colorName;
                this.index = index;
        }
        
        public int getRGB(){
                return rgb;
        }
        
        public String getColorName(){
                return colorName;
        }
        
        public int getIndex(){
                return index;
        }
}
