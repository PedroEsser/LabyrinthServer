
package labyrinthserver;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class StartWindow {

        private JFrame frame;
        
        public StartWindow(){
                Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
                
                frame = new JFrame("Server");
                
                frame.setBounds(d.width/2 - 200, d.height/2 - 150, 360, 190);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setResizable(false);
                JPanel panel = new JPanel();
                panel.setLayout(null);
                panel.setBackground(Color.black);
                frame.add(panel);
                
                JLabel ipLabel = new JLabel("IP:");
                ipLabel.setFont(new Font("Arial", Font.BOLD, 20));
                ipLabel.setForeground(Color.white);
                ipLabel.setBounds(0, 20, 90, 30);
                ipLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                panel.add(ipLabel);
                String ipAdress = "";
                try {
                        ipAdress = InetAddress.getLocalHost().getHostAddress().trim();
                } catch (UnknownHostException ex) {
                }
                
                JTextField IPTextField = new JTextField(ipAdress);
                IPTextField.setEditable(false);
                IPTextField.setBounds(90, 20, 150, 30);
                panel.add(IPTextField);
                
                JLabel portLabel = new JLabel("Port:");
                portLabel.setFont(new Font("Arial", Font.BOLD, 20));
                portLabel.setForeground(Color.white);
                portLabel.setBounds(0, 60, 90, 30);
                portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                panel.add(portLabel);
                
                JTextField portTextField = new JTextField("666");
                portTextField.setBounds(90, 60, 150, 30);
                panel.add(portTextField);
                
                JLabel playersLabel = new JLabel("Players:");
                playersLabel.setFont(new Font("Arial", Font.BOLD, 20));
                playersLabel.setForeground(Color.white);
                playersLabel.setBounds(0, 100, 90, 30);
                playersLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                panel.add(playersLabel);
                
                JTextField playersTextField = new JTextField("8");
                playersTextField.setBounds(90, 100, 150, 30);
                panel.add(playersTextField);
                
                JButton startButton = new JButton("Start!");
                startButton.setBounds(250, 20, 90, 110);
                startButton.addActionListener((ActionEvent e) -> {
                        start(Integer.parseInt(portTextField.getText()), Integer.parseInt(playersTextField.getText()));
                });
                panel.add(startButton);
                frame.setVisible(true);
        }
        
        private void start(int port, int players){
                try {
                        ServerSocket socket = new ServerSocket(port);
                        new Lobby(socket, players);
                        frame.dispose();
                } catch (IOException ex) {
                        ex.printStackTrace();
                }
                
        }
        
        public static void main(String[] args) {
                new StartWindow();
        }
}
