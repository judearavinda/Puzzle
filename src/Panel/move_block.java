package Panel;

public class move_block extends block{

	public move_block(double startX, double startY, int width1, int height1) {
		super(startX, startY, width1, height1);
		setDepth(8);
		hacc = 0.29;
	}
}
