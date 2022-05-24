import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.awt.geom.AffineTransform;
import java.awt.event.KeyEvent; 
import java.awt.event.KeyListener;
/**
 * The code for the user (does not include the move code)
 * 
 * Useful Methods: 
 *                 setImagePosition: sets user picture for animation
 *                 draw: draws user on graphical interface
 *                 canPlaceBlock: if user can place block in desired position 
 *                 placeBlock: places block on graphical interface
 *                 breakBlock: gets index of block user is interacting with
 *                 canMove: checks if user/drone can travel in desired direction
 *                 teleportMove: allows for teleportation travel
 *                 replenishBlocks: restores # of blocks
 *                 increaseBlocks: allows user to carry extra blocks 
 *                                 before replenishing
 *                 getZone: checks if user is in friendly territory
 * 
 * @Avi and Amar Ruthen
 * @12.22.2020
 */
public class Character
{
    int xLoc, yLoc;
    int squareSize;
    int faceDirection;
    
    int numBlocks, blocksAllowed, numImmovableBlocks;
    
    boolean invisible;
    
    double theta = 0.0;
    
    BufferedImage charPic = null;
    Grid grid;

    /**
     * Constructor the user object
     * Initializes coordinates and block variables, transfers grid object/values 
     */
    public Character(int xLoc, int yLoc, int squareSize, Grid grid)
    {
        this.xLoc = xLoc;
        this.yLoc = yLoc;
        this.squareSize = squareSize;
        this.grid = grid;
        faceDirection = 0;          //character faces up
        blocksAllowed = 100;        //Sets total blocks w/o replenishing
        numBlocks = blocksAllowed;  
        numImmovableBlocks = 10;    //Needed if immovable blocks is selected
        invisible = false;
        
        try
        {
            charPic = ImageIO.read(this.getClass().getResourceAsStream(
            "resources/AquaTriangle.png")); //the name of the image
        }
        catch(Exception e){}
    }
    
    /**
     * Toggles between four pictures of the same user graphics to give
     * illusion of movement
     */
    public void setImagePosition(int num)
    {
        switch(faceDirection)
        {
            case 0:
                //Facing up, or starting position
                try {
                    charPic = ImageIO.read(this.getClass().getResourceAsStream(
                    "resources/AquaTriangle.png")); //name of the image,
                                                                                         
                } 
                catch (Exception e) {}
                break;
            case 1:
                //Facing down
                try {
                    charPic = ImageIO.read(this.getClass().getResourceAsStream(
                    "resources/AquaTriangle 2.png")); //name of the image
                                                                                         
                } 
                catch (Exception e) {}
                break;
            case 2:
                //Facing left
                try {
                    charPic = ImageIO.read(this.getClass().getResourceAsStream(
                    "resources/AquaTriangle 3.png")); //name of the image
                                                                                         
                } 
                catch (Exception e) {}
                break;
            case 3:
                //Facing right
                try {
                    charPic = ImageIO.read(this.getClass().getResourceAsStream(
                    "resources/AquaTriangle 4.png")); //name of the image
                                                                                         
                } 
                catch (Exception e) {}
                break;
            default:
                break;
        }
    }
    
    /**
     * Draws the location of the user at given location
     * @param g allows for character to be drawn on graphics window
     * @param d gives the display access to the user
     */
    public void draw(Graphics g, Display d)
    {
        //location of this Character on grid:
        int x =  (int)(squareSize * xLoc);  
        int y =  (int)(squareSize * yLoc);

        //draw the image
        g.drawImage(charPic, x, y, d);
    }
    
