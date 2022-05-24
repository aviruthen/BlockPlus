import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;

/**
 * Creates different kinds of drones that pursue the user's base and/or the user
 * itself. There are five different kinds of drones: Normal, Tank, Stealth,
 * Block, and Speed. Each drone's droneType value is 0, 1, 2, 3, and 4 
 * respectively.
 * 
 * Useful Methods:
 *          travel(): Determines drone movement for all drones, automatically 
 *                    calls stealthTravel()
 *          taggedBase(): Determines if drone has hit the user's base
 *          blockCollision(): Determines if drone has hit any non-drone blocks
 *          placeCompBlocks(): Places blocks for block-placing drones
 *          randomDroneType(): Chooses random kind of drone
 *          getZone(): Decides if drone is in their own zone
 *          setOffense(): Sets drone to offensive or defensive
 *          draw(): Draws the drone
 *
 * @Avi and Amar Ruthen
 * @12.22.2020
 */
public class Drone
{
    int xLoc, yLoc; //raw coordinate locations of the drone
    int numMoves; //the number of moves the drone takes during it's lifetime
    int squareSize; //stores the size of the grid square
    int health = 1; //number of blocks drone can de with
    int droneType; //0 - Normal, 1 - Tank, 2 - Stealth, 3 - Block, 4 - Speed
    boolean offensiveDrone = setOffense(); //randomly sets if drone is offensive
    boolean immobilize = false;
    BufferedImage dronePic = null; //stores the picture of the drone
    Color droneColor = Color.RED; //for testing, will be a picture
    
