import java.awt.*;
import javax.lang.model.util.ElementScanner6;
import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;
import java.awt.geom.*;

/**
 * Provides a window for buttons, entry boxes and displaying of grahpics 1. Sets
 * up all imporant control variable values, 2. makes a Window for buttons and
 * graphics and timer control 3. and makes a Display to show graphics 4. starts
 * the timer to start animation
 * 
 * @author Avi and Amar Ruthen
 * @version 12.21.2020
 */
public class GraphicsWindow extends JFrame implements ActionListener, KeyListener {
    int TIMER_DELAY; // milliseconds for updating graphics/motion, 
                     //subject to change (see buttons)
    Timer t, p1, p2, p3, compassTimer, compassCooldown, powerupsCooldown, 
          droneTimer, timerCounter;

    // components and objects:
    JPanel rightPanel;
    JLabel blockCount, xCharCoords, yCharCoords;
    JTextField info;
    Display display;
    Base userBase, compBase;
    Character user;
    Color userColor = Color.RED;

    //arrays of all the blocks
    Block[] userBlocks = new Block[1000];
    Block[] immovableBlocks = new Block[10];
    Block[] neutralBlocks = new Block[2000];
    Block[] compBlocks = new Block[1000];

    Grid grid;
    PowerUps powerups;
    Compass compass;
    Block brokenCompBlock; //broken blocks

    // size of window:
    int WIDTH;
    int HEIGHT;
    
    int numSquaresAcross; //number of squares in the grid
    int letRun = 1; //becomes 0 if the game ends
    int squareSize; //size of each grid square

    //timers
    int timerCount   = 0;
    int timerCount2  = 0;
    int timerCount3  = 0;
    int compassCount = 0;
    int timerCounts[] = new int[100]; //multiple timer counters
    
    //faceDirections (these values are always constant)
    int UP    = 0;
    int DOWN  = 1;
    int LEFT  = 2;
    int RIGHT = 3;
    
    int powerup1, powerup2, powerup3;
    
    //number of powerup uses
    int teleportUses       = 5;
    int respawnUses        = 1;
    int invisUses          = 3;
    int newSpawnUses       = 1;
    int immobilizeUses     = 3;
    int powerCooldownCount = 0;
    
    //miscellaneous integers (block indexes)
    int blockIndex         = 0;
    int brokenIndex        = -1;
    int blockBreakingSpeed = 5;
    int broken             = 0; //0-nothing broken, 1-compBlock, 2-neutralBlock
    
    //drone integers
    int droneSpeed = 2;
    int droneTimerCount = 0;
    int numberOfDrones = 500;
    Drone[] drones = new Drone[numberOfDrones];
    
    //movement and cooldown booleans
    boolean smoothMovement   = false;
    boolean userImmobilized  = false;
    boolean multiMovement    = false;
    boolean cooldown         = false;

