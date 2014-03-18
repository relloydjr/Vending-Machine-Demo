package vmd;

import java.awt.geom.Ellipse2D;

public class EllipseBox extends Ellipse2D.Double{
	public double amount;

	public EllipseBox(int x, int y, int radius, double amount){
		super(x - radius, y - radius, radius * 2, radius * 2);
		this.amount = amount;
	}
}


