/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vmd;
import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.awt.Point;
import java.awt.geom.RectangularShape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Collections;

public class MouseListener implements MouseWheelListener, java.awt.event.MouseListener, MouseMotionListener {
	VendingMachineCanvas vmc;
	Cursor defaultCursor, handCursor, blankCursor;

	public MouseListener(VendingMachineCanvas vmc){
		this.vmc = vmc;
		handCursor = new Cursor(Cursor.HAND_CURSOR);
		defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		// Create a new blank cursor.
		blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
				cursorImg, new Point(0, 0), "blank cursor");

	}

	public void mouseWheelMoved(MouseWheelEvent e) {}
	public void mouseClicked(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {//set task
		Point p = new Point(e.getX(), e.getY());		
		for(Object box : vmc.boxes)
			if(((RectangularShape)box).contains(p)){
				if(box instanceof EllipseBox){//coins
					vmc.setCursor(blankCursor);
					vmc.mouseText = "$"+VendingMachineCanvas.cashFormat(((EllipseBox)box).amount);
					vmc.mouseMoney = ((EllipseBox)box).amount;
				}
				else{
					switch(((RectangleBox)box).t){
						case PAPERMONEY:
							vmc.setCursor(blankCursor);
							vmc.mouseText = "$"+VendingMachineCanvas.cashFormat(((RectangleBox)box).amount);
							vmc.mouseMoney = ((RectangleBox)box).amount;
							break;
						case BUTTON:
							char keyPressed = ((RectangleBox)box).key;
							vmc.machine.press(vmc.customer, keyPressed);
							break;
					}
				}					
				vmc.repaint();
				break;
			}
	}

	public void mouseDragged(MouseEvent e) {
		vmc.mousex = e.getX();
		vmc.mousey = e.getY();
		vmc.repaint();
	}
	public void mouseMoved(MouseEvent e) {
		vmc.mousex = e.getX();
		vmc.mousey = e.getY();
		boolean hit = false;
		Point p = new Point(e.getX(), e.getY());
		if(vmc.mouseText.length() == 0)
			for(Object box : vmc.boxes)
				if(((RectangularShape)box).contains(p)){
					vmc.setCursor(handCursor);
					hit = true;
				}
		if(!hit)
			vmc.setCursor(defaultCursor);
		vmc.repaint();
	}

	public void mouseReleased(MouseEvent e) {
		if(vmc.slots[0].contains(e.getX(), e.getY()) && vmc.mouseMoney >= 1){//dollars only!
			vmc.customer.transact(-vmc.mouseMoney);
			vmc.machine.insertMoney(vmc.customer, vmc.mouseMoney);
		}
		if(vmc.slots[1].contains(e.getX(), e.getY()) && vmc.mouseMoney < 1){//coins only!
			vmc.customer.transact(-vmc.mouseMoney);
			vmc.machine.insertMoney(vmc.customer, vmc.mouseMoney);
			System.out.println("coid dropped");
		}
		vmc.mouseText = "";
		vmc.mouseMoney = 0;
		vmc.repaint();

	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}