    /**
     * Constructor for Window
     */
    public GraphicsWindow(int powerup1, int powerup2, int powerup3) {
        // calls constructor of the "super class"- JFrame - arg is titlebar name
        super("BlockPlus");
        
        this.powerup1 = powerup1;
        this.powerup2 = powerup2;
        this.powerup3 = powerup3;
        
        // the JFrame/Graphics Window size:
        WIDTH = 1000;
        HEIGHT = 700;
        
        TIMER_DELAY = 10; // SPEED of graphics, lower number = faster
        // (# of milliseconds between updates to graphics)
        
        numSquaresAcross = 25; // will establish size of each square
        // (you could ask the user what size they want)
    

        // size of 1 blank square - change 600 for larger or smaller grid
        squareSize = 600 / numSquaresAcross;
        
        Arrays.fill(timerCounts, 0);
        
        Base t = new Base(0, 0, squareSize, true);
        Grid g = new Grid (0, 0, squareSize); //temporary grid
        int userBaseX = t.setRandomX(g);
        int userBaseY = t.setRandomY(userBaseX, g);
        t.user = false; //set computer coords
        int compBaseX = t.setRandomX(g);
        int compBaseY = t.setRandomY(compBaseX, g);

        //user spawns on the outer ring
        int userX = (int)(Math.random()*5);
        int userY = 0;
        if (userX == 0 || userX == 4)
            userY = (int)(Math.random()*5); //0, 1, 2, 3, 4
        else
            userY = 4*(int)(Math.random()*2); //0, 4

        //userBase  =  new Base(18 + userX, 13 + userY, squareSize, true);
        userBase  =  new Base(17 + userX, 12 + userY, squareSize, true);
        userBase.userOnTop = t.userOnTop;
        userBase.setInitialCoords(userBaseX, userBaseY); //sets surrounding ring

        compBase  =  new Base(compBaseX + (17 + userX - userBaseX), 
                       compBaseY + (12 + userY - userBaseY), squareSize, false);
        compBase.setInitialCoords(compBaseX, compBaseY); //sets surrounding ring

        grid = new Grid(17 + userX - userBaseX, 
                        12 + userY - userBaseY, squareSize);
        user = new Character(20, 15, squareSize, grid);
        setNeutralBlocks(); //sets neutral blocks on map
        
        compass = new Compass(user.xLoc, user.yLoc);

        display = new Display(userBase, compBase, user, grid, userBlocks, 
                              immovableBlocks, neutralBlocks, compBlocks, 
                              compass, drones, WIDTH, HEIGHT, numSquaresAcross);
        
        powerups = new PowerUps(userBase, compBase, user, grid, userBlocks, 
                            immovableBlocks, neutralBlocks, compBlocks, 
                            WIDTH, HEIGHT, numSquaresAcross);
        
        //displays all the objects
        this.userBase = this.display.userBase;
        this.compBase = this.display.compBase;
        this.userBlocks = this.display.userBlocks;
        this.immovableBlocks = this.display.immovableBlocks;
        this.neutralBlocks = this.display.neutralBlocks;
        this.compBlocks = this.display.compBlocks;
        this.compass = this.display.compass;
        this.drones = this.display.drones;
        
        //sets panels and labels
        rightPanel = new JPanel();
        rightPanel.setBackground(Color.BLACK);
        xCharCoords = new JLabel();
        yCharCoords = new JLabel();
        blockCount = new JLabel();
       
        //sets color of the font
        xCharCoords.setForeground(Color.WHITE);
        yCharCoords.setForeground(Color.WHITE);
        blockCount.setForeground(Color.WHITE);

        //shows coordinates and block counts on right panel
        rightPanel.add(xCharCoords);
        rightPanel.add(yCharCoords);
        rightPanel.add(blockCount);

        // allows you to place things where you want them (see this.add() below)
        this.setLayout(new BorderLayout());


        // add the display to this JFrame:
        this.add(display, BorderLayout.CENTER);
        this.add(rightPanel, BorderLayout.EAST);

        // final setup
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    /**
     * Creates a Timer object and starts timer
     */
    public void startTimer() {
        t = new Timer(TIMER_DELAY, this);
        t.setActionCommand("timerFired");
        t.start();
    }
    /**
     * Creates a Timer object for the invisibility cooldown
     */
    public void startP1Timer() 
    {
        p1 = new Timer(1000, this);
        p1.setActionCommand("p1Fired");
        p1.start();
    }
    /**
     * Creates a Timer object for immobilization
     */
    public void startP2Timer() 
    {
        p2 = new Timer(100, this);
        p2.setActionCommand("p2Fired");
        p2.start();
    }
    /**
     * Creates a Timer object for 
     */
    public void startP3Timer() 
    {
        p3 = new Timer(1000, this);
        p3.setActionCommand("p3Fired");
        p3.start();
    }
    /**
     * Creates a Timer object for showing the compass.
     */
    public void startCompassTimer()
    {
        compassTimer = new Timer(1000, this);
        compassTimer.setActionCommand("compassTimer");
        compassTimer.start();
    }
    /**
     * Creates a Timer object for the cooldown between showing the compass
     */
    public void startCompassCooldown()
    {
        compassCooldown = new Timer(1000, this);
        compassCooldown.setActionCommand("compassCooldown");
        compassCooldown.start();
        cooldown = true;
    }
    /**
     * Creates a Timer object for a powerup cooldown
     * @param delay integer for the delay between timer ticks
     */
    public void startPowerupsCooldown(int delay)
    {
        powerupsCooldown = new Timer(delay, this);
        powerupsCooldown.setActionCommand("powerupsCooldown");
        powerupsCooldown.start();
    }
    /**
     * Creates a Timer for the Drones
     */
    public void startDroneTimer()
    {
        droneTimer = new Timer(10, this);
        droneTimer.setActionCommand("droneTimer");
        droneTimer.start();
    }
    /**
     * Creates a Timer for showing that the powerups have restocked
     */
    public void startTimerCounter()
    {
        timerCounter = new Timer(1000, this);
        timerCounter.setActionCommand("timerCounter");
        timerCounter.start();
    }

    /**
     * Called automatically when a button is pressed
     * 
     * @param e contains information about button that was pressed/sent
     *          automatically
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("timerFired")) // timer has fired
        {
            //compBlock broken
            if (broken == 1 && timerCount2 >= blockBreakingSpeed)
            {
                userImmobilized = false; //user no longer immobilized
                p2.stop();
                timerCount2 = 0; //resets timerCount2
                compBlocks[brokenIndex] = null; //block is broken
                broken = 0; //nothing broken anymore
            }
            //neutral block broken
            else if (broken == 2 && timerCount2 >= blockBreakingSpeed)
            {
                //same as above
                userImmobilized = false;
                p2.stop();
                timerCount2 = 0;
                neutralBlocks[brokenIndex] = null;
                broken = 0;
            }
            else if (timerCount2 >= 50)
            {
                //immobilization lasts no longer than 5 seconds
                for (Drone d : drones)
                {
                    if (d != null)
                        d.immobilize = false;
                }
                p2.stop();
                timerCount2 = 0;
            }
            else if(powerCooldownCount >= 5)
            {
                cooldown = false;
                
                //resets powerup uses
                teleportUses = 5;
                respawnUses = 1;
                invisUses = 3;
                newSpawnUses = 1;
                powerCooldownCount = 0;

                timerCounts[0] = -3; //3 seconds away
                powerupsCooldown.stop(); //cooldown stops and repeats
                startPowerupsCooldown(60000); 
                display.restocked = true; //shows restocked words
            }
            if(timerCounts[0] == 0) //restocked words last 3 seconds
                display.restocked = false; 
            display.repaint(); // calls paintComponent to redraw everything
            //shows coordinates and blocks remaining
            xCharCoords.setText("Char x coordinate: " + 
                                grid.toGridCoords(user.xLoc, true));
            yCharCoords.setText("Char y coordinate: " + 
                                grid.toGridCoords(user.yLoc, false));
            blockCount.setText("Blocks Remaining: " + user.numBlocks);
        }
        //if uses is within 1 block in either direction of ceneter
        if (Math.abs(user.xLoc - (compBase.xLoc + 1)) <= 1 && 
            Math.abs(user.yLoc - (compBase.yLoc + 1)) <= 1)
        {
            if (letRun > 0) //ensure GameOverScreen occurs once
            {
                GameOverScreen g = new GameOverScreen(true);
                letRun--;
                this.setVisible(false); //gets rid of map display
                //stops all the timers
                t.stop();
                if (p1 != null)
                    p1.stop();
                if (p2 != null)
                    p2.stop();
                if (p3 != null)
                    p3.stop();
                if (compassTimer != null)
                    compassTimer.stop();
                if (compassCooldown != null)
                    compassCooldown.stop();
                powerupsCooldown.stop();
                droneTimer.stop();
                timerCounter.stop();
            }
        }
        if (e.getActionCommand().equals("p1Fired")) // timer has fired
           timerCount++;
        if (e.getActionCommand().equals("p2Fired")) // timer has fired
           timerCount2++;
        if (e.getActionCommand().equals("p3Fired")) // timer has fired
           timerCount3++;
        if((Math.abs(grid.toGridCoords(user.xLoc, true) - 
                     grid.toGridCoords(userBase.xLoc, true) - 1) <= 2) &&
                     (Math.abs(grid.toGridCoords(user.yLoc, false) - 
                     grid.toGridCoords(userBase.yLoc, false) - 1) <= 2))
            user.replenishBlocks(); //replenishes blocks when user is in base
        if (e.getActionCommand().equals("compassTimer")) // timer has fired
           compassCount++;
        if (e.getActionCommand().equals("compassCooldown")) // timer has fired
           compassCount++;
        if (e.getActionCommand().equals("powerupsCooldown")) // timer has fired
           powerCooldownCount++;
        if (e.getActionCommand().equals("timerCounter")) // timer has fired
           for(int t = 0; t < 100; t++)
                timerCounts[t]++;
        if (e.getActionCommand().equals("droneTimer")) // timer has fired
        {
            //goes through all the drones and checks if they are null
            for (Drone d : drones)
            {
                if (d != null)
                {
                    //checks if the drone has hit the user's base
                    boolean hitBase = d.taggedBase(userBase, grid);
                    if (d.health <= 0)
                        d = null;
                    //game is over if the drone hits the computer's base
                    else if (hitBase)
                    {
                        GameOverScreen g = new GameOverScreen(false);
                        this.setVisible(false); //display turned off

                        //turns off all the timers
                        t.stop();
                        if (p1 != null)
                            p1.stop();
                        if (p2 != null)
                            p2.stop();
                        if (p3 != null)
                            p3.stop();
                        if (compassTimer != null)
                            compassTimer.stop();
                        if (compassCooldown != null)
                            compassCooldown.stop();
                        powerupsCooldown.stop();
                        droneTimer.stop();
                        timerCounter.stop();
                    } 
                    //for speed drones
                    else if (d.droneType == 4 && droneTimerCount % 2 == 1)
                    {
                        //user tags the drones
                        if (user.getZone(grid, userBase) && 
                            !d.getZone(grid, userBase))
                        {
                            if (user.xLoc == d.xLoc && user.yLoc == d.yLoc)
                                d.health = 0;
                        }
                        //drones tage the user
                        if (!user.getZone(grid, userBase) && 
                            d.getZone(grid, userBase))
                        {
                            if (user.xLoc == d.xLoc && user.yLoc == d.yLoc)
                            {
                                powerups.autoRespawn(this); //user respawns
                                d.health = 0;
                            }
                        }
                        //drone moves 1 every 25 timer ticks
                        if (droneTimerCount % droneSpeed == 0)
                        {
                            //drones travel
                            d.travel(user, userBase, grid, userBlocks, 
                                     neutralBlocks, immovableBlocks);
                            //finds the broken block
                            String brokeBlock = d.blockCollision(neutralBlocks, 
                                                userBlocks, immovableBlocks);
                            //broken block is a neutral block
                            if (brokeBlock.charAt(0) == '1')
                            {
                                //finds index of neutral block in array
                                int neutralBIndex = Integer.parseInt(
                                brokeBlock.substring(2, brokeBlock.length()));
                                //drone cannot break immovable blocks
                                if (!neutralBlocks[neutralBIndex].immovable)
                                    neutralBlocks[neutralBIndex] = null;
                                d.health--; //drone loses health
                            }
                            //broken block is a user block
                            else if (brokeBlock.charAt(0) == '2')
                            {
                                //finds index of user block in array
                                int userBIndex = Integer.parseInt(
                                brokeBlock.substring(2, brokeBlock.length()));
                                //drone cannot break immovable blocks
                                if (!userBlocks[userBIndex].immovable)
                                    userBlocks[userBIndex] = null;
                                //drone loses health
                                d.health--;
                            }
                            //immovable block array
                            else if (brokeBlock.charAt(0) == '3')
                            {
                                //block is not broken
                                d.health--;
                            }
                        }
                    }
                    //all other drones (move twice as slow)
                    else if (droneTimerCount % 2 == 0)
                    {
                        //user tags the drones
                        if (user.getZone(grid, userBase) && 
                            !d.getZone(grid, userBase))
                        {
                            if (user.xLoc == d.xLoc && user.yLoc == d.yLoc)
                                d.health = 0;
                        }
                        //drones tage the user
                        if (!user.getZone(grid, userBase) && 
                            d.getZone(grid, userBase))
                        {
                            if (user.xLoc == d.xLoc && user.yLoc == d.yLoc)
                            {
                                powerups.autoRespawn(this); //user respawns
                                d.health = 0;
                            }
                        }
                        //drone moves once every 25 timer ticks
                        if (droneTimerCount % droneSpeed == 0)
                        {
                            //drone travels
                            d.travel(user, userBase, grid, userBlocks, 
                                     neutralBlocks, immovableBlocks);
                            //block-placing drones
                            if (d.droneType == 3)
                            {
                                //1 in 8 chance drones place blocks
                                int willPlaceBlock = (int)(8*Math.random());
                                if (willPlaceBlock == 0)
                                    //drones play the blocks
                                    d.placeCompBlock(userBlocks,immovableBlocks, 
                                    neutralBlocks, compBlocks, userBase, 
                                    compBase, grid);
                            }
                            String brokeBlock = d.blockCollision(neutralBlocks, 
                                                userBlocks, immovableBlocks);
                            //broken block is a neutral block
                            if (brokeBlock.charAt(0) == '1')
                            {
                                //finds index of neutral block in array
                                int neutralBIndex = Integer.parseInt(
                                brokeBlock.substring(2, brokeBlock.length()));
                                //drone cannot break immovable blocks
                                if (!neutralBlocks[neutralBIndex].immovable)
                                    neutralBlocks[neutralBIndex] = null;
                                d.health--; //drone loses health
                            }
                            //broken block is a user block
                            else if (brokeBlock.charAt(0) == '2')
                            {
                                //finds index of user block in array
                                int userBIndex = Integer.parseInt(
                                brokeBlock.substring(2, brokeBlock.length()));
                                //drone cannot break immovable blocks
                                if (!userBlocks[userBIndex].immovable)
                                    userBlocks[userBIndex] = null;
                                //drone loses health
                                d.health--;
                            }
                            //immovable block array
                            else if (brokeBlock.charAt(0) == '3')
                            {
                                //block is not broken
                                d.health--;
                            }
                        }
                    }
                }
            }
            //drone spawn logic
            //1 in 500 chance to spawn per 10 milliseconds (1/5 chance per s)
            int spawnDrone = (int)(25*Math.random());
            int nextIndex = -1;
            //spawns next available drone
            if (spawnDrone == 0)
            {
                for (int i = 0; i < drones.length; i++)
                {
                    if (drones[i] == null)
                    {
                        nextIndex = i;
                        i += 200000; //get out of for loop, save time
                    }
                }
                //spawns new drone at compBase
                if (nextIndex != -1)
                {
                    Drone t = new Drone(0, 0, squareSize, 0);
                    //gets a random drone type
                    int newDroneType = t.randomDroneType();
                    //stores random drone type into new drone
                    drones[nextIndex] = new Drone(compBase.xLoc + 1, 
                        compBase.yLoc + 1, squareSize, newDroneType);
                }
            }
            droneTimerCount++;
        }
        //invisibility lasts only five seconds
        if(timerCount >= 5)
        {
            display.toggleInvisible(); //on or off
            //resets timer and variables
            p1.stop();
            timerCount = 0;
        }
        //displays compass for only three seconds
        if ((compassCount > 3) && (display.toggleCompass))
        {
           display.toggleCompass(); //turns off compass
           //resets timers
           compassTimer.stop();
           startCompassCooldown();
           compassCount = 0;
           cooldown = true; //starts cooldown for displaying compass again
        }
        //30-second cooldown between showing compass
        if (compassCount > 30)
        {
            compassCooldown.stop();
            compassCount = 0;
            cooldown = false;
        }
    }
    
    /**
     * Shifts board according to the direction the user would like to move
     * 
     * @param faceDirection is the direction the user would like to move
     */
    public void move(int faceDirection)
    {
        //board moves opposite direction as faceDirection to simulate movement
        if (faceDirection == DOWN)
             faceDirection = UP;
        else if (faceDirection == UP)
            faceDirection = DOWN;
        else if (faceDirection == RIGHT)
            faceDirection = LEFT;
        else if (faceDirection == LEFT)
            faceDirection = RIGHT;
        
        //MOVES ALL OBJECTS IN THE OPPOSITE DIRECTION 
        //USER WOULD LIKE TO MOVE
        userBase.move(faceDirection);
        compBase.move(faceDirection);
        grid.move(faceDirection);
        for (Block b : userBlocks)
        {
            if (b != null)
                b.move(faceDirection);
        }
        for (Block b : immovableBlocks)
        {
            if (b != null)
                b.move(faceDirection);
        }
        for (Block b : compBlocks)
        {
            if (b != null)
                b.move(faceDirection);
        }
        for (Block b : neutralBlocks)
        {
            if (b != null)
                b.move(faceDirection);
        }
        for (Drone d : drones)
        {
            if (d != null)
                d.move(faceDirection);
        }
    }
    
    /**
     * Toggles the upward-facing picture of user to display for animation
     */
    public void faceUp()
    {
        user.faceDirection = UP;
        user.setImagePosition(user.faceDirection);
    }
    
    /**
     * Toggles the downward-facing picture of user to display for animation
     */
    public void faceDown()
    {
        user.faceDirection = DOWN;
        user.setImagePosition(user.faceDirection);
    }
    
    /**
     * Toggles the left-facing picture of user to display for animation
     */
    public void faceLeft()
    {
        user.faceDirection = LEFT;
        user.setImagePosition(user.faceDirection);
    }
    
    /**
     * Toggles the right-facing picture of user to display for animation
     */
    public void faceRight()
    {
        user.faceDirection = RIGHT;
        user.setImagePosition(user.faceDirection);
    }
    
    /**
     * The code for all the powerups
     * The powerups are then called by pressed keys 1, 2, and 3
     * Each key will call for a different powerup assignment depending
     * On how the user selected their powerups
     * 
     * @param powerupNumber calls for the specific powerup in switch
     */
    public void powerupAssignment(int powerupNumber)
    {
        //NOTE: ALL POWERUPS RESTOCKED AFTER 5 MINUTES EXCEPT IMMOVABLE BLOCKS!
        switch(powerupNumber)
        {
            /*
             * TELEPORTATION
             * USES: 5
             */
            case 1:
                if(teleportUses > 0)
                {
                    powerups.teleport(this);
                    teleportUses--;
                }
                break;
            /*
             * AUTO-RESPAWN
             * USES: 1
             */
            case 2: 
                if(respawnUses > 0)
                {
                    powerups.autoRespawn(this);
                    respawnUses--;
                }
                break;
            /*
             * FLASHLIGHT
             * USES: INFINITE
             */
            case 3:
                powerups.toggleFlashlight();
                break;
            /*
             * INVISIBILITY
             * USES: 3
             */
            case 4:
               if((!(display.userIsInvisible)) && (invisUses > 0))
               {
                   startP1Timer();
                   display.toggleInvisible();
                   invisUses--;
               }
                break;
            /*
             * COMPASS
             * USES: ONCE PER 30 SECONDS. EACH USE LASTS 3 SECONDS
             */
            case 5:
                if((!cooldown) && (!display.toggleCompass))
                {
                    display.toggleCompass();
                    startCompassTimer();
                }
                break;
                
            /*
             * NEW-SPAWN
             * USES: 1
             */
            case 6: 
                if(newSpawnUses > 0)
                {
                    powerups.newSpawn();
                    powerups.autoRespawn(this);
                    userBase.setCoords(19, 14);
                    newSpawnUses--;
                }
                
                //NEXT FOUR LOOPS REMOVE ALL BLOCKS IN NEW SPAWNING AREA
                for (int i = 0; i < userBlocks.length; i++)
                {
                    if (userBlocks[i] != null)
                        if((Math.abs(grid.toGridCoords(userBlocks[i].xLoc, true)
                            - grid.toGridCoords(userBase.xLoc, true)) <= 3) ||
                         ((Math.abs(grid.toGridCoords(userBlocks[i].yLoc, false)
                            - grid.toGridCoords(userBase.yLoc, false)) <= 3)))
                                userBlocks[i] = null;
                }
                for (int i = 0; i < immovableBlocks.length; i++)
                {
                    if (immovableBlocks[i] != null)
                        if((Math.abs(grid.toGridCoords(
                            immovableBlocks[i].xLoc, true) 
                            - grid.toGridCoords(userBase.xLoc, true)) <= 3)  ||
                          ((Math.abs(grid.toGridCoords(
                              immovableBlocks[i].yLoc, false) 
                            - grid.toGridCoords(userBase.yLoc, false)) <= 3)))
                               immovableBlocks[i] = null;
                }
                for (int i = 0; i < compBlocks.length; i++)
                {
                    if (compBlocks[i] != null)
                        if((Math.abs(grid.toGridCoords(compBlocks[i].xLoc, true) 
                            - grid.toGridCoords(userBase.xLoc, true)) <= 3)  ||
                         ((Math.abs(grid.toGridCoords(compBlocks[i].yLoc, false) 
                            - grid.toGridCoords(userBase.yLoc, false)) <= 3)))
                                compBlocks[i] = null;
                }
                for (int i = 0; i < neutralBlocks.length; i++)
                {
                    if (neutralBlocks[i] != null)
                        if((Math.abs(grid.toGridCoords(
                            neutralBlocks[i].xLoc, true)
                            - grid.toGridCoords(userBase.xLoc, true)) <= 3)  ||
                          ((Math.abs(grid.toGridCoords(
                              neutralBlocks[i].yLoc, false)
                            - grid.toGridCoords(userBase.yLoc, false)) <= 3)))
                                neutralBlocks[i] = null;
                }
                break;
                
            /*
             * EXTRA BLOCKS
             * USES: 1 TIME PERMANENT USE
             */
            case 7: 
                powerups.extraBlocks();
                break;
            
            /*
             * IMMOBILIZATION
             * USES: 3
             */
            case 8:
                if(immobilizeUses > 0)
                {
                    startP2Timer();
                    for (Drone d : drones)
                    {
                        if (d != null)
                            d.immobilize = true;
                    }
                    immobilizeUses--;
                }
                break;
            
            /*
             * IMMOVABLE BLOCKS
             * USES: 10 BLOCKS
             * NOTE: NO RESTOCK
             */
            case 9:
                int t;
                if(user.canPlaceBlock(userBlocks, immovableBlocks, 
                                      neutralBlocks, compBlocks, userBase, 
                                      compBase, true, -1, -1, -1))
                    t = user.placeBlock(immovableBlocks, 
                        10 - user.numImmovableBlocks, Color.GRAY, true);
                break;
                
            /*
             * FAST BLOCK BREAKING
             * USES: 1 TIME PERMANENT USE
             */
            case 10:
                blockBreakingSpeed = 
                        powerups.fastBlockBreaking(blockBreakingSpeed);
            /*
             * MUST LEAVE IN
             */
            default:
                break;
        }
    }
    
    /**
     * Called by the timer. MoveCode, breaking blocks, powerup use, etc. done here!
     * This is followed by a repaint of everything
     */
    public void keyPressed(KeyEvent e) 
    {
        //Listens for a key press, tracks which key was pressed
        int code = e.getKeyCode();
        
        //If user is not immobilized (occurs during block-breaking process)
        if (!userImmobilized)
        {
            /*
             * MultiMovement: Turning and changing position are independent
             * Block placing is a bit easier but  more difficult to control
             */
            if (multiMovement)
            {
                //Arrows change the direction user is facing
                if (code == KeyEvent.VK_UP)
                    faceUp();
                if (code == KeyEvent.VK_DOWN)
                    faceDown();
                if (code == KeyEvent.VK_LEFT)
                    faceLeft();
                if (code == KeyEvent.VK_RIGHT)
                    faceRight();
                    
                //WASD Keys move user around the map
                if (code == KeyEvent.VK_W)
                {
                    if (user.canMove(compBlocks, neutralBlocks, 
                                     immovableBlocks, false, 0))
                        move(0);
                }
                if (code == KeyEvent.VK_S)
                {
                    if (user.canMove(compBlocks, neutralBlocks, 
                                     immovableBlocks, false, 1))
                        move(1);
                }
                if (code == KeyEvent.VK_A)
                {
                    if (user.canMove(compBlocks, neutralBlocks, 
                                     immovableBlocks, false, 2))
                        move(2);
                }
                if (code == KeyEvent.VK_D)
                {
                    if (user.canMove(compBlocks, neutralBlocks, 
                                     immovableBlocks, false, 3))
                        move(3);
                }
            }
            
            /*
             * Not MultiMovement: User must turn towards specific direction 
             * to move in that direction 
             * 
             * Easier to control than multiMovement
             */
            else if (!multiMovement)
            {
                //Arrows keys turn user and move user in specified direction
                if (code == KeyEvent.VK_UP)
                {
                    //smooth movement means user will turn and 
                    //move at the same time
                    if (smoothMovement)
                    {
                        faceUp();
                        if (user.canMove(compBlocks, neutralBlocks, 
                                         immovableBlocks, false, -1))
                        {
                            move(user.faceDirection);
                        }
                    }
                    //User must turn first, then move
                    else
                    {
                        if (user.faceDirection != 0)
                            faceUp();
                        else if (user.canMove(compBlocks, neutralBlocks, 
                                              immovableBlocks, false, -1))
                            move(user.faceDirection);
                    }
                }

                if (code == KeyEvent.VK_DOWN)
                {
                    //smooth movement means user will turn 
                    //and move at the same time
                    if (smoothMovement)
                    {
                        faceDown();
                        if (user.canMove(compBlocks, neutralBlocks, 
                                         immovableBlocks, false, -1))
                        {
                            move(user.faceDirection);
                        }
                    }
                    //User must turn first, then move
                    else
                    {
                        if (user.faceDirection != 1)
                            faceDown();
                        else if (user.canMove(compBlocks, neutralBlocks, 
                                              immovableBlocks, false, -1))
                            move(user.faceDirection);
                    }
                }
                
                if (code == KeyEvent.VK_LEFT)
                {
                    //smooth movement means user will turn 
                    //and move at the same time
                    if (smoothMovement)
                    {
                        faceLeft();
                        if (user.canMove(compBlocks, neutralBlocks, 
                                         immovableBlocks, false, -1))
                        {
                            move(user.faceDirection);
                        }
                    }
                    //User must turn first, then move
                    else
                    {
                        if (user.faceDirection != 2)
                            faceLeft();
                        else if (user.canMove(compBlocks, neutralBlocks, 
                                              immovableBlocks, false, -1))
                            move(user.faceDirection);
                    }
                }
                
                if (code == KeyEvent.VK_RIGHT)
                {
                    //smooth movement means user will turn
                    //and move at the same time
                    if (smoothMovement)
                    {
                        faceRight();
                        if (user.canMove(compBlocks, neutralBlocks, 
                                         immovableBlocks, false, -1))
                        {
                            move(user.faceDirection);
                        }
                    }
                    //User must turn first, then move
                    else
                    {
                        if (user.faceDirection != 3)
                            faceRight();
                        else if (user.canMove(compBlocks, neutralBlocks, 
                                              immovableBlocks, false, -1))
                            move(user.faceDirection);
                    }
                }
                //TOGGLES SMOOTH MOVEMENT
                if (code == KeyEvent.VK_M)
                {
                    smoothMovement = !smoothMovement;
                }
            }
            
            //TOGGLES MULTIMOVEMENT
            if (code == KeyEvent.VK_N)
            {
                multiMovement = !multiMovement;
            }
            
            //PLACES USER BLOCKS WHEN SPACE KEY PRESSED
            if (code == KeyEvent.VK_SPACE)
            {
                if (user.canPlaceBlock(userBlocks, immovableBlocks, 
                                       compBlocks, neutralBlocks, userBase, 
                                       compBase, false, -1, -1, -1))
                {
                    blockIndex = user.placeBlock(userBlocks, blockIndex, 
                                                 userColor, false);
                }
            }
            
            //BREAKS BLOCKS WHEN B KEY PRESSED
            //NOTE: cannot break immovable blocks
            if (code == KeyEvent.VK_B)
            {
                brokenIndex = user.breakBlock(compBlocks, neutralBlocks);
                if (brokenIndex % 1009 == 0)
                {
                    brokenIndex /= 1009;
                    brokenIndex--;
                    compBlocks[brokenIndex].setColor(Color.MAGENTA);
                    broken = 1;
                    startP2Timer();
                    userImmobilized = true;
                }
                else if (brokenIndex % 1013 == 0)
                {
                    brokenIndex /= 1013;
                    brokenIndex--;
                    neutralBlocks[brokenIndex].setColor(Color.MAGENTA);
                    broken = 2;
                    startP2Timer();
                    userImmobilized = true;
                }
            }
            
            //HOTKEYS FOR POWERUPS
            if(code == KeyEvent.VK_1)
            {
               powerupAssignment(powerup1);
            }
            if(code == KeyEvent.VK_2)
            {
               powerupAssignment(powerup2);
            }
            if(code == KeyEvent.VK_3)
            {
               powerupAssignment(powerup3);
            }
        }
    }
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
    
    /**
     * Randomly adds breakable and unbreakable neutral blocks to map
     * Randomized generation allows for different maps
     */
    public void setNeutralBlocks()
    {
        for (int i = 0; i < neutralBlocks.length; i++)
        {
            int nBX = 0;        //neutralBlockXLoc
            int nBY = 0;        //neutralBlockYLoc
            boolean sameLoc = false;
            nBX = (int)(grid.gridSideLength*Math.random());
            nBY = (int)(grid.gridSideLength*Math.random());
            
            /*
             * Generates a random x and y location for a block
             * Tests if another block already occupies same space
             * Tests if block is in spawn location
             * Randomly assigns the block as immovable or not (10% immovable)
             * Creates block when all conditions met
             */
            do {
                sameLoc = false;
                
                //Generate random block location
                nBX = (int)(grid.gridSideLength*Math.random());
                nBY = (int)(grid.gridSideLength*Math.random());
                
                //checks if new location is already occupied. 
                 for (int j = 0; j <= i; j++)
                {
                    if (neutralBlocks[j] != null)
                        if (grid.toGridCoords(neutralBlocks[j].xLoc, true) == nBX 
                        &&
                        grid.toGridCoords(neutralBlocks[j].yLoc, false) == nBY)
                            sameLoc = true;
                }
                //conditions of while loop: Not within the bases, not in same location
                //as neutral blocks
            } 
            //Checks that block is not in spawning locations
            while ((Math.abs(grid.toGridCoords(userBase.yLoc + 1, false) 
                                                - nBY) <= 2 && 
                    Math.abs(grid.toGridCoords(userBase.xLoc + 1, true) 
                                                - nBX) <= 2) ||
                    (Math.abs(grid.toGridCoords(compBase.yLoc + 1, false) 
                                                - nBY) <= 2 && 
                    Math.abs(grid.toGridCoords(compBase.xLoc + 1, true) 
                                                - nBX) <= 2) || 
                    sameLoc);
            
            //Randomly assigns block as immovable (10% chance)
            int isImmovable = (int)(10*Math.random());
            
            //Block is created
            if (isImmovable == 0)               
                neutralBlocks[i] = new Block(nBX + grid.xLoc, nBY + grid.yLoc, 
                                           squareSize, false, Color.GRAY, true);
            else
                neutralBlocks[i] = new Block(nBX + grid.xLoc, nBY + grid.yLoc, 
                                         squareSize, false, Color.GREEN, false); 
        }
    }
}