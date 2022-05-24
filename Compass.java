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
 * Handles the rotation of the compass image and tracking of computerBase
 * 
 * Useful Methods: 
 *                  rotateCompass: Calculates theta for compass
 *                                 arrow rotation
 *                  drawCompass: displays compass
 *          
 * @Avi and Amar Ruthen
 * @12.22.2020
 */
public class Compass
{
    BufferedImage compass = null;
    int xLoc, yLoc;
    int ERROR = -1;
    
    /**
     * Constructor for the compass. Adds the arrow image
     */
    public Compass(int xLoc, int yLoc)
    {
        try
        {
            compass = ImageIO.read(this.getClass().getResourceAsStream(
            "resources/compass.png")); //the name of the image, stored in an images folder
        }
        catch(Exception e){}
        this.xLoc = xLoc;
        this.yLoc = yLoc;
    }
    
    /**
     * Constantly tracks the compbase and adjusts the arrow of compass accordingly
     * 
     * @param baseX is the computer base xLocation (as xLoc or gridCoords)
     * @param baseY is the computer base yLocation (as yLoc or gridCoords)
     * @param charX is the character xLocation (as xLoc or gridCoords)
     * @param charY is the character yLocation (as yLoc or gridCoords)
     * Make sure all parameters are either xLoc/yLoc or in gridCoords
     * 
     * returns the theta needed for arrow rotation
     */
    public double rotateCompass(int baseX, int baseY, int charX, int charY)
    {
        //NOTE: for parameters, use grid coords
        double theta = 0;
        double deltaX = baseX - charX;
        double deltaY = baseY - charY;

        if(deltaX == 0)
            if(deltaY > 0)
                return (Math.PI/2);
            else if(deltaY < 0)
                return 3 * (Math.PI/2);
            else
                return ERROR;
        if(deltaY == 0)
            if(deltaX > 0)
                return 0;
            else
                return Math.PI;             //Case where deltaY and deltaX are zero is dealt with above
        
        theta = Math.atan(deltaY/deltaX);
        
        if(deltaX > 0 && deltaY < 0)        //Q1, 
            theta *= -1;
        else if (deltaX < 0 && deltaY < 0)  //Q2, theta must be adjusted as its complement
            theta = Math.PI - theta;
        else if(deltaX < 0 && deltaY > 0)   //Q3, theta must be adjusted as its complement 
            theta = Math.PI - theta;
        else if(deltaX > 0 && deltaY > 0)   //Q4, theta must be adjusted as 2Pi complement
            theta = 2 * Math.PI - theta;
        else
            return -1;
        
        return (2 * Math.PI) - theta;       //Thetas calculated for clockwise rotation. 
                                            //2pi - theta is for counter-clockwise rotation
    }
    
    /**
     * Rotates the arrow image in compass towards the direction of the compBase
     * 
     * @param g allows for arrow to be drawn on Graphics Window
     * @param theta is the angle image must be rotated (see above)
     * @param charX is the current character xLocation (as xLoc)
     * @param charY is the current character yLocation (as yLoc)
     */
    public void drawCompass(Graphics g, double theta, int charX, int charY)
    {
        AffineTransform at = AffineTransform.getTranslateInstance(charX, charY);
        at.rotate(theta);
        
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(compass, at, null);   
    }
}