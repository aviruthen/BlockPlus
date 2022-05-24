import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;

/**
 * Creates GUI for Powerup Selection Screen
 * 
 * @Avi and Amar Ruthen
 * @12.22.2020
 */
public class PowerupScreen extends JFrame implements ActionListener {
    String[] powerups =        {"Select:", "Teleportation", "Auto-Respawn", 
                                "Flashlight", "Invisibility", "Compass", 
                                "New Spawn", "Extra Blocks", "Immobilize", 
                                "Immovable Blocks", "Fast Block Breaking"};
    String[] deckSuggestions = {"Select:", "Navigator", "Spy", "Defender", 
                                "Speedrunner", "Magician"};
    
                                int powerup1, powerup2, powerup3;
    int suggestedDeck = 0;

    String[] taskNames; // to be used in combobox for selections
    
    JComboBox[] powerupSelector = new JComboBox[4]; //For selecting powerups

    JPanel masterPanel;     // panel for all components of the display
    JPanel topPanel;        // panel for the buttonPanel and instructionsPanel
    JPanel buttonPanel;     
    JPanel welcomePanel;    // panel for welcome header
    JPanel ioPanel;         // panel to hold instructions and JCombo boxes
    JPanel reportPanel;     // panel for all game explanations
    JPanel menuPanel;       // panel for JCombo boxes
    JButton quitButton, startButton, powerupButton, ruleButton;
    JLabel welcome;         // label for welcome to user
    JTextArea report;       // area to rules and guidelines
    JScrollPane outputScroll;

    /**
     * GUI constructor
     */
    public PowerupScreen() {
        // master panel IS the whole window
        masterPanel = (JPanel) this.getContentPane(); 
        masterPanel.setLayout(new BorderLayout());
        
        /*
         * SET UP BUTTON PANEL:
         */
        startButton = new JButton("START");
        startButton.addActionListener(this);
        startButton.setActionCommand("startButton");
        startButton.setToolTipText("Click to begin!");
        
        powerupButton = new JButton("POWERUP");
        powerupButton.addActionListener(this);
        powerupButton.setActionCommand("powerupButton");
        powerupButton.setToolTipText("Click to select powerups");
        
        ruleButton = new JButton("RULES");
        ruleButton.addActionListener(this);
        ruleButton.setActionCommand("ruleButton");
        ruleButton.setToolTipText("Click to see the game description");
        
        quitButton = new JButton("QUIT");
        quitButton.addActionListener(this);
        quitButton.setActionCommand("quitButton");
        quitButton.setToolTipText("Click to end game");

        buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);

        
        /*
         * SET UP MENU PANEL
         * fomatting for first three combo boxes, fourth is different
         * Initializes...
         *    the pull-down menu object
         *    the default index
         *    the listener
         */
        for(int i = 0; i < powerupSelector.length - 1; i++)
        {
            powerupSelector[i] = new JComboBox(powerups);       
            powerupSelector[i].setSelectedIndex(0);             
            powerupSelector[i].addActionListener(this);         
            powerupSelector[i].setActionCommand("Box" + (i + 1) + " selected");
        }
        
        powerupSelector[3] = new JComboBox(deckSuggestions);   
        powerupSelector[3].setSelectedIndex(0);                 
        powerupSelector[3].addActionListener(this);             
        powerupSelector[3].setActionCommand("Box4 selected");
        
        /*
         * SET UP TOP PANEL
         */
        welcomePanel = new JPanel();
        welcomePanel.setBackground(Color.BLACK);
        welcome = new JLabel("Welcome to BlockPlus!");
        welcome.setForeground(Color.BLUE);
        Font font = new Font("Verdana", Font.BOLD, 24);
        welcome.setFont(font);
        //Add JLabel into panel
        welcomePanel.add(welcome);

