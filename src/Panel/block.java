package Panel;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
 


public class block extends parent_block {
	int type;
	int indX;
	int indY; 
	int width;
	int height;
    Rectangle bound;

    BufferedImage img;
    
    boolean visible, moving;
    
    public static double round_to(double val, double round){
    	return Math.round(val/round)*round;
    }
    
    public block(double startX, double startY, int width1, int height1)
    {
    	super(round_to(startX,32),round_to(startY,32),(int)round_to(width1,32),(int)round_to(height1,32)); 
    	setDepth(10);
        //just in case 
        moving = false;
        visible = true;
    }
   

	public double getX()
    {
            return x;
    }

       
    public double getY()
    {
            return y;
    }
    
    
    public int getType()
    {
            return type;
    }
   
    public boolean getVisible()
    {
            return visible;
    }
    
    //???
    public Image getImage()
    {
            return img;
    }
    
    public boolean getMoving()
    {
            return moving;
    }
   
    public void move(){

    	
    }
    
}
