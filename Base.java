import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;

/**
 * Contains all the necessary information/methods about the base, including 
 * the base's coordinates, which zone the base is in, whether or not the base
 * is the user's or the computer's, etc.
 * 
 * Useful Methods:
 *          setInitialCoords(): Sets the coordinates of the base in the grid to
 *                              draw green spawning area around it.
 *          setZone(): Randomly sets the zone the base is in (upper or lower)
 *          setRandomX(): Selects a random possible x-coordinate
 *          setRandomY(): Selects a random possible y-coordinate
 *          draw(): Draws the base picture
 *
 * @Avi and Amar Ruthen @12.22.2020
 */
public class Base {
    
    /***************************************************************************
       IMPORTANT!!!!!!: The xLoc and yLoc for the Base correlate to the location 
       of the upper left-corner of the base picture, NOT the middle
    ***************************************************************************/
    int xLoc, yLoc; //the raw coordinates for the base
    int initialX, initialY; //the position of the base within the grid
    int squareSize; //the size of each grid tile
    int numMoves; //keeps track of the number of "moves" the base makes
                  //the user is always centered around the screen, so the base
                  //must move to simulate user movement
    boolean user; //tracks if the base is the user's or not
    boolean userOnTop = setZone(); //sets the zone for the user's base
    BufferedImage basePic = null;

    /**
     * Constructor for objects of class Base
     */
    public Base(int xLoc, int yLoc, int squareSize, boolean user) {
        //stores values from constructor into class variables
        this.xLoc = xLoc;
        this.yLoc = yLoc;
        this.squareSize = squareSize;
        this.user = user;
        numMoves = 0; //initializes number of moves
        
        //displays the base picture depending if the base is the user's or the
        //computer's base
        if (user)
        {
            try {
                basePic = ImageIO.read(this.getClass().getResourceAsStream(
                          "resources/MilitaryLogo.png")); //name of image
            } catch (Exception e) {}
        }
        else
        {
            try {
                basePic = ImageIO.read(this.getClass().getResourceAsStream(
                          "resources/MilitaryLogo2.png")); //name of image
                                                                                
            } catch (Exception e) {}
        }
    }

