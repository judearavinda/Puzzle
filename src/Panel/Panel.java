package Panel;


import java.awt.AlphaComposite;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;


public class Panel extends JPanel implements Runnable
{
/**
	 * 
	 */
	private static final long serialVersionUID = -9L;
	
	//Initiate variables

static String direc;
//Timer time;
double fps = 0.0;
double drawfps = 0.0;
double lastTime = 0.0;
double game_speed = 60.0;
int elapse = 0;
int tt = 0;
boolean key_pressed_up = false;
boolean key_pressed_left = false;
boolean key_pressed_right = false;

Color c_black = Color.BLACK;
Color c_bk = Color.DARK_GRAY;
Color c_gray = Color.GRAY;
Color c_white = Color.WHITE;
Color c_red = Color.RED;
Color c_blue = Color.BLUE;
Color c_green = Color.GREEN;
Color c_yellow = Color.YELLOW;

boolean key = false;
int cLVL = 0;

player player1;
//move_block wgt;
////////

String[] levels = {
		"level0.txt",
		"level1.txt",
		"level2.txt",
		"level3.txt",
		"level4.txt",
};

int vx, vy, vw, vh;
double camx, camy;

static Point window;
static ArrayList<parent_block> blocks;

	private Thread animator;
	private final int DELAY = (int)(1000.0/game_speed);

