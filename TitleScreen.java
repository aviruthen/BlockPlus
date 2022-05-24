import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.imageio.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * Displays the title screen at the beginning of the game
 *
 * @Avi and Amar Ruthen
 * @12.22.2020
 */
public class TitleScreen extends JFrame implements ActionListener
{
    JPanel buttonPanel;
    JPanel textPanel;
    JPanel picPanel;
    JButton startButton;
    JLabel welcome;
    JLabel backgroundPic;

    BufferedImage titlePic;
    final Color DARK_BLUE = new Color(0, 0, 204);
    int WIDTH;
    int HEIGHT;
    
    /**
     * Constructor for creating initial title screen
     */
    public TitleScreen()
    {
        super("BlockPlusMenu");
        
        //the JFrame/Graphics Window size:
        WIDTH = 1000;
        HEIGHT = 700;
        
        Font font = new Font("Verdana", Font.BOLD, 32);
        
        this.setLayout(new BorderLayout());
        
        //Create the panel for the welcoming text
        textPanel = new JPanel();
        textPanel.setBackground(DARK_BLUE);
        
        welcome = new JLabel();
        welcome.setText("Welcome to BlockPlus!");
        welcome.setFont(font);
        welcome.setForeground(Color.WHITE);
        
        //Add welcome mensage to the panel
        textPanel.add(welcome);
       
        //Create the button panel
        buttonPanel = new JPanel();
        buttonPanel.setBackground(DARK_BLUE);
        
        startButton = new JButton("BEGIN");
        startButton.addActionListener(this);
        startButton.setActionCommand("startButton");
        
        //Add start button to panel
        buttonPanel.add(startButton);
        
        //Create the picture panel
        picPanel = new JPanel();
        picPanel.setBackground(DARK_BLUE);
        
        ImageIcon titlePic = new ImageIcon("resources/TitleBackground.png");
        backgroundPic = new JLabel(titlePic);
        //Add picture to the whole paenl
        picPanel.add(backgroundPic);
        
        //Arrange panels on screen
        this.add(buttonPanel, BorderLayout.CENTER);
        this.add(textPanel, BorderLayout.NORTH);
        this.add(picPanel, BorderLayout.SOUTH);
        
        //Fine-tuning screen settings
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));  
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }
    
    /**
     * Actionperformed: when a button is pressed, this is automatically called:
     */
    public void actionPerformed(ActionEvent e) {
        //Transfer to powerup screen
        if (e.getActionCommand().equals("startButton")) {  
               PowerupScreen ps = new PowerupScreen();
               this.setVisible(false);
        }
    }
}