    /**
     * Constructor for objects of class Base
     */
    public Drone(int xLoc, int yLoc, int squareSize, int droneType)
    {
        //stores constructor values into class variables
        this.xLoc = xLoc;
        this.yLoc = yLoc;
        this.squareSize = squareSize;
        this.droneType = droneType;
        numMoves = 0;
        //tank drones have 3 times the health of a normal drone
        if (droneType == 1)
            health = 3;
        
        //each type of drone has a different picture    
        switch (droneType)
        {
            case 0: //normal drones
            {
                try
                {
                    dronePic = ImageIO.read(this.getClass().getResourceAsStream(
                    "resources/normalDrone.png")); //name of image in folder
                }
                catch(Exception e){}
                break;
            }
            case 1: //tank drones
            {
                try
                {
                    dronePic = ImageIO.read(this.getClass().getResourceAsStream(
                    "resources/tankDrone.png")); //name of image in folder
                }
                catch(Exception e){}
                break;
            }
            case 2: //stealth drones
            {
                try
                {
                    dronePic = ImageIO.read(this.getClass().getResourceAsStream(
                    "resources/stealthDrone.png")); //name of image in folder
                }
                catch(Exception e){}
                break;
            }
            case 3: //block-placing drones
            {
                try
                {
                    dronePic = ImageIO.read(this.getClass().getResourceAsStream(
                    "resources/blockPlacingDrone.png"));//name of image in folder
                }
                catch(Exception e){}
                break;
            }
            case 4: //speed drones
            {
                try
                {
                    dronePic = ImageIO.read(this.getClass().getResourceAsStream(
                    "resources/speedDrone.png")); //name of image in folder
                    }
                catch(Exception e){}
                break;
            }
        }
    }
    /**
     * Moves the Drone up, down, left, or right
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
     * Assesses the path the drone should take to go to the user's base. Drones
     * will target the user if they are defensive and the user is in their zone.
     * This movement is blind to blocks on the board. This movement also 
     * automatically calls stealthTravel() for stealth drones
     * 
     * @param user Contains all the information about the user
     * @param userBase Contains all the information about the user's base
     * @param grid Contains all the information about the grid
     * @param userBlocks An array with all of the user's blocks
     * @param neutralBlocks An array of all the neutral blocks on the map
     * @param immovableBlocks An array of all the user's immovable blocks if the
     *                        the user has the "immovable blocks" powerup
     */
    public void travel(Character user, Base userBase, Grid grid, 
                       Block[] userBlocks, Block[] neutralBlocks, 
                       Block[] immovableBlocks)
    {
        //stealth drones will stealth travel unless they are close enough to the
        //user's base
        if (droneType == 2 && Math.abs(xLoc - (userBase.xLoc + 1)) > 8 &&  
            Math.abs(yLoc - (userBase.yLoc + 1)) > 8)
                stealthTravel(user, userBase, grid, userBlocks, 
                              neutralBlocks, immovableBlocks);
        
        //drone will move towards user if the user is in their zone and they
        //are a defensive drone, the user is not invisible, and the drone
        //is not immobilized
        else if (!user.getZone(grid, userBase) && getZone(grid, userBase) 
                && !offensiveDrone && !user.invisible && !immobilize)
        {
            //move randomly in x or y direction
            int xOrY = (int)(2*Math.random());
            if (xLoc == user.xLoc)
                xOrY = 1; //the drone does not need to change its x-coordinate
            if (yLoc == user.yLoc)
                xOrY = 0; //the drone does not need to change its y-coordinate
            //moves horizontally
            if (xOrY == 0)
            {
                if (xLoc > user.xLoc)
                {
                    move(2); //move left towards user
                }
                else if (xLoc < user.xLoc)
                    move(3); //move right towards user
            }
            //moves vertically
            if (xOrY == 1)
            {
                if (yLoc > user.yLoc)
                    move(0); //move up towards user
                else if (yLoc < user.yLoc)
                    move(1); //move down towards user;  
            }
        }
        //drone will default to moving towards user's base if not immobilized
        else if (!immobilize)
        {
            //move randomly in x or y direction
            int xOrY = (int)(2*Math.random());
            //userBase.xLoc and userBase.yLoc denote the location of the
            //top left corner of the userBase picture
            if (xLoc == userBase.xLoc + 1)
                xOrY = 1;
            if (yLoc == userBase.yLoc + 1)
                xOrY = 0;
            //moves horizontally
            if (xOrY == 0)
            {
                if (xLoc > userBase.xLoc + 1)
                    move(2); //move left towards base
                else if (xLoc < userBase.xLoc + 1)
                    move(3); //move right towards base
            }
            //moves vertically
            if (xOrY == 1)
            {
                if (yLoc > userBase.yLoc + 1)
                    move(0); //move up towards base
                else if (yLoc < userBase.yLoc + 1)
                    move(1); //move down towards base;  
            }
        }
    }
    /**
     * Assesses if the drone has tagged the user's base
     * 
     * @param userBase Contains all the information about the user's base
     * @param grid Contains all the information about the grid
     * @return true if the drone is within 1 block of the center of the base
     */
    public boolean taggedBase(Base userBase, Grid grid)
    {
        //drone must be within 1 block of the center of the user's base
        if (Math.abs(grid.toGridCoords(userBase.xLoc + 1, true) - 
            grid.toGridCoords(xLoc, true)) <= 1 && 
            Math.abs(grid.toGridCoords(userBase.yLoc + 1, false) - 
            grid.toGridCoords(yLoc, false)) <= 1)
            return true;
        return false;
    }
    /**
     * Determines if a block collision has occured and with which blocks
     * 
     * @param neutralBlocks An array of all the neutral blocks on the map
     * @param userBlocks An array of all the user's blocks
     * @param immovableBlocks An array of all the user's immovable blocks if the
     *                        the user has the "immovable blocks" powerup
     * @return String starting with a "1" for neutral blocks, "2" for the
     *         user's blocks, "3" for immovable blocks, and "0" if no collision
     *         has occured. The String also contains the index of the block
     *         the drone has collided with.
     */
    public String blockCollision(Block[] neutralBlocks, Block[] userBlocks, 
                                 Block[] immovableBlocks)
    {
        int index = 0; //the location of the broken block within the arrays
        //determines if the drone's location is the same as any of the neutral
        //blocks currently on the map.
        for (Block nB : neutralBlocks)
        {
            if (nB != null)
            {
                if (xLoc == nB.xLoc && yLoc == nB.yLoc) //same location
                    return "1 " + index; //deciphered in droneTimer
            }
            index++; //tracks location of the block within the array
        }
        index = 0;
        //same process for userBlocks and immovableBlocks
        for (Block uB : userBlocks)
        {
            if (uB != null)
            {
                if (xLoc == uB.xLoc && yLoc == uB.yLoc)
                    return "2 " + index;
            }
            index++;
        }
        //same process
        for (Block iB : immovableBlocks)
        {
            if (iB != null)
            {
                if (xLoc == iB.xLoc && yLoc == iB.yLoc)
                    return "3";
            }
        }
        return "0"; //no collision
    }
    /**
     * Finds the best route for stealth drones to pursue the user or the user's
     * base while avoiding all non-computer blocks
     * 
     * @param user Contains all the information about the character
     * @param userBase Contains all the information about the user's base
     * @param grid Contains all the information about the grid
     * @param userBlocks An array of all the user's blocks
     * @param neutralBlocks An array of all the neutral blocks on the map
     * @param immovableBlocks An array of all of the user's immovable blocks if
     *                    the user has activated the "immovable blocks" powerup
     */
    public void stealthTravel(Character user, Base userBase, Grid grid,
            Block[] userBlocks, Block[] neutralBlocks, Block[] immovableBlocks)
    {
        boolean[] goodMoves = new boolean[4]; //one for each direction
        for (int i = 0; i < goodMoves.length; i++)
            goodMoves[i] = true; //initializes all possible moves as true
        //goes to i = 3 since there are four directions (up, down, left, right)
        for (int i = 0; i <= 3; i++)
        {
            //checks if there any blocks above the drone (faceDirection = 0)
            if (i == 0)
            {
                //checks if there are any neutral blocks above the drone
                for (Block nB : neutralBlocks)
                {
                    if (nB != null)
                    {
                        if (nB.yLoc == yLoc - 1 && nB.xLoc == xLoc)
                            goodMoves[i] = false; //cannot crash into block
                    }
                }
                //same process with user blocks
                for (Block uB : userBlocks)
                {
                    if (uB != null)
                    {
                        if (uB.yLoc == yLoc - 1 && uB.xLoc == xLoc)
                            goodMoves[i] = false;
                    }
                }
                //same process with immovable blocks
                for (Block iB : immovableBlocks)
                {
                    if (iB != null)
                    {
                        if (iB.yLoc == yLoc - 1 && iB.xLoc == xLoc)
                            goodMoves[i] = false;
                    }
                }
            }
            //checks if there are any blocks below the drone
            else if (i == 1)
            {
                for (Block nB : neutralBlocks)
                {
                    if (nB != null)
                    {
                        if (nB.yLoc == yLoc + 1 && nB.xLoc == xLoc)
                            goodMoves[i] = false;
                    }
                }
                for (Block uB : userBlocks)
                {
                    if (uB != null)
                    {
                        if (uB.yLoc == yLoc + 1 && uB.xLoc == xLoc)
                            goodMoves[i] = false;
                    }
                }
                for (Block iB : immovableBlocks)
                {
                    if (iB != null)
                    {
                        if (iB.yLoc == yLoc + 1 && iB.xLoc == xLoc)
                            goodMoves[i] = false;
                    }
                }
            }
            //checks if there are any blocks to the left of the drone
            else if (i == 2)
            {
                for (Block nB : neutralBlocks)
                {
                    if (nB != null)
                    {
                        if (nB.yLoc == yLoc && nB.xLoc == xLoc - 1)
                            goodMoves[i] = false;
                    }
                }
                for (Block uB : userBlocks)
                {
                    if (uB != null)
                    {
                        if (uB.yLoc == yLoc && uB.xLoc == xLoc - 1)
                            goodMoves[i] = false;
                    }
                }
                for (Block iB : immovableBlocks)
                {
                    if (iB != null)
                    {
                        if (iB.yLoc == yLoc && iB.xLoc == xLoc - 1)
                            goodMoves[i] = false;
                    }
                }
            }
            //checks if there are any blocks to the right of the drone
            else
            {
                for (Block nB : neutralBlocks)
                {
                    if (nB != null)
                    {
                        if (nB.yLoc == yLoc && nB.xLoc == xLoc + 1)
                            goodMoves[i] = false;
                    }
                }
                for (Block uB : userBlocks)
                {
                    if (uB != null)
                    {
                        if (uB.yLoc == yLoc && uB.xLoc == xLoc + 1)
                            goodMoves[i] = false;
                    }
                }
                for (Block iB : immovableBlocks)
                {
                    if (iB != null)
                    {
                        if (iB.yLoc == yLoc && iB.xLoc == xLoc + 1)
                            goodMoves[i] = false;
                    }
                }
            }
        }
        int[] moveOrder = new int[4]; //an array which orders the optimal drone moves
        //decides which x direction to move first
        int whichX = (int)(2*Math.random()) + 2; //decides if left or right
        int whichY = (int)(2*Math.random()); //decides if up or down

        //drone will move towards user if the user is in their drone, the drone 
        //is defensive, the user is not invisibe, and the drone is not 
        //immobilized
        if (!user.getZone(grid, userBase) && getZone(grid, userBase) && 
            !offensiveDrone && !user.invisible && !immobilize)
        {
            //decides if drone moves horizontally or vertically
            int xOrY = (int)(2*Math.random());
            
            //moves horizontally
            if (xOrY == 0)
            {
                //drone is to the right of the user
                if (xLoc > user.xLoc)
                {
                    moveOrder[0] = 2; //move left towards user
                    if (yLoc > user.yLoc)
                    {
                        //prioritize left, up, right, down
                        moveOrder[1] = 0;
                        moveOrder[2] = 3;
                        moveOrder[3] = 1;
                    }
                    else if (yLoc < user.yLoc)
                    {
                        //prioritize left, down, right, up
                        moveOrder[1] = 1;
                        moveOrder[2] = 3;
                        moveOrder[3] = 0;
                    }
                    else if (yLoc == user.yLoc)
                    {
                        //prioritize left, up or down, then right
                        moveOrder[1] = whichY;
                        if (whichY == 0)
                            whichY = 1;
                        else
                            whichY = 0;
                        moveOrder[2] = whichY;
                        moveOrder[3] = 3;
                    }
                }
                //drone is to the left of the user
                else if (xLoc < user.xLoc)
                {
                    moveOrder[0] = 3; //move right towards user
                    if (yLoc > user.yLoc)
                    {
                        //prioritize right, up, left, down
                        moveOrder[1] = 0;
                        moveOrder[2] = 2;
                        moveOrder[3] = 1;
                    }
                    else if (yLoc < user.yLoc)
                    {
                        //prioritize right, down, left, up
                        moveOrder[1] = 1;
                        moveOrder[2] = 2;
                        moveOrder[3] = 0;
                    }
                    else if (yLoc == user.yLoc)
                    {
                        //prioritize right, up or down, then left
                        moveOrder[1] = whichY;
                        if (whichY == 0)
                            whichY = 1;
                        else
                            whichY = 0;
                        moveOrder[2] = whichY;
                        moveOrder[3] = 2;
                    }
                }
                //drone is in-line horizontally with the user
                else if (xLoc == user.xLoc)
                {
                    if (yLoc > user.yLoc)
                    {
                        //move up
                        moveOrder[0] = 0;
                        //move either left or right
                        moveOrder[1] = whichX;
                        if (whichX == 2)
                            whichX = 3;
                        else
                            whichX = 2;
                        moveOrder[2] = whichX;
                        moveOrder[3] = 1; //move down last
                    }
                    else if (yLoc < user.yLoc)
                    {
                        //move down first
                        moveOrder[0] = 1;
                        //move either left or right
                        moveOrder[1] = whichX;
                        if (whichX == 2)
                            whichX = 3;
                        else
                            whichX = 2;
                        moveOrder[2] = whichX;
                        moveOrder[3] = 0; //move up last
                    }
                }
            }
            //moves vertically
            else
            {
                //drone is below the user
                if (yLoc > user.yLoc)
                {
                    moveOrder[0] = 0; //move up towards user
                    if (xLoc > user.xLoc)
                    {
                        //prioritize up, left, down, right
                        moveOrder[1] = 2;
                        moveOrder[2] = 1;
                        moveOrder[3] = 3;
                    }
                    else if (xLoc < user.xLoc)
                    {
                        //prioritize up, right, down, down
                        moveOrder[1] = 3;
                        moveOrder[2] = 1;
                        moveOrder[3] = 2;
                    }
                    else if (xLoc == user.xLoc)
                    {
                        //prioritize up, left or right, then down
                        moveOrder[1] = whichX;
                        if (whichX == 2)
                            whichX = 3;
                        else
                            whichX = 2;
                        moveOrder[2] = whichX;
                        moveOrder[3] = 1;
                    }
                }
                //drone is above the user
                else if (yLoc < user.yLoc)
                {
                    moveOrder[0] = 1; //move down towards user
                    if (xLoc > user.xLoc)
                    {
                        //prioritize down, left, up, right
                        moveOrder[1] = 2;
                        moveOrder[2] = 0;
                        moveOrder[3] = 3;
                    }
                    else if (xLoc < user.xLoc)
                    {
                        //prioritize down, right, up, down
                        moveOrder[1] = 3;
                        moveOrder[2] = 0;
                        moveOrder[3] = 2;
                    }
                    else if (xLoc == user.xLoc)
                    {
                        //prioritize down, left or right, then up
                        moveOrder[1] = whichX;
                        if (whichX == 2)
                            whichX = 3;
                        else
                            whichX = 2;
                        moveOrder[2] = whichX;
                        moveOrder[3] = 0;
                    }
                }
                //drone is in-line vertically with the user
                else if (yLoc == user.yLoc)
                {
                    if (xLoc > user.xLoc)
                    {
                        //move left first
                        moveOrder[0] = 2;
                        //move either up or down
                        moveOrder[1] = whichY;
                        if (whichY == 0)
                            whichY = 1;
                        else
                            whichY = 0;
                        moveOrder[2] = whichY;
                        moveOrder[3] = 3; //move right last
                    }
                    else if (xLoc < user.xLoc)
                    {
                        //move right first
                        moveOrder[0] = 3;
                        //move either up or down
                        moveOrder[1] = whichY;
                        if (whichY == 0)
                            whichY = 1;
                        else
                            whichY = 0;
                        moveOrder[2] = whichY;
                        moveOrder[3] = 2; //move left last
                    }
                }
            }
        }
        //drone will move towards userBase if not immobilized
        else if (!immobilize)
        {
            //the same process as above occurs here, except with userBase
            int xOrY = (int)(2*Math.random());
            //moves horizontally
            if (xOrY == 0)
            {
                if (xLoc > userBase.xLoc + 1)
                {
                    moveOrder[0] = 2; //move left towards base
                    if (yLoc > userBase.yLoc + 1)
                    {
                        //prioritize left, up, right, down
                        moveOrder[1] = 0;
                        moveOrder[2] = 3;
                        moveOrder[3] = 1;
                    }
                    else if (yLoc < userBase.yLoc + 1)
                    {
                        //prioritize left, down, right, up
                        moveOrder[1] = 1;
                        moveOrder[2] = 3;
                        moveOrder[3] = 0;
                    }
                    else if (yLoc == userBase.yLoc + 1)
                    {
                        //prioritize left, right, either up or down
                        moveOrder[1] = 3;
                        moveOrder[2] = whichY;
                        if (whichY == 0)
                            whichY = 1;
                        else
                            whichY = 0;
                        moveOrder[3] = whichY;
                    }
                }
                else if (xLoc < userBase.xLoc + 1)
                {
                    moveOrder[0] = 3; //move right towards base
                    if (yLoc > userBase.yLoc + 1)
                    {
                        //prioritize right, up, left, down
                        moveOrder[1] = 0;
                        moveOrder[2] = 2;
                        moveOrder[3] = 1;
                    }
                    else if (yLoc < userBase.yLoc + 1)
                    {
                        //prioritize right, down, left, up
                        moveOrder[1] = 1;
                        moveOrder[2] = 2;
                        moveOrder[3] = 0;
                    }
                    else if (yLoc == userBase.yLoc + 1)
                    {
                        //prioritize right, left, either up or down
                        moveOrder[1] = 2;
                        moveOrder[2] = whichY;
                        if (whichY == 0)
                            whichY = 1;
                        else
                            whichY = 0;
                        moveOrder[3] = whichY;
                    }
                }
                else if (xLoc == userBase.xLoc + 1)
                {
                    if (yLoc > userBase.yLoc + 1)
                    {
                        //move up first, then down
                        moveOrder[0] = 0;
                        moveOrder[1] = 1;
                    }
                    else if (yLoc < userBase.yLoc + 1)
                    {
                        //move down first, then up
                        moveOrder[0] = 1;
                        moveOrder[1] = 0;
                    }
                    //move either left or right
                    moveOrder[2] = whichX;
                    if (whichX == 2)
                        whichX = 3;
                    else
                        whichX = 2;
                    moveOrder[3] = whichX;
                }
            }
            //moves vertically
            else
            {
                if (yLoc > userBase.yLoc + 1)
                {
                    moveOrder[0] = 0; //move up towards base
                    if (xLoc > userBase.xLoc + 1)
                    {
                        //prioritize up, left, down, right
                        moveOrder[1] = 2;
                        moveOrder[2] = 1;
                        moveOrder[3] = 3;
                    }
                    else if (xLoc < userBase.xLoc + 1)
                    {
                        //prioritize up, right, down, down
                        moveOrder[1] = 3;
                        moveOrder[2] = 1;
                        moveOrder[3] = 2;
                    }
                    else if (xLoc == userBase.xLoc + 1)
                    {
                        //prioritize up, down, either left or right
                        moveOrder[1] = 1;
                        moveOrder[2] = whichX;
                        if (whichX == 2)
                            whichX = 3;
                        else
                            whichX = 2;
                        moveOrder[3] = whichX;
                    }
                }
                else if (yLoc < userBase.yLoc + 1)
                {
                    moveOrder[0] = 1; //move down towards base
                    if (xLoc > userBase.xLoc + 1)
                    {
                        //prioritize down, left, up, right
                        moveOrder[1] = 2;
                        moveOrder[2] = 0;
                        moveOrder[3] = 3;
                    }
                    else if (xLoc < userBase.xLoc + 1)
                    {
                        //prioritize down, right, up, down
                        moveOrder[1] = 3;
                        moveOrder[2] = 0;
                        moveOrder[3] = 2;
                    }
                    else if (xLoc == userBase.xLoc + 1)
                    {
                        //prioritize down, up, either left or right
                        moveOrder[1] = 0;
                        moveOrder[2] = whichX;
                        if (whichX == 2)
                            whichX = 3;
                        else
                            whichX = 2;
                        moveOrder[3] = whichX;
                    }
                }
                else if (yLoc == userBase.yLoc + 1)
                {
                    if (xLoc > userBase.xLoc + 1)
                    {
                        //move left first, then right
                        moveOrder[0] = 2;
                        moveOrder[1] = 3;
                    }
                    else if (xLoc < userBase.xLoc + 1)
                    {
                        //move right first, then left
                        moveOrder[0] = 3;
                        moveOrder[1] = 2;
                    }
                    //move either up or down
                    moveOrder[2] = whichY;
                    if (whichY == 0)
                        whichY = 1;
                    else
                        whichY = 0;
                    moveOrder[3] = whichY;
                }
            }
        }
        boolean foundBestMove = false; //ensures the drone moves just once
        //drone moves in best direction if possible
        for (int i = 0; i <= 3; i++)
        {
            if (!foundBestMove)
            {
                //returns true if you can move in the moveOrder direciton
                if (goodMoves[moveOrder[i]])
                {
                    foundBestMove = true; //ensures drone moves just once
                    move(moveOrder[i]);
                }
            }
        }
    }
    /**
     * Places computer blocks adjacent to block-placing drones in open areas
     * 
     * @param userBlocks An array of all the user's blocks
     * @param immovableBlocks An array of all the user's immovable blocks
     * @param neutralBlocks An array of all the neutral blocks on the m ap
     * @param compBlocks An array of all of the computer's blocks (drone)
     * @param userBase Contains all the information about the user's base
     * @param compBase Contains all the information about the computer's base
     * @param grid Contains all the information about the grid
     */
    public void placeCompBlock(Block[] userBlocks, Block[] immovableBlocks, 
                               Block[] neutralBlocks, Block[] compBlocks, 
                               Base userBase, Base compBase, Grid grid)
    {
        //only four possible block placements: Above, below, left, or right
        boolean[] goodBlockPlaces = new boolean[4];
        //temporary character to access character methods
        Character t = new Character(0, 0, squareSize, grid);
        for (int i = 0; i < goodBlockPlaces.length; i++)
        {
            //accesses a method in character that accomplishes a similar task
            goodBlockPlaces[i] = t.canPlaceBlock(userBlocks, immovableBlocks, 
                                compBlocks, neutralBlocks, userBase, compBase, 
                                false, i, xLoc, yLoc);
        }
        boolean openPlacement = false;
        //checks through the four different directions to see if the drone can
        //place blocks there
        for (int i = 0; i < goodBlockPlaces.length; i++)
        {
            if (goodBlockPlaces[i])
                openPlacement = true;
        }
        
        //block-placement
        if (openPlacement)
        {
            int placeDirection;
            //chooses a random feasible direction where blocks can go
            do{
            placeDirection = (int)(4*Math.random());
            } while (!goodBlockPlaces[placeDirection]);
            
            int bIndex = 0;
            //finds the first available opening in the computer blocks array
            for (Block cB: compBlocks)
            {
                if (cB != null)
                    bIndex++;
            }
            //places block above the drone
            if (placeDirection == 0)
                compBlocks[bIndex] = new Block(xLoc, yLoc - 1, squareSize, 
                                                false, Color.YELLOW, false);
            //places block below the drone
            else if (placeDirection == 1)
                compBlocks[bIndex] = new Block(xLoc, yLoc + 1, squareSize, 
                                                false, Color.YELLOW, false);
            //places block to the left of the drone
            else if (placeDirection == 2)
                compBlocks[bIndex] = new Block(xLoc - 1, yLoc, squareSize, 
                                                false, Color.YELLOW, false);
            //places block to the right of the drone
            else if (placeDirection == 3)
                compBlocks[bIndex] = new Block(xLoc + 1, yLoc, squareSize, 
                                                false, Color.YELLOW, false);
        }
    }
    /**
     * Selects a random drone type. Normal Drone = 30% chance, Tank Drone = 20%
     * chance, Stealth Drone = 10% chance, Block-Placing Drone = 20% chance,
     * Speed Drone = 20% chance.
     * 
     * @return integer with the kind of drone (0-Normal, 1-Tank, 2-Stealth, 
     * 3-Block-Placing, 4-Speed)
     */
    public int randomDroneType()
    {
        int selectDrone = (int)(Math.random()*100);
        if (selectDrone < 30)
            return 0; //normal drone (30%)
        else if (selectDrone < 50)
            return 1; //tank drone (20%)
        else if (selectDrone < 60)
            return 2; //stealth drone (10%)
        else if (selectDrone < 80)
            return 3; //block-placing drone (20%)
        else
            return 4; //speed drone (20%)
    }
    /**
     * Determines the zone the drone is currently in
     * 
     * @param grid Contais all the information about the grid
     * @param userBase Contains all the information about the user's base
     * @return true if the drone is within its own zone
     */
    public boolean getZone(Grid grid, Base userBase)
    {
        //drone is not in the same zone as the userBase
        if ((grid.toGridCoords(xLoc, true) + grid.toGridCoords(yLoc, false) < 
            grid.gridSideLength - 2 && !userBase.userOnTop) || 
            (grid.toGridCoords(xLoc, true) + grid.toGridCoords(yLoc, false) > 
            grid.gridSideLength - 1 && userBase.userOnTop))
            return true;
        return false;
    } 
    /**
     * Randomly selects if the drone is offensive or not (50% chance), meaning
     * that the drone will strictly pursue the user's base
     * 
     * @return true if the drone is offensive 
     */
    public boolean setOffense()
    {
        int offense = (int)(2*Math.random()); //0 or 1, simulates coin flip
        if (offense == 1)
            return true;
        return false;
    }
    /**
     * Draws the drone picture itself
     * 
     * @param g Contains basic information for rendering images
     * @param d Displays all of the pictures/objects on the screen
     */
    public void draw(Graphics g, Display d)
    {
        //location of the drone adjusted for the grid size:
        int x =  (int)(squareSize * xLoc);  
        int y =  (int)(squareSize * yLoc);


        g.drawImage(dronePic, x, y, d); //draws the drone picture
    }
}