import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;
import java.awt.geom.*;
/**
 * The code for the grid
 * 
 * Useful Methods:
 *                  draw: creates grid
 *                  drawSpawn: creates green spawning area
 *                  setFogSize: changes user viewing area
 *                  drawFog: limits viewing area
 *                  toGridCoords: locates row and column 
 *                                of object on the grid
 * 
 * @Avi and Amar Ruthen
 * @12.22.2020
 */
public class Grid
{
    int xLoc, yLoc, squareSize, numMoves;
    int gridSideLength = 200;
    int fogSize = 5;        //LIGHTING CONDITIONS: Change to any positive integer
                            //Does NOT affect flashlight powerup
    /**
     * Constructor for the Grid
     */
    public Grid(int xLoc, int yLoc, int squareSize)
    {
        this.xLoc = xLoc;
        this.yLoc = yLoc;
        this.squareSize = squareSize;
        numMoves = 0;
    }

    /**
     * Moves the whole Grid in 4 directions
     * @param moveCode 0-3 code, in order: up, down, left, right
     */
    public void move(int moveCode)
    {
        numMoves++;
        switch(moveCode)
        {
            case 0:     //move up
            yLoc--;
            break;

            case 1:     //move down
            yLoc++;
            break;

            case 2:     // move left
            xLoc--;
            break;

            case 3:     //moves right
            xLoc++; 
            break;

            default:    //Don't move
            break;
        }
    }
    /**
     * Draws the full grid while alternating colors for aesthetic reasons
     * Also draws diagonal border
     * @param g allows for drawing to be displayed on Graphics Window
     */
    public void draw(Graphics g)
    {
        // draw the grid:
        for (int col = 0; col < gridSideLength; col++) {
            for (int row = 0; row < gridSideLength; row++) {
                // set location of where to draw current rectangle
                int x = (int) (squareSize * col) + (squareSize * xLoc);
                int y = (int) (squareSize * row) + (squareSize * yLoc);
                
                if (row + col == gridSideLength - 1)            //On diagonal border        
                    g.setColor(Color.RED);      //between user and comp
                else if ((row + col) % 2 == 0)
                    g.setColor(Color.GRAY);
                else
                    g.setColor(Color.BLUE);

                g.drawRect(x, y, squareSize, squareSize);
            }
        }
    }
    
    /**
     * Draws the green spawn section where bases are located
     * Uses the INITIAL base coordinates (meaning before any movement)
     * Spawn is drawn around the top left corner of the base picture
     * 
     * @param g allows for drawing to be displayed on Graphics Window
     * @param spawnCol initial base xLocation
     * @param spawnRow initial base yLocation
     */
    public void drawSpawn(Graphics g, int spawnCol, int spawnRow)
    {
        //Draws 5X5 grid centered around base image 
        //NOTE: (row = 0, col = 0) is top-left corner of base pic in this case
        
        for (int col = spawnCol - 1; col <= spawnCol + 3; col++) 
        {
            for (int row = spawnRow - 1; row <= spawnRow + 3; row++) 
            {
               int x = (int) (squareSize * col) + (squareSize * xLoc);
               int y = (int) (squareSize * row) + (squareSize * yLoc);
               
               //change color of spawn area here!
               g.setColor(Color.GREEN);
               g.drawRect(x, y, squareSize, squareSize);
            }
        }
    }
    
    /**
     * Allows other classes to change fogSize 
     * for a larger (or smaller) viewing area
     * 
     * @param size how many blocks (in any direction) you would
     * like the character to be able to view
     */
    public void setFogSize(int size)
    {
        fogSize = size;
    }
    
    /**
     * Limits the viewing area of the user (is changed by flashlight powerup)
     * Change the value of fogSize to change the size of the viewing area
     * 
     * @param g allows for drawing to be displayed on Graphics Window
     */
    public void drawFog(Graphics g)
    {
        for (int col = 0; col < gridSideLength; col++) {
            for (int row = 0; row < gridSideLength; row++) {
                int x = (int) (squareSize * col) + (squareSize * xLoc);
                int y = (int) (squareSize * row) + (squareSize * yLoc);
                
                g.setColor(Color.BLACK);
                if((Math.abs(20 - (col + xLoc)) > fogSize) || (Math.abs(15 - (row + yLoc)) > fogSize))
                    g.fillRect(x, y, squareSize + 1, squareSize + 1);
            }
        }
    }
    
    /**
     * Since the character stays in the same place on screen and 
     * the other objects move, method takes the xLoc or yLoc of
     * an object and returns the corresponding row or column on
     * the grid that the object is located
     * 
     * @param coord takes either the xLoc or yLoc of an object
     * @param toggleX is true if finding xLoc, false for yLoc
     * @return corresponding row or column, based on toggleX
     */
    public int toGridCoords(int coord, boolean toggleX)
    {
        if(toggleX)
            return (-1 * xLoc) + coord;
        return (-1 * yLoc) + coord;
    }
}