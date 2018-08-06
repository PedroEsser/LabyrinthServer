

package labyrinthserver;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Lobby extends JFrame implements KeyListener{

        private ServerSocket server;
        private int maxPlayers;
        private JPanel panel;
        private JLabel[] playerLabels = new JLabel[8];
        private LinkedList <Player> players = new LinkedList <>();
        private Colour[] colours;
        private LinkedList <Colour> availableColours = new LinkedList <>();
        private boolean gameIsRunning = false;
        private JTextField message;
        private JLabel updates;
        private StyledDocument chat;
        private Style style;
        
        public Lobby(ServerSocket socket, int maxPlayers){
                super("LobbyServer");
                Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
                
                this.setBounds(d.width/2 - 300, d.height/2  - 200, 700, 450);
                this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                this.setResizable(false);
                
                panel = new JPanel(){
                        @Override
                        protected void paintComponent(Graphics g){
                                g.setColor(Color.black);
                                g.fillRect(0, 0, 700, 450);
                                g.setFont(new Font("Arial", Font.BOLD, 30));
                                g.setColor(Color.white);
                                g.drawString("Lobby", 20, 30);
                                g.fillRect(10, 45, 690, 2);
                                g.fillRect(10, 350, 690, 2);
                        }
                };
                panel.setLayout(null);
                
                JTextPane chatBox = new JTextPane();
                JScrollPane sp = new JScrollPane(chatBox, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                chatBox.setEditable(false);
                sp.setBounds(500, 55, 190, 250);
                chatBox.setBackground(Color.black);
                chatBox.setForeground(Color.white);
                sp.setBorder(BorderFactory.createLineBorder(Color.white, 2));
                panel.add(sp);
                
                chat = chatBox.getStyledDocument();
                style = chatBox.addStyle("", null);
                
                message = new JTextField();
                message.setBounds(500, 307, 190, 30);
                message.setBackground(Color.black);
                message.setForeground(Color.white);
                message.setBorder(BorderFactory.createLineBorder(Color.white, 2));
                message.addKeyListener(this);
                panel.add(message);
                
                setLabels();
                
                JButton startButton = new JButton("Start Game");
                startButton.setBounds(500, 358, 190, 50);
                startButton.setFont(new Font("Arial", Font.BOLD, 25));
                startButton.setBackground(Color.black);
                startButton.setForeground(Color.white);
                startButton.setBorder(BorderFactory.createLineBorder(Color.white, 2));
                startButton.addActionListener((ActionEvent e) -> {
                        if(!gameIsRunning)startGame();
                });
                panel.add(startButton);
                
                updates = new JLabel("Waiting for players...");
                updates.setBounds(20, 365, 380, 40);
                updates.setForeground(Color.white);
                updates.setFont(new Font("Arial", Font.BOLD, 25));
                panel.add(updates);
                
                this.add(panel);
                this.setVisible(true);
                
                this.server = socket;
                if(maxPlayers > 8){
                        this.maxPlayers = 8;
                }else if(maxPlayers < 1){
                        this.maxPlayers = 1;
                }else{
                        this.maxPlayers = maxPlayers;
                }
                
                setColours();
                listen();
        }
        
        private void setLabels(){
                
                for(int i = 0 ; i < 8 ; i++){
                        JLabel label = new JLabel("");
                        label.setSize(470, 30);
                        label.setLocation(20, 55 + i * 36);
                        label.setFont(new Font("Arial", Font.BOLD, 20));
                        playerLabels[i] = label;
                        panel.add(label);
                }
        }
        
        private void listen(){
                new Thread(() -> {
                        while (!gameIsRunning) {
                                try {
                                        canPlayerJoin(server.accept());
                                } catch (IOException ex) {
                                }
                        }
                }).start();
        }
        
        private void canPlayerJoin(Socket socket){
                if(maxPlayers == players.size()){
                        Player p = new Player(this, socket);
                        p.setIndex(-1);
                        p.send("e,Lobby is full");
                }else if(gameIsRunning){
                        Player p = new Player(this, socket);
                        p.setIndex(-1);
                        p.send("e,The game has already started");
                }else{
                        
                        Player p = new Player(this, socket);
                        
                        delay(100);
                        String errorMessage = validName(p.getName());
                        
                        if(errorMessage.equals("good name")){
                                addPlayer(p);
                        }else{
                                p.setIndex(-1);
                                p.send("e," + errorMessage);
                        }
                        
                }
        }
        
        private String validName(String name){
                if(name == null){
                        return "Your name must have at least 3 letters";
                }else if(name.contains(",")){
                        return "Your name can't have any commas";
                }else if(name.length() < 3){
                        return "Your name must have at least 3 letters";
                }else if(name.length() > 16){
                        return "Your name can't have more than 16 letters";
                }else{
                        for(int i = 0 ; i < players.size() ; i++){
                                if(players.get(i).getName().equals(name)){
                                        return "Your name has already been picked!";
                                }
                        }
                        return "good name";
                }
        }
        
        public void addPlayer(Player p){
                
                p.setIndex(players.size());
                p.send("l");
                Colour playerColour = availableColours.removeFirst();
                p.setColor(playerColour);
                addPlayerLabel(p);

                String colours = "c";
                for(int i = 0 ; i < this.colours.length ; i++){
                        Colour c = this.colours[i];
                        colours += ":" + c.getColorName() + "," + c.getRGB() + "," + c.getIndex();
                }
                p.send(colours);                                                //sending all colors
                
                for(int i = 0 ; i < availableColours.size() ; i++){
                        p.send("b," + availableColours.get(i).getIndex());      //sending available colors
                }
                
                broadcastInformation("f," + playerColour.getIndex());
                
                broadcastInformation("a," + p.getName() + "," + p.getColor().getRGB());

                broadcastInformation("m," + p.getName() + "," + p.getColor().getRGB() + ", joined the lobby!");
                appendToChat(p.getName(), new Color(p.getColor().getRGB()));
                appendToChat(" joined the lobby! \n" , Color.white);
                
                players.add(p);

                for(int i = 0 ; i < players.size() ; i++){
                        Player a = players.get(i);
                        p.send("a," + a.getName() + "," + a.getColor().getRGB());
                }
                
                if(maxPlayers == players.size()){
                        startGame();
                }
        }
        
        private void startGame(){
                
                if(!gameIsRunning && !players.isEmpty()){
                        new Thread(() ->{
                                gameIsRunning = true;

                                broadcastInformation("n,Get Ready!");

                                delay(2000);

                                for(int i = 3 ; i != 0 ; i--){
                                        broadcastInformation("n,Game starting in " + i + " ...");
                                        updates.setText("Game starting in " + i + " ...");
                                        delay(1000);
                                }

                                new Game(this, players);
                                broadcastInformation("n,Game is running...");
                                updates.setText("Game is running...");
                        }).start();
                }
                
        }
        
        public void endGame(){
                gameIsRunning = false;
                broadcastInformation("n,Getting ready for next round...");
                updates.setText("Getting ready for next round...");
                for(int i = 0 ; i < players.size() ; i++){
                        players.get(i).setGameServer(null);
                }
        }
        
        public void addPlayerLabel(Player p){
                playerLabels[p.getIndex()].setText(p.getName());
                playerLabels[p.getIndex()].setForeground(new Color(p.getColor().getRGB()));
                playerLabels[p.getIndex()].setBorder(BorderFactory.createLineBorder(new Color(p.getColor().getRGB()), 1));
        }
        
        private void setColours(){
                colours = new Colour[8];
                colours[0]=new Colour(new Color(255,0,16).getRGB(), "Red", 0);
                colours[1]=new Colour(new Color(0,117,220).getRGB(), "Blue", 1);
                colours[2]=new Colour(new Color(255,255,0).getRGB(), "Yellow", 2);
                colours[3]=new Colour(new Color(255,168,187).getRGB(), "Pink", 3);
                colours[4]=new Colour(new Color(157,204,0).getRGB(), "Lime", 4);
                colours[5]=new Colour(Color.CYAN.getRGB(), "Cyan", 5);
                colours[6]=new Colour(new Color(255, 92, 0).getRGB(), "Orange", 6);
                colours[7]=new Colour(new Color(145,30,180).getRGB(), "Purple", 7);
                
                for(int i = 0 ; i < colours.length ; i++){
                        availableColours.add(colours[i]);
                }
                
        }
        
        public void removePlayer(int index){
                if(index != -1){
                        Player p = players.get(index);
                        availableColours.add(p.getColor());
                        players.remove(p);
                        broadcastInformation("b," + p.getColor().getIndex());
                        broadcastInformation("r," + index);
                        broadcastInformation("m," + p.getName() + "," + p.getColor().getRGB() + ", left the lobby!");
                        appendToChat(p.getName(), new Color(p.getColor().getRGB()));
                        appendToChat(" left the lobby! \n", Color.white);
                        for(int i = index ; i < players.size() ; i++){
                                Player player = players.get(i);
                                player.setIndex(i);
                                addPlayerLabel(player);
                        }
                        playerLabels[players.size()].setText("");
                        playerLabels[players.size()].setBorder(null);
                        System.out.println(p.getName() + " has disconnected");
                }
        }
        
        private void broadcastInformation(String info){
                for(int i = 0 ; i < players.size() ; i++){
                        players.get(i).send(info);
                }
        }
        
        public void broadcastMessage(String message, Player p){
                String info = "m," + p.getName() + " : ," + p.getColor().getRGB() + "," + message;
                
                broadcastInformation(info);
                
                appendToChat(p.getName() + " : ", new Color(p.getColor().getRGB()));
                appendToChat(message + "\n", Color.WHITE);
        }
        
        private void appendToChat(String msg, Color c){
        
                StyleConstants.setForeground(style, c);

                try { 
                        chat.insertString(chat.getLength(), msg, style); 
                }catch (BadLocationException e){
                }
        
        }
        
        public void swapColors(Player player, int colorIndex){
                availableColours.add(player.getColor());
                broadcastInformation("b," + player.getColor().getIndex());
                
                player.setColor(colours[colorIndex]);
                availableColours.remove(colours[colorIndex]);
                broadcastInformation("f," + colours[colorIndex].getIndex());
                
                addPlayerLabel(player);
                broadcastInformation("s," + player.getIndex() + "," + player.getColor().getRGB());
        }
        
        private void delay(int mili){
                try {
                        Thread.sleep(mili);
                } catch (InterruptedException ex) {
                }
        }
        
        @Override
        public void dispose(){
                try{
                        broadcastInformation("d");
                }catch(Exception e){
                        
                }
                
                System.exit(0);
        }
        
        private boolean validMessage(String message){
                if(message.equals("")){
                        return false;
                }
                for(int i = 0 ; i < message.length() ; i++){
                        if(message.charAt(i) != ' '){
                                return true;
                        }
                }
                return false;
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                        String message = this.message.getText();
                        if(validMessage(message)){
                                appendToChat("Server : ", Color.GREEN);
                                appendToChat(message + "\n", Color.white);
                                broadcastInformation("m," + "Server : ," + Color.GREEN.getRGB() + "," + message);
                        }
                        this.message.setText("");
                }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
}