        // top panel holds buttons and welcome
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 1));
        topPanel.add(buttonPanel);
        topPanel.add(welcomePanel);

        // Text for game rules
        report = new JTextArea("\nThis is a fast-paced player vs enemy game!" + 
        "\nUse the arrow keys to turn or toggle different " + 
        "movement using wasd by pressing 'm'!\n" + 
        "1. You earn points by discovering entering the enemy base!\n" + 
        "2. You must defend your base against enemy drones that constantly " + 
        "spawn and search for your base!\n" + 
        "3. If the drones reach your base first, " +
        "the computer earns the point!\n" + 
        "4. Blocks can be placed as barriers, but they can be removed!\n" + 
        "5. Furthermore, if you are caught outside of " +
        "your half of the map and in the enemy side," + 
        "you may be tagged and sent back to your home base!\n" + 
        "6. Select 3 powerups to play with in game\n" + 
        "7. Powerups can be accessed by pressing the keys 1, 2, and 3\n" + 
        "\nClick the Powerups Button for a description of each powerup!\n",
        15, 50);

        report.setBackground(Color.BLACK);                        
        report.setForeground(Color.BLUE);
        Font font2 = new Font("Verdana", Font.BOLD, 18);
        report.setFont(font2);

        report.setEditable(false);
        report.setLineWrap(true);
        report.setWrapStyleWord(true);
        // add the report to a scroll pane, so if it gets big, it will scroll:
        outputScroll = new JScrollPane(report);
        // add the scroll pane to the overall panel:
        reportPanel = new JPanel();
        reportPanel.setBackground(Color.BLACK);
        reportPanel.add(outputScroll);

        //Add buttons to button panel
        buttonPanel.add(startButton);
        buttonPanel.add(powerupButton);
        buttonPanel.add(ruleButton);
        buttonPanel.add(quitButton);

        //Add combo boxes to menu
        menuPanel = new JPanel();
        menuPanel.setBackground(Color.BLACK);
        for(int i = 0; i < powerupSelector.length; i++)
            menuPanel.add(powerupSelector[i]);
            

        // make a panel to add entry boxes and report to:
        ioPanel = new JPanel(new BorderLayout());
        ioPanel.add(menuPanel, BorderLayout.NORTH);
        ioPanel.add(reportPanel, BorderLayout.CENTER);
        ioPanel.setBackground(Color.GRAY);
        /*
         * Put the two panels into the master panel, i.e. the whole window:
         */
        masterPanel.add(topPanel, BorderLayout.NORTH);
        masterPanel.add(ioPanel, BorderLayout.CENTER);

        this.setSize(1000, 650);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    /**
     * Actionperformed: when a button is pressed, this is automatically called:
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //Show powerup rules
        if (e.getActionCommand().equals("powerupButton")) {
            report.setText("\nSelect 3 of the following abilities:\n" + 
            "1. Teleport: Teleport five blocks in the direction " + 
            "you are facing. Only five uses!\n" + 
            "2. Auto-Respawn: Immediately teleport " +
            "back to you home base. One time use!\n" + 
            "3. Flashlight: Increase your viewing range for the whole game!\n" + 
            "4. Invisibility: Become invisible to " + 
            "your opponents for five seconds. Only 3 uses!\n" + 
            "5. Compass: Displays direction of enemy base for 3 seconds. " + 
            " 30 second cooldown!\n" + 
            "6. New Spawn: Create a new home base location. One time use!\n" + 
            "7. Extra Blocks: Increase the number of blocks you can carry!\n" + 
            "8. Immobilize: Temporarily freeze all drones on the map!\n" + 
            "9. Immovable Blocks: Have the ability to place " + 
            "10 unbreakable blocks!\n" +
            "10. Fast Block Breaking: Doubles the speed in breaking blocks!\n" + 
            "\nPowerups will restock after 5 minutes, " + 
            "EXCEPT for immovable blocks!" + 
            "\n\nThe 4th menu gives some suggestions for powerups, " + 
            "but feel free to customize your own!" + 
            "\nClick the Rules Button to see the game description again, " + 
            "or press start to begin " + 
            "once you have selected your powerups!\n");
        }
        
        //Send powerups selected to Graphics Window and begin game
        else if (e.getActionCommand().equals("startButton")) {
            // Will not start game if three powerups are not selected or
            // Same powerup is selected for two hotkeys
            if((powerup1 == powerup2) || (powerup1 == powerup3) || 
               (powerup2 == powerup3) || (powerup1 == 0) || 
               (powerup2 == 0) || (powerup3 == 0))
                report.setText("\nPlease choose a different " +
                                "powerup from each menu!");
            else
            {
                //Beginning of game with added timers!
                GraphicsWindow AaA = new GraphicsWindow(powerup1, 
                                                        powerup2, 
                                                        powerup3);
                AaA.startTimer();
                AaA.startTimerCounter();
                AaA.startDroneTimer();
                AaA.startPowerupsCooldown(60000);
                this.setVisible(false);
            }
        }
        
        //End game if user wants to
        else if (e.getActionCommand().equals("quitButton")) {
            JOptionPane.showMessageDialog(null,"HAVE A NICE DAY","End of Thing", 
                                          JOptionPane.PLAIN_MESSAGE, null);
            System.exit(0);
        } 
        //Shows rules again after seeing powerup rules
        else if (e.getActionCommand().equals("ruleButton")){
          report.setText("\nThis is a fast-paced player vs enemy game!" + 
          "\nUse the arrow keys to turn or toggle different " + 
          "movement using wasd by pressing 'm'!\n" + 
          "1. You earn points by discovering entering the enemy base!\n" + 
          "2. You must defend your base against enemy drones that constantly " + 
          "spawn and search for your base!\n" + 
          "3. If the drones reach your base first, " +
          "the computer earns the point!\n" + 
          "4. Blocks can be placed as barriers, but they can be removed!\n" + 
          "5. Furthermore, if you are caught outside of " +
          "your half of the map and in the enemy side," + 
          "you may be tagged and sent back to your home base!\n" + 
          "6. Select 3 powerups to play with in game\n" + 
          "7. Powerups can be accessed by pressing the keys 1, 2, and 3\n" + 
          "\nClick the Powerups Button for a description of each powerup!\n");
        }
        //GET INFO FROM FIRST THREE MENUS IN NEXT THREE STATEMENTS
        else if (e.getActionCommand().equals("Box1 selected")) {
            powerup1 = powerupSelector[0].getSelectedIndex();
        }
        else if (e.getActionCommand().equals("Box2 selected")) {
            powerup2 = powerupSelector[1].getSelectedIndex();
        }
        else if (e.getActionCommand().equals("Box3 selected")) {
            powerup3 = powerupSelector[2].getSelectedIndex();
        }
        //AUTOMATICALLY SET MENUS TO SHOW POWERUPS
        else if (e.getActionCommand().equals("Box4 selected")) {
            suggestedDeck = powerupSelector[3].getSelectedIndex();
            
            switch(suggestedDeck)
            {
                //Teleport, Flashlight, Compass
                case 1:
                    powerupSelector[0].setSelectedIndex(1);
                    powerupSelector[1].setSelectedIndex(3);
                    powerupSelector[2].setSelectedIndex(5);
                    break;
                //Auto-Respawn, Invisibility, Immobilize
                case 2:
                    powerupSelector[0].setSelectedIndex(2);
                    powerupSelector[1].setSelectedIndex(4);
                    powerupSelector[2].setSelectedIndex(8);
                    break;
                //Auto-Respawn, Extra Blocks, Immovable Blocks
                case 3:
                    powerupSelector[0].setSelectedIndex(2);
                    powerupSelector[1].setSelectedIndex(7);
                    powerupSelector[2].setSelectedIndex(9);
                    break;
                //Teleportation, Compass, Fast Block Breaking
                case 4:
                    powerupSelector[0].setSelectedIndex(1);
                    powerupSelector[1].setSelectedIndex(5);
                    powerupSelector[2].setSelectedIndex(10);
                    break;
                //Invisibility, New Spawn, Immobilize
                case 5:
                    powerupSelector[0].setSelectedIndex(4);
                    powerupSelector[1].setSelectedIndex(6);
                    powerupSelector[2].setSelectedIndex(8);
                    break;
                default:
                    break;
            }
        }
    }   
}