    /**
     * Moves the base up, down, left, or right
     * 
     * @param moveCode 0-3 code, 0 = Up, 1 = Down, 2 = Left, 3 = Right
     */
    public void move(int moveCode) {
        numMoves++;
        switch (moveCode) {
            case 0: // moves up
                yLoc--;
                break;

            case 1: // move down
                yLoc++;
                break;

            case 2: // move left
                xLoc--;
                break;

            case 3: // move down
                xLoc++;
                break;

            default: // Don't move
                break;
        }
    }
    /**
     * Sets the base's coordinates within the grid
     * 
     * @param x The x-coordinate of the base on the grid
     * @param y The y-coordinate of the base on the grid
     */
    public void setInitialCoords(int x, int y)
    {
       initialX = x;
       initialY = y;
    }
    /**
     * Determines if the base should be in the upper zone or the lower zone
     * @return true if the zone is the upper zone
     */
    public boolean setZone()
    {
        int top = (int)(Math.random()*2); //50-50 chance of upper or lower
        if (top == 0)
            return true;
        return false;
    }
    /**
     * Randomly selects an x-coordinate base location for the user. If the base
     * is in the upper zone, the x-coordinate 4 is favored, and the probability
     * that the base spawns at a coordinate greater than 4 decreases by a 
     * quartic function. The base also can spawn at x = 3,2,1, but these
     * probabilities are very low. If the base is in the lower zone, then the
     * x-coordinate 194 is favored, with x=197,196,195 having low probabilities
     * of spawning. The grid ranges from x = 0 to x = 199.
     * 
     * 
     * @param grid Contains all the information about the grid
     * @return integer value of the user's base's x-coordinate in the grid
     */
    public int setRandomX(Grid grid)
    {
        int userBaseX = 0;
        int potentialSpawns = grid.gridSideLength - 4;
        //optimal tile: (4, 4)
        if ((userOnTop && user) || (!userOnTop && !user))
        {
            //selects random userBaseX with increasing probability towards (4,4)
            userBaseX = (int)(Math.random() * Math.pow(potentialSpawns, 4)) 
                        + 15000000;
            double c = (double)userBaseX;
            if (c < Math.pow(potentialSpawns,4))
                userBaseX = grid.gridSideLength - 1 - (int)(Math.pow(c, 0.25));
            //for selecting a tile beyond (4,4) [P(X<4) ~ 1.08%]
            else
            {
                c -= (double)Math.pow(potentialSpawns,4);
                //53.3% odds of spawning at X = 3
                if (c < 8000000.0)
                    userBaseX = 3;
                //40.0% odds of spawning at X = 2
                else if (c < 14000000.0)
                    userBaseX = 2;
                //0.067% odds of spawning at X = 1
                else
                    userBaseX = 1;
            }
        }
        //optimal tile: (195, 195)
        else
        {
            potentialSpawns = grid.gridSideLength - 6;
            userBaseX = (int)(Math.random() * Math.pow(potentialSpawns, 4)) 
                        + 15000000;
            double c = (double)userBaseX;
            if (c < (double)Math.pow(potentialSpawns,4))
                //selects an xLoc with increasing probability towards (194,194)
                userBaseX = 1 + (int)(Math.pow(c, 0.25));
            //for selecting a tile beyond (194,194) [P(X>194) ~ 1.08%]
            else
            {
                c -= (double)Math.pow(potentialSpawns,4);
                //53.3% odds of spawning at X = 195
                if (c < 8000000.0)
                    userBaseX = grid.gridSideLength - 5;
                //40.0% odds of spawning at X = 196
                else if (c < 14000000.0)
                    userBaseX = grid.gridSideLength - 4;
                //0.067% odds of spawning at X = 197
                else
                    userBaseX = grid.gridSideLength - 3;
            }
        }
        return userBaseX; //returns x-coordinate of userBase
    }
    /**
     * Randomly selects a y-coordinate for the user's base following the same
     * logic as selecting the x-coordinate. However, if the base is in the upper
     * zone, the sum of the coordinates cannot exceed 194 (so the base does not
     * spawn on or over the boundary line). If the base is in the lower zone,
     * the sum of the coordinates must be greater than 202 for a similar reason.
     * 
     * @param userBaseX The x-coordinate value of the base in the grid
     * @param grid Contains all the information about the grid
     * @return integer value of the y-coordinate of the base in the grid
     */
    public int setRandomY(int userBaseX, Grid grid)
    {
        int userBaseY = 0; //initializes userBaseY
        int potentialSpawns = grid.gridSideLength - 4;
        if ((userOnTop && user) || (!userOnTop && !user)) //on top
        {
            do
            {
                //selects random userBaseX with increasing probability towards (4,4)
                userBaseY = (int)(Math.random()*Math.pow(potentialSpawns, 4)) + 15000000;
                double c = (double)userBaseY;
                if (c < Math.pow(potentialSpawns,4))
                    userBaseY = grid.gridSideLength - 1 - (int)(Math.pow(c, 0.25));
                //for selecting a tile beyond (4,4) [P(X<4) ~ 1.08%]
                else
                {
                    c -= (double)Math.pow(potentialSpawns,4);
                    //53.3% odds of spawning at X = 3
                    if (c < 8000000.0)
                        userBaseY = 3;
                    //40.0% odds of spawning at X = 2
                    else if (c < 14000000.0)
                        userBaseY = 2;
                    //0.067% odds of spawning at X = 1
                    else
                        userBaseY = 1;
                }
                //userX + userY needs to be <= 194
            } while (userBaseX + userBaseY > grid.gridSideLength - 6);
        }
        else //base on botoom
        {
            potentialSpawns = grid.gridSideLength - 6;
            do
            {
                userBaseY = (int)(Math.random()*Math.pow(potentialSpawns, 4)) + 15000000;
                double c = (double)userBaseY;
                if (c < (double)Math.pow(potentialSpawns,4))
                    //selects an xLoc with increasing probability towards (194,194)
                    userBaseY = 1 + (int)(Math.pow(c, 0.25));
                //for selecting a tile beyond (194,194) [P(X>194) ~ 1.08%]
                else
                {
                    c -= (double)Math.pow(potentialSpawns,4);
                    //53.3% odds of spawning at X = 195
                    if (c < 8000000.0)
                        userBaseY = grid.gridSideLength - 5;
                    //40.0% odds of spawning at X = 196
                    else if (c < 14000000.0)
                        userBaseY = grid.gridSideLength - 4;
                    //0.067% odds of spawning at X = 197
                    else
                        userBaseY = grid.gridSideLength - 3;
                }
                //userX + userY needs to be >= 202
            } while (userBaseX + userBaseY < grid.gridSideLength + 2); 
        }
        return userBaseY; //returns y-coordinate of base in the grid
    }
    /**
     * Sets new raw coordinates for the base
     * 
     * @param x New x-coordinate
     * @param y New y-coordinate
     */
    public void setCoords(int x, int y)
    {
        xLoc = x;
        yLoc = y;
    }
    /**
     * Draws the base itself
     * 
     * @param g Contains basic information for rendering images
     * @param d Displays all of the pictures/objects on the screen
     */
    public void draw(Graphics g, Display d) {
        // location of the base on the grid:
        int x = (int) (squareSize * xLoc);
        int y = (int) (squareSize * yLoc);

        // draws the base image:
        g.drawImage(basePic, x, y, d);
    }
}