    /**
     * Cycles through all given scenarios of whether the user can place a block:
     * Cannot place if...
     *      Block occupies the space in front of direction user is facing
     *      Block placed would be outside the border
     *      Block placed is within 5x5 near either base spawn
     *      Number of blocks of that specific type is zero
     *      
     * @param userBlocks, immovableBlocks, compBlocks, neutralBlocks sends all 
     *        block types
     * @param userBase, compBase sends the current updated base objects
     * @param manualFD tracks which direction user is facing
     * @param immovable checks whether the block in question is immovable
     */
    public boolean canPlaceBlock(Block[] userBlocks, Block[] immovableBlocks, 
             Block[] compBlocks, Block[] neutralBlocks, Base userBase, 
             Base compBase, boolean immovable, int manualFD, int xLoc, int yLoc)
    {
        //manual Face Direction is to check if you can place a block for any 
        //adjacent location of your choice (for drones, specifically)
        if (manualFD == -1)
            manualFD = faceDirection;
        //sets test xLocs and yLocs to character's xLoc and yLoc if method is
        //not called by a drone
        if (xLoc == -1)
            xLoc = this.xLoc;
        if (yLoc == -1)
            yLoc = this.yLoc;   
        //if the user has no blocks to place, they cannot place blocks
        if ((numBlocks == 0) && (!immovable) || 
            (numImmovableBlocks == 0) && (immovable))
            return false;
        //user facing up
        if (manualFD == 0)
        {
            //if the 5X5 base areas are above the user, cannot place blocks
            if (Math.abs(userBase.yLoc + 1 - (yLoc - 1)) <= 2 && 
                Math.abs(userBase.xLoc + 1 - xLoc) <= 2)
                return false;
            if (Math.abs(compBase.yLoc + 1 - (yLoc - 1)) <= 2 && 
                Math.abs(compBase.xLoc + 1 - xLoc) <= 2)
                return false;
            //cannot place blocks beyond border edge
            if (grid.toGridCoords(yLoc, false) == 0)
                return false;
            //cannot place blocks on top of other user blocks
            for (Block uB : userBlocks)
            {
                if (uB != null)
                {
                    if (uB.yLoc == yLoc - 1 && uB.xLoc == xLoc)
                        return false;
                }
            }
            //cannot place blocks on top of other immovable blocks
            for (Block iB : immovableBlocks)
            {
                if (iB != null)
                {
                    if (iB.yLoc == yLoc + 1 && iB.xLoc == xLoc)
                        return false;
                }
            }
            //cannot place blocks on top of computer blocks
            for (Block cB : compBlocks)
            {
                if (cB != null)
                {
                    if (cB.yLoc == yLoc - 1 && cB.xLoc == xLoc)
                        return false;
                }
            }
            //cannot place blocks on top of neutral blocks
            for (Block nB : neutralBlocks)
            {
                if (nB != null)
                {
                    if (nB.yLoc == yLoc - 1 && nB.xLoc == xLoc)
                        return false;
                }
            }
        }
        //user is facing down
        else if (manualFD == 1)
        {
            //cannot place blocks within 5X5 base area
            if (Math.abs(userBase.yLoc + 1 - (yLoc + 1)) <= 2 && 
                Math.abs(userBase.xLoc + 1 - xLoc) <= 2)
                return false;
            if (Math.abs(compBase.yLoc + 1 - (yLoc + 1)) <= 2 && 
                Math.abs(compBase.xLoc + 1 - xLoc) <= 2)
                return false;
            //cannot place blocks beyond border edge
            if(grid.toGridCoords(yLoc, false) == grid.gridSideLength - 1)
                return false;
            //cannot place blocks on top of other user blocks
            for (Block uB : userBlocks)
            {
                if (uB != null)
                {
                    if (uB.yLoc == yLoc + 1 && uB.xLoc == xLoc)
                        return false;
                }
            }
            //cannot place blocks on top of other immovable blocks
            for (Block iB : immovableBlocks)
            {
                if (iB != null)
                {
                    if (iB.yLoc == yLoc + 1 && iB.xLoc == xLoc)
                        return false;
                }
            }
            //cannot place blocks on top of other computer blocks
            for (Block cB : compBlocks)
            {
                if (cB != null)
                {
                    if (cB.yLoc == yLoc + 1 && cB.xLoc == xLoc)
                        return false;
                }
            }
            //cannot place blocks on top of other neutral blocks
            for (Block nB : neutralBlocks)
            {
                if (nB != null)
                {
                    if (nB.yLoc == yLoc + 1 && nB.xLoc == xLoc)
                        return false;
                }
            }
        }
        //user facing left
        else if (manualFD == 2)
        {
            //cannot place blocks within 5X5 base area
            if (Math.abs(userBase.xLoc + 1 - (xLoc - 1)) <= 2 && 
                Math.abs(userBase.yLoc + 1 - yLoc) <= 2)
                return false;
            if (Math.abs(compBase.xLoc + 1 - (xLoc - 1)) <= 2 && 
                Math.abs(compBase.yLoc + 1 - yLoc) <= 2)
                return false;
            //cannot place blocks beyond the edge of the border
            if(grid.toGridCoords(xLoc, true) == 0)
                return false;
            //cannot place blocks on top of other user blocks
            for (Block uB : userBlocks)
            {
                if (uB != null)
                {
                    if (uB.xLoc == xLoc - 1 && uB.yLoc == yLoc)
                        return false;
                }
            }
            //cannot place blocks on top of other immovable blocks
            for (Block iB : immovableBlocks)
            {
                if (iB != null)
                {
                    if (iB.xLoc == xLoc - 1 && iB.yLoc == yLoc)
                        return false;
                }
            }
            //cannot place blocks on top of other computer blocks
            for (Block cB : compBlocks)
            {
                if (cB != null)
                {
                    if (cB.xLoc == xLoc - 1 && cB.yLoc == yLoc)
                        return false;
                }
            }
            //cannot place blocks on top of other neutral blocks
            for (Block nB : neutralBlocks)
            {
                if (nB != null)
                {
                    if (nB.xLoc == xLoc - 1 && nB.yLoc == yLoc)
                        return false;
                }
            }
        }
        //user facing right
        else if (manualFD == 3)
        {
            //cannot place blocks within 5X5 base spawning area
            if (Math.abs(userBase.xLoc + 1 - (xLoc + 1)) <= 2 && 
                Math.abs(userBase.yLoc + 1 - yLoc) <= 2)
                return false;
            if (Math.abs(compBase.xLoc + 1 - (xLoc + 1)) <= 2 && 
                Math.abs(compBase.yLoc + 1 - yLoc) <= 2)
                return false;
            //cannot place blocks beyond the edge of the border
            if(grid.toGridCoords(xLoc, true) == grid.gridSideLength - 1)
                return false;
            //cannot place blocks on top of other user blocks
            for (Block uB : userBlocks)
            {
                if (uB != null)
                {
                    if (uB.xLoc == xLoc + 1 && uB.yLoc == yLoc)
                        return false;
                }
            }
            //cannot place blocks on top of other immovable blocks
            for (Block iB : immovableBlocks)
            {
                if (iB != null)
                {
                    if (iB.xLoc == xLoc + 1 && iB.yLoc == yLoc)
                        return false;
                }
            }
            //cannot place blocks on top of other computer blocks
            for (Block cB : compBlocks)
            {
                if (cB != null)
                {
                    if (cB.xLoc == xLoc + 1 && cB.yLoc == yLoc)
                        return false;
                }
            }
            //cannot place blocks on top of other neutral blocks
            for (Block nB : neutralBlocks)
            {
                if (nB != null)
                {
                    if (nB.xLoc == xLoc + 1 && nB.yLoc == yLoc)
                        return false;
                }
            }
        }
        return true; //if none of the following conditions occur
    }
    /**
     * Places block in location specified by user
     * @param blockType is which array of blocks will be placed by the user
     * @param blockIndex the index of last block placed in specified blockType
     * @param userColor the color of the block (red-normal, gray-immovable)
     * @param immovable denotes whether block can be broken
     * 
     * @return new last blockIndex
     */
    public int placeBlock(Block[] blockType, int blockIndex, Color userColor, 
                          boolean immovable)
    {
        //user facing up
        if (faceDirection == 0)
        {
            //places block above user
            blockType[blockIndex] = new Block(xLoc, yLoc - 1, squareSize, 
                                              true, userColor, immovable);
        }
        //user facing down
        else if (faceDirection == 1)
        {
            //places block below user
            blockType[blockIndex] = new Block(xLoc, yLoc + 1, squareSize, 
                                              true, userColor, immovable);
        } 
        //user facing left 
        else if (faceDirection == 2)
        {
            //places block to the left of the user
            blockType[blockIndex] = new Block(xLoc - 1, yLoc, squareSize, 
                                              true, userColor, immovable);
        }  
        //user facing right
        else if (faceDirection == 3)
        {
            //places block to the right of the user
            blockType[blockIndex] = new Block(xLoc + 1, yLoc, squareSize, 
                                              true, userColor, immovable);
        }    
        if(!immovable)
            numBlocks--; // immovable blocks stored in separate array
        else
            numImmovableBlocks--;
        blockIndex++; //blockIndex tracks the next available space in userBlocks
        return blockIndex;
    }
    /**
     * Gives index and blockType of the block to be broken
     * 
     * @param compBlocks is the computer blocks array
     * @param neutralBlocks is the neutral blocks array
     * 
     * @return a number that stores index of block and type of block
     */
    public int breakBlock(Block[] compBlocks, Block[] neutralBlocks)
    {
        int index = 0; //tracks index of block broken
        for (Block cB : compBlocks)
        {
            //cannot break immovable blocks
            if (cB != null && !cB.immovable)
            {
                //user facing up
                if (faceDirection == 0)
                {
                    //is there a block above
                    if (grid.toGridCoords(cB.yLoc, false) - 
                        grid.toGridCoords(yLoc, false) == -1 && 
                        grid.toGridCoords(cB.xLoc, true) == 
                        grid.toGridCoords(xLoc, true))
                        return 1009*(index + 1); //1009 indicates computer block
                }
                //user facing down
                else if (faceDirection == 1)
                {
                    //is there a block below
                    if (grid.toGridCoords(cB.yLoc, false) - 
                        grid.toGridCoords(yLoc, false) == 1 && 
                        grid.toGridCoords(cB.xLoc, true) == 
                        grid.toGridCoords(xLoc, true))
                        return 1009*(index + 1);
                }
                //user facing left
                if (faceDirection == 2)
                {
                    //is there a block to the left
                    if (grid.toGridCoords(cB.xLoc, true) - 
                        grid.toGridCoords(xLoc, true) == -1 && 
                        grid.toGridCoords(cB.yLoc, false) == 
                        grid.toGridCoords(yLoc, false))
                        return 1009*(index + 1);
                }
                //user facing right
                else if (faceDirection == 3)
                {
                    //is there a block to the right
                    if (grid.toGridCoords(cB.xLoc, true) - 
                        grid.toGridCoords(xLoc, true) == 1 && 
                        grid.toGridCoords(cB.yLoc, false) == 
                        grid.toGridCoords(yLoc, false))
                        return 1009*(index + 1);
                }
            }
            index++; //otherwise increases index
        }
        index = 0; //initializes index
        for (Block nB : neutralBlocks)
        {
            //goes through same process
            if (nB != null && !nB.immovable)
            {
                //user facing up
                if (faceDirection == 0)
                {
                    //is there a block above
                    if (grid.toGridCoords(nB.yLoc, false) - 
                        grid.toGridCoords(yLoc, false) == -1 && 
                        grid.toGridCoords(nB.xLoc, true) == 
                        grid.toGridCoords(xLoc, true))
                        return 1013*(index + 1); //1009 indicates computer block
                }
                //user facing down
                else if (faceDirection == 1)
                {
                    //is there a block below
                    if (grid.toGridCoords(nB.yLoc, false) - 
                        grid.toGridCoords(yLoc, false) == 1 && 
                        grid.toGridCoords(nB.xLoc, true) == 
                        grid.toGridCoords(xLoc, true))
                        return 1013*(index + 1);
                }
                //user facing left
                if (faceDirection == 2)
                {
                    //is there a block to the left
                    if (grid.toGridCoords(nB.xLoc, true) - 
                        grid.toGridCoords(xLoc, true) == -1 && 
                        grid.toGridCoords(nB.yLoc, false) == 
                        grid.toGridCoords(yLoc, false))
                        return 1013*(index + 1);
                }
                //user facing right
                else if (faceDirection == 3)
                {
                    //is there a block to the right
                    if (grid.toGridCoords(nB.xLoc, true) - 
                        grid.toGridCoords(xLoc, true) == 1 && 
                        grid.toGridCoords(nB.yLoc, false) == 
                        grid.toGridCoords(yLoc, false))
                        return 1013*(index + 1);
                }
            }
            index++; //checks each block in array
        }
        return -1; //block not found
    }
     /**
     * Tests if user can move in specified direction
     * Returns false if a non-user block occupies the place they want to go
     * Ignores if the movement is teleportation, which can go through blocks
     * 
     * @param compBlock, neutralBlocks, immovableBlocks are all block arrays
     * @param teleport checks if user is teleport-moving or moving normally
     * @param manualFD is a manually inputted faceDirection (to check if
     * blocks are in any place surrounding user or drone, not just in front)
     */
    public boolean canMove(Block[] compBlocks, Block[] neutralBlocks, 
                   Block[] immovableBlocks, boolean teleport, int manualFD)
    {
        //checks if user can move in any direction (manual input face direction)
        if (manualFD == -1)
            manualFD = faceDirection; //if no manual input, faceDirection is 
                                      //the user's face direction
        //cehcks if user is on the edge of the map
        switch (manualFD)
        {
            //checks above
            case 0:
            {
                if (grid.toGridCoords(yLoc, false) == 0)
                    return false;
                break;
            }
            //checks below
            case 1:
            {
                if (grid.toGridCoords(yLoc, false) == grid.gridSideLength - 1)
                    return false;
                break;
            }
            //checks to the left
            case 2:
            {
                if (grid.toGridCoords(xLoc, true) == 0)
                    return false;
                break;
            }
            //checks to the right
            case 3:
            {
                if (grid.toGridCoords(xLoc, true) == grid.gridSideLength - 1)
                    return false;
                break;
            }
        }
        //checks if the user is colliding with any non-user blocks
        if (!teleport)
        {
            for (Block cB : compBlocks)
            {
                if (cB != null)
                {
                    //checks above
                    if (manualFD == 0) //block above
                    {
                        if (grid.toGridCoords(cB.yLoc, false) - 
                            grid.toGridCoords(yLoc, false) == -1 && 
                            grid.toGridCoords(cB.xLoc, true) == 
                            grid.toGridCoords(xLoc, true))
                            return false;
                    }
                    else if (manualFD == 1) //block below
                    {
                        if (grid.toGridCoords(cB.yLoc, false) - 
                            grid.toGridCoords(yLoc, false) == 1 && 
                            grid.toGridCoords(cB.xLoc, true) == 
                            grid.toGridCoords(xLoc, true))
                            return false;
                    }
                    else if (manualFD == 2) //block to the left
                    {
                        if (grid.toGridCoords(cB.xLoc, true) - 
                            grid.toGridCoords(xLoc, true) == -1 && 
                            grid.toGridCoords(cB.yLoc, false) == 
                            grid.toGridCoords(yLoc, false))
                            return false;
                    }
                    else if (manualFD == 3) //block to the right
                    {
                        if (grid.toGridCoords(cB.xLoc, true) - 
                            grid.toGridCoords(xLoc, true) == 1 && 
                            grid.toGridCoords(cB.yLoc, false) == 
                            grid.toGridCoords(yLoc, false))
                            return false;
                    }
                }
            }
            //same process for neutral blocks
            for (Block nB : neutralBlocks)
            {
                if (nB != null)
                {
                    //checks above
                    if (manualFD == 0) //block above
                    {
                        if (grid.toGridCoords(nB.yLoc, false) - 
                            grid.toGridCoords(yLoc, false) == -1 && 
                            grid.toGridCoords(nB.xLoc, true) == 
                            grid.toGridCoords(xLoc, true))
                            return false;
                    }
                    else if (manualFD == 1) //block below
                    {
                        if (grid.toGridCoords(nB.yLoc, false) - 
                            grid.toGridCoords(yLoc, false) == 1 && 
                            grid.toGridCoords(nB.xLoc, true) == 
                            grid.toGridCoords(xLoc, true))
                            return false;
                    }
                    else if (manualFD == 2) //block to the left
                    {
                        if (grid.toGridCoords(nB.xLoc, true) - 
                            grid.toGridCoords(xLoc, true) == -1 && 
                            grid.toGridCoords(nB.yLoc, false) == 
                            grid.toGridCoords(yLoc, false))
                            return false;
                    }
                    else if (manualFD == 3) //block to the right
                    {
                        if (grid.toGridCoords(nB.xLoc, true) - 
                            grid.toGridCoords(xLoc, true) == 1 && 
                            grid.toGridCoords(nB.yLoc, false) == 
                            grid.toGridCoords(yLoc, false))
                            return false;
                    }
                }
            }
            //same process for immovable blocks
            for (Block iB : immovableBlocks)
            {
                if (iB != null)
                {
                    //checks above
                    if (manualFD == 0) //block above
                    {
                        if (grid.toGridCoords(iB.yLoc, false) - 
                            grid.toGridCoords(yLoc, false) == -1 && 
                            grid.toGridCoords(iB.xLoc, true) == 
                            grid.toGridCoords(xLoc, true))
                            return false;
                    }
                    else if (manualFD == 1) //block below
                    {
                        if (grid.toGridCoords(iB.yLoc, false) - 
                            grid.toGridCoords(yLoc, false) == 1 && 
                            grid.toGridCoords(iB.xLoc, true) == 
                            grid.toGridCoords(xLoc, true))
                            return false;
                    }
                    else if (manualFD == 2) //block to the left
                    {
                        if (grid.toGridCoords(iB.xLoc, true) - 
                            grid.toGridCoords(xLoc, true) == -1 && 
                            grid.toGridCoords(iB.yLoc, false) == 
                            grid.toGridCoords(yLoc, false))
                            return false;
                    }
                    else if (manualFD == 3) //block to the right
                    {
                        if (grid.toGridCoords(iB.xLoc, true) - 
                            grid.toGridCoords(xLoc, true) == 1 && 
                            grid.toGridCoords(iB.yLoc, false) == 
                            grid.toGridCoords(yLoc, false))
                            return false;
                    }
                }
            }
        }
        return true; //otherwise, user can move
    }
    /**
     * The code for teleportation travel (moves 5 blocks in faceDirection)
     * User may pass through blocks unless it ends on top of a block, in which
     * It travels less than 5 blocks until it reaches a space 
     * not occupied by a non-user block
     * 
     * @param compBlock, neutralBlocks, immovableBlocks are all block arrays
     * 
     * @return how many blocks the user can teleport
     */
    public int teleportMove(Block[] compBlocks, 
                            Block[] neutralBlocks, 
                            Block[] immovableBlocks)
    {
        //to check if there are comp blocks 1-5 blocks ahead of faceDirection 
        boolean[] pos = new boolean[5];

        //checks through all of the computer blocks
        for (Block cB : compBlocks)
        {
            if (cB != null)
            {
                //all blocks in same column as user (facing up)
                if (faceDirection == 0 && grid.toGridCoords(cB.xLoc, true) == 
                                          grid.toGridCoords(xLoc, true))
                {
                    // i = 5 since max of five moves when teleporting
                    for (int i = 1; i <= 5; i++)
                    {
                        //assesses if teleportation will hit the wall
                        if (grid.toGridCoords(yLoc, false) - i < 0)
                            pos[i - 1] = true;
                        if (grid.toGridCoords(cB.yLoc, false) - 
                            grid.toGridCoords(yLoc, false) == -i)
                            pos[i - 1] = true;
                    }
                }
                //all blocks in same column as user (facing down)
                else if (faceDirection == 1 && 
                         grid.toGridCoords(cB.xLoc, true) == 
                         grid.toGridCoords(xLoc, true))
                {
                    for (int i = 1; i <= 5; i++)
                    {
                        //assesses if teleportation will hit the wall
                        if (grid.toGridCoords(yLoc, false) + i > 
                            grid.gridSideLength - 1)
                            pos[i - 1] = true;
                        if (grid.toGridCoords(cB.yLoc, false) - 
                            grid.toGridCoords(yLoc, false) == i)
                            pos[i - 1] = true;
                    }
                }
                //all blocks in same row as user (facing left)
                else if (faceDirection == 2 && 
                        grid.toGridCoords(cB.yLoc, false) == 
                        grid.toGridCoords(yLoc, false))
                {
                    for (int i = 1; i <= 5; i++)
                    {
                        //assesses if teleportation will hit the wall
                        if (grid.toGridCoords(xLoc, true) - i < 0)
                            pos[i - 1] = true;
                        if (grid.toGridCoords(cB.xLoc, false) - 
                            grid.toGridCoords(xLoc, false) == -i)
                            pos[i - 1] = true;
                    }
                }
                //all blocks in same row as user (facing right)
                else if (faceDirection == 3 && 
                        grid.toGridCoords(cB.yLoc, false) == 
                        grid.toGridCoords(yLoc, false))
                {
                    //assesses if teleportation will hit the wall
                    for (int i = 1; i <= 5; i++)
                    {
                        if (grid.toGridCoords(xLoc, true) + i > 
                            grid.gridSideLength - 1)
                            pos[i - 1] = true;
                        if (grid.toGridCoords(cB.xLoc, false) - 
                            grid.toGridCoords(xLoc, false) == i)
                            pos[i - 1] = true;
                    }
                }
            }
        }
        //same process for neutral blocks
        for (Block nB : neutralBlocks)
        {
            if (nB != null)
            {
                //all blocks in same column as user (facing up)
                if (faceDirection == 0 && grid.toGridCoords(nB.xLoc, true) == 
                                          grid.toGridCoords(xLoc, true))
                {
                    // i = 5 since max of five moves when teleporting
                    for (int i = 1; i <= 5; i++)
                    {
                        //assesses if teleportation will hit the wall
                        if (grid.toGridCoords(yLoc, false) - i < 0)
                            pos[i - 1] = true;
                        if (grid.toGridCoords(nB.yLoc, false) - 
                            grid.toGridCoords(yLoc, false) == -i)
                            pos[i - 1] = true;
                    }
                }
                //all blocks in same column as user (facing down)
                else if (faceDirection == 1 && 
                         grid.toGridCoords(nB.xLoc, true) == 
                         grid.toGridCoords(xLoc, true))
                {
                    for (int i = 1; i <= 5; i++)
                    {
                        //assesses if teleportation will hit the wall
                        if (grid.toGridCoords(yLoc, false) + i > 
                            grid.gridSideLength - 1)
                            pos[i - 1] = true;
                        if (grid.toGridCoords(nB.yLoc, false) - 
                            grid.toGridCoords(yLoc, false) == i)
                            pos[i - 1] = true;
                    }
                }
                //all blocks in same row as user (facing left)
                else if (faceDirection == 2 && 
                        grid.toGridCoords(nB.yLoc, false) == 
                        grid.toGridCoords(yLoc, false))
                {
                    for (int i = 1; i <= 5; i++)
                    {
                        //assesses if teleportation will hit the wall
                        if (grid.toGridCoords(xLoc, true) - i < 0)
                            pos[i - 1] = true;
                        if (grid.toGridCoords(nB.xLoc, false) - 
                            grid.toGridCoords(xLoc, false) == -i)
                            pos[i - 1] = true;
                    }
                }
                //all blocks in same row as user (facing right)
                else if (faceDirection == 3 && 
                        grid.toGridCoords(nB.yLoc, false) == 
                        grid.toGridCoords(yLoc, false))
                {
                    //assesses if teleportation will hit the wall
                    for (int i = 1; i <= 5; i++)
                    {
                        if (grid.toGridCoords(xLoc, true) + i > 
                            grid.gridSideLength - 1)
                            pos[i - 1] = true;
                        if (grid.toGridCoords(nB.xLoc, false) - 
                            grid.toGridCoords(xLoc, false) == i)
                            pos[i - 1] = true;
                    }
                }
            }
        }
        //same process for immovable blocks
        for (Block iB : immovableBlocks)
        {
            if (iB != null)
            {
                //all blocks in same column as user (facing up)
                if (faceDirection == 0 && grid.toGridCoords(iB.xLoc, true) == 
                                          grid.toGridCoords(xLoc, true))
                {
                    // i = 5 since max of five moves when teleporting
                    for (int i = 1; i <= 5; i++)
                    {
                        //assesses if teleportation will hit the wall
                        if (grid.toGridCoords(yLoc, false) - i < 0)
                            pos[i - 1] = true;
                        if (grid.toGridCoords(iB.yLoc, false) - 
                            grid.toGridCoords(yLoc, false) == -i)
                            pos[i - 1] = true;
                    }
                }
                //all blocks in same column as user (facing down)
                else if (faceDirection == 1 && 
                         grid.toGridCoords(iB.xLoc, true) == 
                         grid.toGridCoords(xLoc, true))
                {
                    for (int i = 1; i <= 5; i++)
                    {
                        //assesses if teleportation will hit the wall
                        if (grid.toGridCoords(yLoc, false) + i > 
                            grid.gridSideLength - 1)
                            pos[i - 1] = true;
                        if (grid.toGridCoords(iB.yLoc, false) - 
                            grid.toGridCoords(yLoc, false) == i)
                            pos[i - 1] = true;
                    }
                }
                //all blocks in same row as user (facing left)
                else if (faceDirection == 2 && 
                        grid.toGridCoords(iB.yLoc, false) == 
                        grid.toGridCoords(yLoc, false))
                {
                    for (int i = 1; i <= 5; i++)
                    {
                        //assesses if teleportation will hit the wall
                        if (grid.toGridCoords(xLoc, true) - i < 0)
                            pos[i - 1] = true;
                        if (grid.toGridCoords(iB.xLoc, false) - 
                            grid.toGridCoords(xLoc, false) == -i)
                            pos[i - 1] = true;
                    }
                }
                //all blocks in same row as user (facing right)
                else if (faceDirection == 3 && 
                        grid.toGridCoords(iB.yLoc, false) == 
                        grid.toGridCoords(yLoc, false))
                {
                    //assesses if teleportation will hit the wall
                    for (int i = 1; i <= 5; i++)
                    {
                        if (grid.toGridCoords(xLoc, true) + i > 
                            grid.gridSideLength - 1)
                            pos[i - 1] = true;
                        if (grid.toGridCoords(iB.xLoc, false) - 
                            grid.toGridCoords(xLoc, false) == i)
                            pos[i - 1] = true;
                    }
                }
            }
        }
        //looks for the first position with no blocks up to five blocks ahead
        int index = 0;
        for (int i = 4; i >= 0; i--)
        {
            if (pos[i] == false)
            {
                index = i + 1;
                i = -1; //breaks loop
            }
        }
        return index; //number of blocks user will teleport ahead
    }
    /**
     * Replenishes the number of blocks user can place
     * Called upon when user is in their home base
     */
    public void replenishBlocks()
    {
        numBlocks = blocksAllowed; //user's blocks goes back to 100
    }
    /**
     * Allows user to carry extra blocks before needing to replenish
     * Called upon by extra blocks powerup
     * 
     * @param the new number of blocks user can store (is 200)
     */
    public void increaseBlocksAllowed(int newSize)
    {
        blocksAllowed = newSize; //for extra blocks powerup
    }
    /**
     * Determines if user is on the friendly side of the map
     * 
     * @param grid allows for grid methods to be called
     * @param userBase gives access to the userBase object
     * 
     * @return true if user is on the same side as their base
     */
    public boolean getZone(Grid grid, Base userBase)
    {
        //user is in the same zone as their base
        if ((grid.toGridCoords(xLoc, true) + grid.toGridCoords(yLoc, false) < 
            grid.gridSideLength - 2 && userBase.userOnTop) || 
            (grid.toGridCoords(xLoc, true) + grid.toGridCoords(yLoc, false) > 
            grid.gridSideLength - 1 && !userBase.userOnTop) )
            return true;
        return false;
    } 
}