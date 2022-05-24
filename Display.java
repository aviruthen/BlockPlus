import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.lang.Math;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Arranges and draws all objects on the graphical interface
 * 
 * Useful Methods:
 *                  toggleInvisible: Turns user pic off;
 *                                   user becomes undetectable
 *                  getInvisibility: returns invis status
 *                  toggleCompass: Displays or turns off compass
 * 
 * @author Avi and Amar Ruthen
 * @version 12.21.2020
 */
public class Display extends JPanel {
    Character user;
    Base userBase;
    Base compBase;
    PowerUps p;
    Block[] userBlocks = new Block[1000];
    Block[] immovableBlocks = new Block[10];
    Block[] neutralBlocks = new Block[1000];
    Block[] compBlocks = new Block[1000];
    Drone[] drones = new Drone[100];
    Grid grid;
    Compass compass;
    
    Font myFont;
    
    boolean userIsInvisible = false;
    boolean toggleCompass = false;
    boolean restocked = false;
    int numSquaresAcross;
    int squareSize;
    double theta;
    
    /**
     * CONSTRUCTOR displays all objects created and the grid
     * Certain powerups also require the display
     */
    public Display(Base userBase, Base compBase, Character user, Grid grid, 
                   Block[] userBlocks, Block[] immovableBlocks, Block[] neutralBlocks, 
                   Block[] compBlocks, Compass compass, Drone[] drones, 
                   int w, int h, int numSquaresAcross) {
        this.userBase = userBase;
        this.compBase = compBase;
        this.user = user;
        this.grid = grid;
        this.userBlocks = userBlocks;
        this.immovableBlocks = immovableBlocks;
        this.neutralBlocks = neutralBlocks;
        this.compBlocks = compBlocks;
        this.drones = drones;
        this.compass = compass;

        this.numSquaresAcross = numSquaresAcross;
        squareSize = 600/numSquaresAcross;

        this.myFont = new Font("Verdana", Font.BOLD, 16); // nice font for display purposes
        setPreferredSize(new Dimension(w, h));            // set size of display
    }
    
    /**
     * Repaints all objects on screen; called in Graphics Window by a timer calling
     * repaint()
     * 
     * @param g the graphics object, automatically sent when repaint() is called
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // clear the old drawings
        
                                 //Allows for smooth rotational graphics
        Graphics2D g2d = (Graphics2D) g; 

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        
        // creates background color of window the size of screen              
        g.setColor(Color.BLACK); 
        g.fillRect(0, 0, 1000, 750);
        
        //Draw the grid and spawn areas (the green 5x5 boards)
        grid.draw(g);
        grid.drawSpawn(g, userBase.initialX, userBase.initialY);
        grid.drawSpawn(g, compBase.initialX, compBase.initialY);
        
        /*
         * ADD BASES
         */
        userBase.draw(g, this);
        compBase.draw(g, this);

        /*
         * ADD ALL TYPES OF BLOCKS
         */
        for (Block uB : userBlocks)
            if(uB != null)
                uB.draw(g);
        
        for (Block iB : immovableBlocks)
            if(iB != null)
                iB.draw(g);
                
        for (Block nB : neutralBlocks)
            if(nB != null)
                nB.draw(g);
                
        for (Block cB : compBlocks)
            if(cB != null)
                cB.draw(g);
        
        /*
         * ADD DRONES
         */        
        for (Drone d : drones)
            if (d != null && d.health > 0)
                d.draw(g, this);
        
        /*
         * DRAWS USER IF NOT INVISBLE
         */
        if(!userIsInvisible)
            user.draw(g, this);
            
        /*
         * DRAWS THE FOG THAT LIMITS VIEWING AREA
         * LEAVE THIS AFTER OTHER OBJECTS ABOVE ALWAYS
         */
        grid.drawFog(g);
        
        /*
         * DRAW AND ROTATION OF COMPASS
         */
        theta = compass.rotateCompass(grid.toGridCoords(compBase.xLoc, true) + 1, grid.toGridCoords(compBase.yLoc, false) + 1,
                                            grid.toGridCoords(user.xLoc, true), grid.toGridCoords(user.yLoc, false));
        if((theta != -1) && (toggleCompass)) 
        {
            compass.drawCompass(g, theta, 500, 50);
            g.setColor(Color.GREEN);
            g.drawOval(453 - (int)(12 * Math.sin(theta)), 5 + (int)(5 * Math.cos(theta)), 90, 90);
        }
        
        //Displays restocked powerups text every five minutes
        g.setFont(myFont);
        if(restocked)
        {
            g.setColor(Color.WHITE);
            g.drawString("Powerups Restocked!", 350, 50);
        }
    }
    
    /**
     * Toggles the invisibility of the user
     */
    public void toggleInvisible()
    {
            user.invisible = !user.invisible;
            userIsInvisible = !userIsInvisible;
    }
    
    /**
     * Toggles the visibility of compass
     */
    public void toggleCompass()
    {
        toggleCompass = !toggleCompass;
    }
}