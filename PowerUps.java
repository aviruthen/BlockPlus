import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;
import java.awt.geom.*;
import java.lang.Math;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.awt.geom.AffineTransform;
/**
 * The code for many of the different powerups
 * 
 * Useful Methods:
 *                  fastBlockBreaking: increases user block-breaking
 *                                     speed
 *                  teleport: moves user five blocks in the direction
 *                            user is facing
 *                  autoRespawn: instantly teleports user back to
 *                               home base
 *                  extraBlocks: user gets 200 blocks before 
 *                               needing to replenish
 *                  toggleFlashlight: user can manually increase
 *                                    viewing size by specified blocks
 *                  newSpawn: creates a new spawning location for base
 *                            on the same side of the map
 *                    
 * @Avi and Amar Ruthen
 * @12.22.2020
 */
public class PowerUps
{
    BufferedImage compass = null;
    
    int numSquaresAcross;
    int squareSize;
    boolean fastBreaking;
   
    Grid grid;
    Character user;
    Base userBase;
    Base compBase;
    PowerUps p;
    Block[] userBlocks = new Block[1000];
    Block[] immovableBlocks = new Block[10];
    Block[] neutralBlocks = new Block[500];
    Block[] compBlocks = new Block[1000];
    
    /**
     * Constructor for the PowerUps class
     * Transfers all objects and variables here for use
     */
    public PowerUps(Base userBase, Base compBase, Character user, Grid grid, 
                    Block[] userBlocks, Block[] immovableBlocks, Block[] neutralBlocks, 
                    Block[] compBlocks, int w, int h, int numSquaresAcross)
    {
        this.userBase = userBase;
        this.compBase = compBase;
        this.user = user;
        this.grid = grid;
        this.userBlocks = userBlocks;
        this.immovableBlocks = immovableBlocks;
        this.neutralBlocks = neutralBlocks;
        this.compBlocks = compBlocks;

        this.numSquaresAcross = numSquaresAcross;
        squareSize = 600 / numSquaresAcross;
    }

    /**
     * Doubles the speed of breaking blocks
     * 
     * @param blockBreakingSpeed takes the current block-breaking speed
     *        (is variable blockBreakingSpeed in Graphics Window)
     *        
     * @return the new blockBreakingSpeed for the user
     */    
    public int fastBlockBreaking(int blockBreakingSpeed)
    {
        //allows you to toggle fastBreaking on and off
        fastBreaking = !fastBreaking;
        if (fastBreaking)
            return blockBreakingSpeed/2; //breaks blocks faster
        return blockBreakingSpeed*2;     //breaks blocks slower
    }
    
    /**
     * Moves user five blocks in the direction they are facing
     * 
     * Will adjust teleportation distance if the last block the user 
     * will land on is a block
     * 
     * @param g allows for graphical movement of user from the PowerUps class
     */
    public void teleport(GraphicsWindow g)
    {
      int numBlocksAhead = user.teleportMove(compBlocks, neutralBlocks, immovableBlocks);
      for (int i = 0; i < numBlocksAhead; i++)
      {
          if (user.canMove(compBlocks, neutralBlocks, immovableBlocks, true, -1))
              g.move(user.faceDirection);
      }
    }
    
    /**
     * Automatically teleports user back to their home base from anywhere on map
     * 
     * @param g allows for graphical movement of user from the PowerUps class
     */
    public void autoRespawn(GraphicsWindow g)
    {
        //Calculates horizontal and vertical distance to the userBase
        int deltaX = userBase.initialX - grid.toGridCoords(user.xLoc, true);
        int deltaY = userBase.initialY - grid.toGridCoords(user.yLoc, false);
        
        //Moves the user instantaneously to the correct xLocation on the map
        if(deltaX >= 0)
        {
            for(int i = 0; i < deltaX; i++)
                g.move(3);
        }
        else
        {
            for(int i = 0; i > deltaX; i--)
                g.move(2);
        }
        
        //Moves the user instantaneously to the correct yLocation on the map
        if(deltaY >= 0)
        {
            for(int i = 0; i < deltaY; i++)
                g.move(1);
        }
        else
        {
            for(int i = 0; i > deltaY; i--)
                g.move(0);
        }
        
        g.move(1);   //Extra movement needed as center xLoc 
        g.move(3);   //and yLoc for picture is one off
    }
    
    /**
     * Gives user access to more blocks without replenishment 
     * by increasing blocksAllowed
     */
    public void extraBlocks()
    {
        user.increaseBlocksAllowed(200);
    }
    
    /**
     * Allows user to toggle the size of their viewing screen 
     * from a 10x10 to a 20x20 
     * 
     * NOTE: setFogSize sets the number of viewable blocks in 
     * one direction, then applies that for all directions
     */
    public void toggleFlashlight()
    {
        if(grid.fogSize == 5)
            grid.setFogSize(10); //Change the viewing size by given # of blocks
        else
            grid.setFogSize(5);
    }
    
    /**
     * Sets a new spot for the userBase (on the same side of the map as before)
     */
    public void newSpawn()
    {
        //Creates a temporary base that will create the new spawn location
        Base t = new Base(0, 0, squareSize, true);
        
        //Checks the side of the map the userBase currently is
        t.userOnTop = userBase.userOnTop;
        
        //Randomizes a new location given its side on the map
        int userBaseX = t.setRandomX(grid);
        int userBaseY = t.setRandomY(userBaseX, grid);

        userBase.setInitialCoords(userBaseX, userBaseY);
    }
}