import java.awt.*;
import javax.swing.*;

/**
 * The class for all block objects
 * 
 * Useful Methods:
 *                  draw: creates each block
 *                  setColor: allows one to change the
 *                            color of block to be drawn
 * 
 * @Avi and Amar Ruthen
 * @12.22.2020
 */
public class Block 
{
    int xLoc, yLoc, squareSize, numMoves;
    boolean user, immovable;
    Color blockColor;

    /**
     * Constructor for all blocks created
     */
    public Block(int xLoc, int yLoc, int squareSize, boolean user, 
                 Color blockColor, boolean immovable) 
    {
        this.xLoc = xLoc;
        this.yLoc = yLoc;
        this.squareSize = squareSize;
        this.user = user;
        this.blockColor = blockColor;
        this.immovable = immovable;
        
        numMoves = 0;
    }

    /**
     * Moves the blocks in 4 directions
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
     * Draws each block, which is represented as a colored square
     * 
     * @param g allows for drawing to be displayed on Graphics Window
     */
    public void draw(Graphics g) 
    {
        // location that block should be drawn
        int x = (int) (squareSize * xLoc);
        int y = (int) (squareSize * yLoc);

        //Sets color of block depending on the type of block
        if(immovable)
            g.setColor(Color.GRAY);
        g.setColor(blockColor);

        g.fillRect(x, y, squareSize, squareSize);
    }
    /**
     * Allows other classes to set the color of the block to be drawn
     * 
     * @param c is the desired color
     */
    public void setColor(Color c)
    {
        blockColor = c;
    }
}