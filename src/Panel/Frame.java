package Panel;

import javax.swing.*;

public class Frame 
{

	public static void main(String[] args)
	{
		
		JFrame frame = new JFrame("2D Puzzle Thing");

		frame.setLocationByPlatform(true);
		frame.add(new Panel());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setVisible(true);
		frame.repaint();
	}
	
}