	public Panel()
	{
		
		
		setBackground(c_black);
		blocks = new ArrayList<parent_block>();
		
		addKeyListener(new AL());
		setFocusable(true);
        setFocusable(true);
        setDoubleBuffered(true);
		direc = System.getProperty("user.dir")+"/";
		
		//remove cursor
//		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
//		setCursor(blankCursor);
		
		vx = 128;
		vy = 128;
		vw = 800 - (vx*2);
		vh = 600 - (vy*2);
		
		camx=0;
		camy=0;
		
		try {
			parseFile(direc + levels[cLVL]);
			//blocks.add(new move_block(288-128+32, 1150-64, 32, 32));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/////////////
		//tile[1] =  loadIMG("Resources/Images/redstone_block.png");
		//tile[2] =  loadIMG("Resources/Images/lapis_block.png");
		
		//backImg = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
		//Graphics2D backG = (Graphics2D)backImg.getGraphics();

		
		//set_alpha(backG, 1f);
		
		
		//time = new Timer((int) (1000/game_speed), this); //game_speed is frames per second
		//time.start();
		
	}
	
	public void addNotify() {
        super.addNotify();
        animator = new Thread(this);
        animator.start();
    }
	
	//Load shit
	public BufferedImage loadIMG(String file)
	{
		//ImageIcon imgicn = new ImageIcon(direc+file);
		BufferedImage img, in = null;
		Graphics2D g2d;
		File fimg = new File(Panel.direc+file);
		
        Dimension g = Panel.getBuff(fimg);
        
        try {
			in = ImageIO.read(fimg);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		img = new BufferedImage((int) g.getWidth(),(int) g.getHeight(),BufferedImage.TYPE_INT_ARGB);
		g2d = img.createGraphics();
		g2d.drawImage(in, 0, 0, null);
		g2d.dispose();
		return img;
	}
	
	public ArrayList<parent_block>return_ordered_block(ArrayList<parent_block> unsorted_list)
	{
		//sort unsorted_list by arrays
		ArrayList<parent_block> sorted_parent_blocks = new ArrayList<parent_block>();
		HashMap<Integer, ArrayList<parent_block>> hash_map_orderings = new HashMap<Integer, ArrayList<parent_block>>();
		//put all the values in a hashmap
		for (int i = 0; i < unsorted_list.size();i++)
		{
			if (hash_map_orderings.containsKey(unsorted_list.get(i).getDepth()))
			{
				ArrayList<parent_block> new_array = hash_map_orderings.get(unsorted_list.get(i).getDepth());
				new_array.add(unsorted_list.get(i));
				hash_map_orderings.put(unsorted_list.get(i).getDepth(), new_array); 
			}
			else
			{
				ArrayList<parent_block> new_array = new ArrayList<parent_block>();
				new_array.add(unsorted_list.get(i));
				hash_map_orderings.put(unsorted_list.get(i).getDepth(), new_array); 
			}
		}
		Set<Integer> integer_set = hash_map_orderings.keySet();
		int current_max_depth;
		while(!integer_set.isEmpty())
		{
			current_max_depth =  Collections.max(integer_set);
			for (int i = 0; i < hash_map_orderings.get(current_max_depth).size();i++)
			{
				sorted_parent_blocks.add(hash_map_orderings.get(current_max_depth).get(i));
			}
			integer_set.remove(current_max_depth);
		}
		return sorted_parent_blocks;
		
	}
	
	public void update(parent_block bl){
		if (bl.getVsp() > 0){
			for (int i = 0; i< bl.getVsp(); i++){
				if (!blockCheck(0,1,bl))
					bl.setY(bl.getY()+1);
			}
		}else if (bl.getVsp() < 0){
			for (int i = 0; i< -bl.getVsp(); i++){
				if (!blockCheck(0,-1,bl))
					bl.setY(bl.getY()-1);
				else
					bl.setVsp(0);
			}
		}
		
		if (bl.getHsp() > 0){
			for (int i = 0; i< bl.getHsp(); i++){
				if (!blockCheck(1,0,bl))
					bl.setX(bl.getX()+1);
			}
		}else if (bl.getHsp() < 0){
			for (int i = 0; i< -bl.getHsp(); i++){
				if (!blockCheck(-1,0,bl))
					bl.setX(bl.getX()-1);
			}
		}
	}
	
	public void run()
	{

		while (true)
		{
		
			fps = -1000000000.0 / (lastTime - (lastTime = System.nanoTime())); 
			if (elapse > 30)
			{
				drawfps = fps;
				
				elapse = 0;
			}
			
			
			
			for (int i = 0; i < blocks.size();i++)
			{
				parent_block bl = blocks.get(i); 
				if (bl instanceof move_block)
				{
					if (!blockCheck(0,1,bl)){
						bl.setVsp(bl.getVsp()+0.5);
					}else{
						bl.setVsp(0);
					}
					
					if (Math.abs(bl.getHsp()) > bl.getHacc()){
						   if (bl.hsp>0){
							   bl.hsp -= bl.hacc; 
						   }else if (bl.hsp<0){
							   bl.hsp += bl.hacc; 
						   }
						}else{
							bl.hsp = 0;
							bl.x = Math.round(bl.x/32)*32;
						}
					
					update(bl);
					
					
					
				}
				else if (bl instanceof Key)
				{
					if (!key){
						if (blockCheckPlayer(0,0,bl)){
							key = true;
						}
					}
				}
				else if (bl instanceof Door)
				{
					if (blockCheckPlayer(0,0,bl) && key){
						blocks.clear();
						cLVL++;
						key = false;
						try {
							parseFile(direc + levels[cLVL]);
							
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			
			//JELLYYYY
			/*
			double[] inter = new double[player1.detail];
			int k = 0;
			for (double j=0; j<(2*Math.PI); j+=(2*Math.PI)/player1.detail, k++){
				
				double x_mid = player1.getX()+(player1.getWidth()/2.0);
				double y_mid = player1.getY()+(player1.getHeight()/2.0);
				
				for (double i = 0; i < (int)player1.jelly_vel[k]; i++){
					if (blockPointCheck(
							x_mid+i*Math.sin(j),
							y_mid+i*Math.cos(j)
							)){
						
						//player1.setHsp(player1.getHsp()-(i/128) * Math.sin(j));
						//player1.setVsp(player1.getVsp()-(i/128) * Math.cos(j));
						player1.jelly_dis[k] = i;//Math.min(i,player1.jelly_dis[k]);
						break;
					}else{
						//player1.jelly_dis[k]++;
					}
					//
				}
				
			}
			

			for (k = 0; k < player1.detail; k++){
				if (k == 0){
					
				}else if (k == player1.detail-1){
					
				}else{
					inter[k] = (player1.jelly_dis[k]*0.4 + player1.jelly_dis[k+1]*0.2 + player1.jelly_dis[k-1]*0.2);
				}
			}
			
			for (k = 0; k < player1.detail; k++){
				player1.jelly_dis[k] = inter[k];
				player1.jelly_dis[k] += player1.jelly_vel[k];
				
				for(int h = 0; h <  player1.jelly_vel[k]; h++){
					
				}
				
				player1.jelly_vel[k] += player1.jelly_acc[k];
				player1.jelly_acc[k] = (player1.R - player1.jelly_dis[k]) * 0.03;
			}
			*/
			
			/*double xoff = 32/2;
			double yoff = 32/2;
			double d = dist(wgt.x,wgt.y,player1.x+xoff,player1.y+yoff);
			double maxx = 128;
			*/
			
			if (!blockCheck(0,1,player1)){
				//player1.vacc = 0.5;
				player1.setVsp(player1.vsp+0.5);
			}else{
				//player1.vacc = 0;
				player1.setVsp(0);
				if (key_pressed_up){
					player1.setVsp(player1.getJumpspeed());
				}
			}
			
			//double gx;
			//double gy;
			
			
			
			if (player1.getVsp()<0 && !key_pressed_up){
				player1.setVsp(player1.getVsp()+1.3);
			}
			
			
			
			/*
			double ang = getAngle(player1.x+xoff,player1.y+yoff,wgt.x,wgt.y);
			
			player1.gx = (int) (player1.x-player1.hsp);
			player1.gy = (int) (player1.y-player1.vsp);
			
			double gd = dist(wgt.x,wgt.y,player1.gx+xoff,player1.gy+yoff);
			double gang = getAngle(player1.gx+xoff,player1.gy+yoff,wgt.x,wgt.y);
			
			if (d > maxx){
				//player1.hsp=player1.hsp*Math.sin(ang);
				//player1.vsp=player1.vsp*Math.cos(ang);
				
				//player1.gx = (int) (wgt.x + max*Math.sin(ang)-xoff);
				//player1.gy = (int) (wgt.y + max*Math.cos(ang)-yoff);
				
				player1.x = (int) (wgt.x + maxx*Math.sin(ang)-xoff);
				player1.y = (int) (wgt.y + maxx*Math.cos(ang)-yoff);
				
				player1.gx = (int) (wgt.x + Math.min(gd,maxx)*Math.sin(gang)-xoff);
				player1.gy = (int) (wgt.y + Math.min(gd,maxx)*Math.cos(gang)-yoff);
				
				//double dx = (player1.gx - player1.x);
				//double dy = (player1.gy - player1.y);
				
				double ang1 = getAngle(player1.gx+xoff,player1.gy+yoff,player1.x+xoff,player1.y+yoff);
				
				gd = Math.min(dist(player1.gx,player1.gy,player1.x,player1.y),1.0);
				
				player1.vsp = player1.vsp*Math.cos(ang1)*gd;
				player1.hsp = player1.hsp*Math.sin(ang1)*gd;
			}
			*/
			
			//CAN BE OPTIMIZED
			//add y and x values
			if (player1.getVsp() > 0){
				for (int i = 0; i< player1.getVsp(); i++){
					if (!blockCheck(0,1,player1))
						player1.setY(player1.getY()+1);
				}
			}else if (player1.getVsp() < 0){
				for (int i = 0; i< -player1.getVsp(); i++){
					if (!blockCheck(0,-1,player1))
						player1.setY(player1.getY()-1);
					else
						player1.setVsp(0);
				}
			}
			
			if (player1.getHsp() > 0){
				for (int i = 0; i< player1.getHsp(); i++){
					if (!blockCheck(1,0,player1))
						player1.setX(player1.getX()+1);
				}
			}else if (player1.getHsp() < 0){
				for (int i = 0; i< -player1.getHsp(); i++){
					if (!blockCheck(-1,0,player1))
						player1.setX(player1.getX()-1);
				}
			}

			
			
			
			//Redraws the screen
			repaint();
			
			try
			{
		    	Thread.sleep(DELAY);
			}
			catch (InterruptedException e)
			{
		    	System.out.println("interrupted");
			}
		}
	}

	public float getAngle(double d, double e, double camx2, double camy2) {
	    float angle = (float) Math.atan2( d - camx2, e - camy2);

	    return angle;
	}
	
	public float dist(double d, double e, double camx2, double camy2) {
		return (float) Math.sqrt(Math.pow(camx2-d,2) + Math.pow(camy2-e,2));
	}
	
	public void paint(Graphics g) {
		elapse++;
		tt++;
		
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		
		//box
		g2d.setColor(c_bk);
		g2d.fillRect(vx, vy, vw, vh);
		

		double dist = dist(player1.getX(),player1.getY(),camx,camy);
		double dir = getAngle(player1.getX(),player1.getY(),camx,camy);
		camx += dist*Math.sin(dir)*0.1;
		camy += dist*Math.cos(dir)*0.1;
		
		
		g2d.setColor(c_white);
		//g2d.drawString(String.valueOf(player1.gx - player1.x), 64, 64);
		
		
		g2d.translate(400-camx, 300-camy);
		///////////
		
		//g2d.draw(new Line2D.Double(player1.x+16, player1.y+16, wgt.x+16, wgt.y+16));
		
		//g2d.drawOval(player1.gx-4, player1.gy-4, 8, 8);
		
		for (int i = 0; i < blocks.size();i++)
		{
			parent_block bl = blocks.get(i); 
			
			if (bl instanceof player)
			{
				g2d.setColor(c_red);
				g2d.fillRect((int)bl.getX(), (int)bl.getY(), bl.width, bl.height);
				
				/*
				int k =0;
				for (double j=0; j<(2*Math.PI); j+=(2*Math.PI)/player1.detail, k++){
					
					double x_mid = player1.getX()+player1.getWidth()/2.0;
					double y_mid = player1.getY()+player1.getHeight()/2.0;
					
					g2d.draw(new Line2D.Double(x_mid, y_mid, 
							x_mid+player1.jelly_dis[k]*Math.sin(j), 
							y_mid+player1.jelly_dis[k]*Math.cos(j)));
				}*/
				
			}
			else if (bl instanceof move_block)
			{
				g2d.setColor(c_blue);
				block b2 = (block) bl;
				g2d.fillRect((int)bl.getX(), (int)bl.getY(), b2.getWidth(), b2.getHeight());
			}
			else if (bl instanceof block)
			{
				g2d.setColor(c_green);
				block b2 = (block) bl;
				g2d.fillRect((int)bl.getX(), (int)bl.getY(), b2.getWidth(), b2.getHeight());
			}
			else if (bl instanceof Door)
            {
				if (!key){
            		g2d.setColor(c_gray); 
            	}else{
            		g2d.setColor(c_white); 
            	}
                 
                g2d.fillRect((int)bl.getX(), (int)bl.getY(), bl.getWidth(), bl.getHeight());
            }
            else if (bl instanceof Key)
            {
            	if (!key){
            		g2d.setColor(c_yellow); 
                    g2d.fillRect((int)bl.getX(), (int)bl.getY(), bl.getWidth(), bl.getHeight());
            	}
                
            }
			else
			{
				System.out.println("Error");
			}
		}
		
		Toolkit.getDefaultToolkit().sync();
        g.dispose();
	}
	
	public void parseFile(String string) throws FileNotFoundException
    {
        File file = new File(string);
        Scanner input = new Scanner(file);
        ArrayList<String> lines = new ArrayList<String>();
        while(input.hasNext())
        {
            lines.add(input.next());
        }
        for (int i = 0; i < lines.size();i+=5)
        {
            if (lines.get(i).equals("player"))
            {
                //it is a player
                player1 = new player((int) Double.parseDouble(lines.get(i+1)),(int) Double.parseDouble(lines.get(i+2)));  
                blocks.add(player1);
                camx=player1.x;
                camy=player1.y;
                
            }
            else if (lines.get(i).equals("door"))
            {
                //it is a player
                Door door = new Door((int) Double.parseDouble(lines.get(i+1)),(int) Double.parseDouble(lines.get(i+2)));  
                blocks.add(door);
                
            }
            else if (lines.get(i).equals("key"))
            {
                //it is a player
                Key key = new Key((int) Double.parseDouble(lines.get(i+1)),(int) Double.parseDouble(lines.get(i+2)));  
                blocks.add(key);
                
            } 
            else if (lines.get(i).equals("move"))
            {
                //it is a block  
            	move_block wgt = new move_block((int) Double.parseDouble(lines.get(i+1)), (int) Double.parseDouble(lines.get(i+2)),(int)  Double.parseDouble(lines.get(i+3)), (int) Double.parseDouble(lines.get(i+4)));
                blocks.add(wgt);
            }
            else
            {
                //it is a block  
                blocks.add(new block((int) Double.parseDouble(lines.get(i+1)), (int) Double.parseDouble(lines.get(i+2)),(int)  Double.parseDouble(lines.get(i+3)), (int) Double.parseDouble(lines.get(i+4))));
            }
            blocks = return_ordered_block(blocks);
        }
        
        input.close();    
    }
	
	public void set_alpha(Graphics2D g2d, float alpha)
	{
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		g2d.setComposite(ac);
	}
	
	public static Dimension getBuff(File fold){
		BufferedImage readImage = null;

		try 
		{
		    readImage = ImageIO.read(fold);
		    int h = readImage.getHeight();
		    int w = readImage.getWidth();
		    return new Dimension(w,h);
		} 
		catch (Exception e) 
		{
		    readImage = null;
		}
		System.out.print("dim err\n");
		return new Dimension(0,0);
	}
	
	boolean point_rect(double AX1 , double AX2, double AY1, double AY2, double PX, double PY){
		return (PX > AX1 && PX < AX2 && PY > AY1 && PY < AY2);
	}
	
	boolean collision_detection(double AX1 , double AX2, double AY1, double AY2, double BX1 , double BX2, double BY1, double BY2)
	{
		return (AX1 < BX2 && AX2 > BX1 &&
			    AY1 < BY2 && AY2 > BY1); 
	}
	
	boolean blockPointCheck(double d, double e)
	{
		for (int i = 0; i < blocks.size();i++)
		{
			if (blocks.get(i) instanceof block)
			{
				if (point_rect(blocks.get(i).getX(), blocks.get(i).getX()+ blocks.get(i).getWidth(),
						blocks.get(i).getY(),blocks.get(i).getY()+blocks.get(i).getHeight(),d,e))
					return true;
			}
		}
		return false;
	}
	
	boolean blockCheck(int x, int y, parent_block p)
	{
		for (int i = 0; i < blocks.size();i++)
		{
			if (blocks.get(i) instanceof block && blocks.get(i) != p)
			{
				if (collision_detection(blocks.get(i).getX(), blocks.get(i).getX()+ blocks.get(i).getWidth(),
						blocks.get(i).getY(),blocks.get(i).getY()+blocks.get(i).getHeight(),
						p.getX()+x,p.getX()+x+p.getWidth(),
						p.getY()+y,p.getY()+y+p.getHeight()))
					return true;
			}
		}
		return false;
	}
	
	boolean blockCheckPlayer(int x, int y, parent_block p)
	{
		for (int i = 0; i < blocks.size();i++)
		{
			if (blocks.get(i) instanceof player && blocks.get(i) != p)
			{
				if (collision_detection(blocks.get(i).getX(), blocks.get(i).getX()+ blocks.get(i).getWidth(),
						blocks.get(i).getY(),blocks.get(i).getY()+blocks.get(i).getHeight(),
						p.getX()+x,p.getX()+x+p.getWidth(),
						p.getY()+y,p.getY()+y+p.getHeight()))
					return true;
			}
		}
		return false;
	}
	
	boolean blockCheckSpec(int x, int y, parent_block p, parent_block t)
	{
				if (collision_detection(t.getX(), t.getX()+ t.getWidth(),
						t.getY(),t.getY()+t.getHeight(),
						p.getX()+x,p.getX()+x+p.getWidth(),
						p.getY()+y,p.getY()+y+p.getHeight()))
					return true;
				return false;
	}
	
	public void setMBSpeed(move_block MB, double spd){
		if (MB instanceof move_block)
		{
			
				MB.hsp = spd;
				
				for (int i = 0; i < blocks.size();i++)
				{
					parent_block bl = blocks.get(i); 
					if (bl instanceof move_block && bl!=MB && blockCheckSpec(0, -32, MB, bl))
					{
						setMBSpeed((move_block)bl, spd);
					}
				}
				
			
		}
		
		
		
	}
	
	private class AL extends KeyAdapter
	{
		
		public void keyPressed(KeyEvent e) 
		{
			int key = e.getKeyCode();
			
			if (key == KeyEvent.VK_LEFT) {
				player1.setHsp(0-player1.getWalkspeed());
				
				//////////////
				if (!key_pressed_left){
					key_pressed_left = true;
					for (int i = 0; i < blocks.size();i++)
					{
						parent_block bl = blocks.get(i); 
						if (bl instanceof move_block)
						{
							if (blockCheckPlayer(1,0,bl) && blockCheck(0,1,player1)){
								setMBSpeed((move_block)bl, -4);
								player1.setHsp(0);
							}
						}
					}
				}
				
				
		    }
			else if (key == KeyEvent.VK_RIGHT) {
				player1.setHsp(player1.getWalkspeed());
				
				//////////////
				if (!key_pressed_right){
					key_pressed_right = true;
					
					for (int i = 0; i < blocks.size();i++)
					{
						parent_block bl = blocks.get(i); 
						if (bl instanceof move_block)
						{
							if (blockCheckPlayer(-1,0,bl) && blockCheck(0,1,player1)){
								setMBSpeed((move_block)bl, 4);
								player1.setHsp(0);
							}
						}
					}
				}
				
				
		    }
			else if (key == KeyEvent.VK_UP) {
				key_pressed_up = true;
		    } 

		}
		public void keyReleased(KeyEvent e) 
		{
			int kkey = e.getKeyCode();
			
			if (kkey == KeyEvent.VK_LEFT) {
				player1.setHsp(0);
				key_pressed_left = false;
				
			}else if (kkey == KeyEvent.VK_RIGHT) {
				player1.setHsp(0);
				key_pressed_right = false;
		    }
			else if (kkey == KeyEvent.VK_UP) {
				key_pressed_up = false;
		    }else if(kkey == KeyEvent.VK_R){
		    	blocks.clear();
				key = false;
				try {
					parseFile(direc + levels[cLVL]);
					
				} catch (FileNotFoundException ev) {
					// TODO Auto-generated catch block
					ev.printStackTrace();
				}
		    }
			
		}
	}
}


