package vmd;

import java.awt.Rectangle;
import java.awt.geom.RectangularShape;

public class RectangleBox extends Rectangle{
	public static enum type{PAPERMONEY, BUTTON};
	public double amount;
	public char key;
	public type t;
	public RectangleBox(int x, int y, double amount){//dollar bill
		super(x, y, VendingMachineCanvas.PAPERMONEYWIDTH, VendingMachineCanvas.PAPERMONEYHEIGHT);
		this.amount = amount;
		this.t = type.PAPERMONEY;
	}
	public RectangleBox(int x, int y, char key){//machine button
		super(x, y, VendingMachineCanvas.BUTTONWIDTH, VendingMachineCanvas.BUTTONHEIGHT);
		this.key = key;
		this.t = type.BUTTON;
	}
	
}

