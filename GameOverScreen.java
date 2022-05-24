import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * Displays the Game-Over Screen after a win or loss
 *
 * @Avi and Amar Ruthen
 * @12.22.2020
 */
public class GameOverScreen extends JFrame implements ActionListener
{
    JPanel buttonPanel;
    JPanel textPanel;
    JPanel picPanel;
    JButton replayButton, exitButton;
    JLabel gameResult;
    JLabel backgroundPic;

    BufferedImage titlePic;
    final Color DARK_BLUE = new Color(0, 0, 204);
    
    int WIDTH;
    int HEIGHT;

    /**
     * Constructor for creating the Game-Over Screen
     * 
     * @param win displays different text based on whether the user
     * has won or lost
     */
    public GameOverScreen(boolean win)
    {
        super("Game Over!");
        
        // the JFrame/Graphics Window size:
        WIDTH = 1000;
        HEIGHT = 700;
        
        Font font = new Font("Verdana", Font.BOLD, 32);
        
        this.setLayout(new BorderLayout());
        
        //Creating the panel to display text
        textPanel = new JPanel();
        textPanel.setBackground(DARK_BLUE);
        
        gameResult = new JLabel();
        if(win)
            gameResult.setText("You found the enemy base first! Congrats!");
        else
            gameResult.setText("Sorry, the Drones found your base!");
        
        gameResult.setFont(font);
        gameResult.setForeground(Color.WHITE);
        //Add text to the top panel
        textPanel.add(gameResult);               
       
        //Creating the panel for the buttons
        buttonPanel = new JPanel();
        buttonPanel.setBackground(DARK_BLUE);
        
        replayButton = new JButton("REPLAY");
        replayButton.addActionListener(this);
        replayButton.setActionCommand("replay");
        
        exitButton = new JButton("EXIT");
        exitButton.addActionListener(this);
        exitButton.setActionCommand("exit");

        //Add buttons to the front panel
        buttonPanel.add(replayButton);
        buttonPanel.add(exitButton);
        
        //Create the picture panel
        picPanel = new JPanel();
        picPanel.setBackground(DARK_BLUE);
        
        //Establish picture from files and add to bottom panel
        ImageIcon titlePic = new ImageIcon("resources/TitleBackground.png");
        backgroundPic = new JLabel(titlePic);
        picPanel.add(backgroundPic);
        
        //Construct and arrange panels on full screen
        this.add(buttonPanel, BorderLayout.CENTER);
        this.add(textPanel, BorderLayout.NORTH);
        this.add(picPanel, BorderLayout.SOUTH);
        
        //Fine-tuning the screen
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));  
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }
    
    /**
     * Actionperformed: when a button is pressed, this is automatically called:
     */
    public void actionPerformed(ActionEvent e) {
        //Transfer screen to powerup selection
        if (e.getActionCommand().equals("replay")) {  
            PowerupScreen ps = new PowerupScreen();
            this.setVisible(false);
        }
        //Quite out of game
        if (e.getActionCommand().equals("exit")) {  
            JOptionPane.showMessageDialog(null, "HAVE A NICE DAY", "Game Over", 
                                          JOptionPane.PLAIN_MESSAGE, null);
            System.exit(0);
        }
    }
}