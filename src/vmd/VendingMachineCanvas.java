package vmd;

import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;



public class VendingMachineCanvas extends JComponent{
	
	public static String mouseText ="";
	public static double mouseMoney = 0;

	public static VendingMachine machine;
	public static Customer customer;

	public static int mousex, mousey;
	public static  ArrayList<Object> boxes = new ArrayList<Object>();
	public static  Rectangle[] slots = new Rectangle[2];

	public static final int PAPERMONEYWIDTH = 223;
	public static final int PAPERMONEYHEIGHT = 95;
	public static final int PAPERMONEYGAP = 6;
	public static int PAPERMONEYX;
	public static int PAPERMONEYY;

	public static int COINSX;
	public static int COINSY;
	public static int COINSCENTERY;
	public static final int COINWIDTH = 111;
	public static final int COINSHEIGHT = 60;

	public static final int BUTTONSX = 463;
	public static final int BUTTONSY = 230;
	public static final int BUTTONWIDTH = 6;
	public static final int BUTTONHEIGHT = 5;
	
	private static final String NEWLINE = System.getProperty("line.separator");
	private static int width, height;
	private static BufferedImage image, image2, paperMoneyImage, coinsImage;
	private static Color colorGtaGreen, colorLCDBlue, colorWhite, colorBlack, colorBrightGreen, colorRed;	
	private static  Font gtaFont;
	private static final float gtaFontHeight = 30f;
	private static Font lcdFont;
	private static final float lcdFontHeight = 10f;
	private static boolean greenOn;
	private static long referenceTime, startTime, time; 
	private static int lcdIndex;

	public VendingMachineCanvas(final VendingMachine machine, Customer customer, int width, int height) throws Exception{
		this.width = width;
		this.height = height;
		this.machine = machine;
		this.customer = customer;

		image = ImageIO.read(new File("vmd/vm.jpg"));
		image2 = ImageIO.read(new File("vmd/person.png"));
		paperMoneyImage = ImageIO.read(new File("vmd/papermoney.png"));
		coinsImage = ImageIO.read(new File("vmd/coins.jpg"));
		gtaFont = Font.createFont(Font.TRUETYPE_FONT, new File("vmd/pricedown.ttf"));
		gtaFont = gtaFont.deriveFont(gtaFontHeight);
		colorGtaGreen = new Color(0x008000);
		lcdFont = Font.createFont(Font.TRUETYPE_FONT, new File("vmd/DS-DIGI.TTF"));
		lcdFont = lcdFont.deriveFont(lcdFontHeight);
		colorLCDBlue = new Color(0x34DDDD);
		colorWhite = new Color(0xffffff);
		colorBlack = new Color(0x000000);
		colorRed = new Color(0x800000);

		colorBrightGreen = new Color(0x00ff00);



		MouseListener m = new MouseListener(this);
		addMouseWheelListener(m);
		addMouseListener(m);
		addMouseMotionListener(m);
		startTime = referenceTime = time = (new Date()).getTime();
		Thread t = new Thread(){
			@Override
				public void run(){
					while(true)
						try{
							sleep(500);
							time = (new Date()).getTime() - startTime + referenceTime;
							greenOn = !greenOn;
							lcdIndex++;
							machine.incrementErrorTime();
							repaint();
						}catch(Exception e){}
				}


		};
		t.start();
		PAPERMONEYX = this.width - paperMoneyImage.getWidth(null);
		PAPERMONEYY = 50;
		COINSX = this.width - coinsImage.getWidth(null);
		COINSY = this.height - coinsImage.getHeight(null);
		COINSCENTERY = COINSY + coinsImage.getHeight(null) / 2;
		setMoneyBoxes();
		setCoinBoxes();
		setMoneySlots();
		setButtonBoxes();
	}
	public void paint(Graphics g){
		Graphics2D g2d = (Graphics2D)g;
		g.setColor(colorWhite);
		g.fillRect(0, 0, width, height);
		//draw main background / machine
		float ratio = image.getWidth(null) / (float)image.getHeight(null);
		int width = (int)(this.height * ratio);
		int floor = 60;
		g.drawImage(image,
				0, 0, width, this.height,
				0, 0, image.getWidth(null), image.getHeight(null),
				null);


		//blinking green lights
		if(greenOn){
			g.setColor(colorBrightGreen);
			g.drawRect(492, 234, 2, 2);
			g.setColor(colorBrightGreen);
			g.drawRect(492, 203, 2, 2);
		}
		//draw zoom
		if(1==1){
			int x = Math.max(mousex - 100, 0);
			int y = Math.max(mousey - 100, 0);
			int x2 = (int)Math.max(mousex * (image.getWidth(null) / (float)width)  - 100, 0);
			int y2 = (int)Math.max(mousey * (image.getHeight(null) / (float)this.height)  - 100, 0);
			g.drawImage(image,
					x, y, x + 200, y + 200,
					x2, y2, x2 + 200, y2 + 200,
					null);
			Rectangle2D rect = new Rectangle2D.Float();
			//ellipse.setFrame(x, y, ew, eh);
			//g2.setClip(ellipse);
			rect.setRect(x, y, 200, 200);
			float zoomRatio = image.getWidth(null) / (float)width;
			g.setClip(rect);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			writeMachineLCD(g2d, zoomRatio);
			//blinking green lights
			if(greenOn){
				g.drawRect(mousex + (int)((492 - mousex) * zoomRatio), mousey + (int)((234 - mousey) * zoomRatio), 2, 2);
				g.drawRect(mousex + (int)((488 - mousex) * zoomRatio), mousey + (int)((230 - mousey) * zoomRatio), 10, 40);


				g.drawRect(mousex + (int)((473 - mousex) * zoomRatio), mousey + (int)((206 - mousey) * zoomRatio), 2, 1);
				g.drawRect(mousex + (int)((483 - mousex) * zoomRatio), mousey + (int)((206 - mousey) * zoomRatio), 2, 1);
				g.drawRect(mousex + (int)((464 - mousex) * zoomRatio), mousey + (int)((203 - mousey) * zoomRatio), 81, 20);

			}

		}
		g.setClip(null);
		//draw little girl
		int width_girl = (int)(this.height * (image2.getWidth(null) / (float)image2.getHeight(null)));
		g.drawImage(image2,
				0, (this.height - floor) - image2.getHeight(null), image2.getWidth(), (this.height - floor),
				0, 0, image2.getWidth(null), image2.getHeight(null),
				null);

		
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		writeCustomerBalance(g2d);
		writeClock(g2d);
		writeMachineStats(g2d);
		writeMachineStats2(g2d);
		writeLCDBig(g2d);

		//draw money
		g.drawImage(paperMoneyImage,
				PAPERMONEYX, PAPERMONEYY, this.width, PAPERMONEYY + paperMoneyImage.getHeight(null),
				0, 0, paperMoneyImage.getWidth(null), paperMoneyImage.getHeight(null),
				null);
		g.drawImage(coinsImage,
				COINSX, COINSY, this.width, this.height,
				0, 0, coinsImage.getWidth(null), coinsImage.getHeight(null),
				null);
		//draw mouseText
		writeMouseText(g2d);


	}
	private void writeCustomerBalance(Graphics2D g){
		int fontSize = (int)gtaFontHeight;
		g.setColor(colorGtaGreen);	
		Font font = gtaFont.deriveFont((float)fontSize);
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics(font);		
		String string = "$"+cashFormat(customer.getBalance());
		int fontWidth = metrics.stringWidth(string);
		g.drawString(string, this.width - fontWidth - 10, gtaFontHeight - 10);
	}

	private void writeClock(Graphics2D g){
		int fontSize = (int)gtaFontHeight;
		g.setColor(colorBlack);	
		Font font = gtaFont.deriveFont((float)fontSize);
		g.setFont(font);			
		FontMetrics metrics = g.getFontMetrics(font);
		Date date = new Date(time);
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String string = formatter.format(date);		
		int fontWidth = metrics.stringWidth(string);
		g.drawString(string, this.width - fontWidth - 10, gtaFontHeight*2 - 10);
	}

	private void writeLCDBig(Graphics2D g){
		int fontSize = (int)gtaFontHeight;
		g.setColor(colorBlack);	
		Font font = gtaFont.deriveFont((float)fontSize);
		g.setFont(font);			
		FontMetrics metrics = g.getFontMetrics(font);
		String string = machine.getLCD();		
		int fontWidth = metrics.stringWidth(string);
		g.drawString(string,  360 - fontWidth / 2, this.height - 30);
	}

	private void writeMachineStats(Graphics2D g){
		int fontSize = 14;
		g.setColor(colorBlack);
		Font font = lcdFont.deriveFont((float)fontSize);
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics(font);
		String string = "Quarters: "+machine.quarters+"   Dimes: "+machine.dimes+"   Nickels: "+machine.nickels+
			"   Pennies: "+machine.pennies+"   Total: $"+cashFormat(machine.getMachineTotal());
		int fontWidth = metrics.stringWidth(string);
		g.drawString(string, 360 - fontWidth / 2, 30);

	}

	private void writeMachineStats2(Graphics2D g){
		int fontSize = 17;
		g.setColor(colorBlack);
		g.setFont(gtaFont.deriveFont((float)fontSize + 4));
		g.drawString("Inventory:", 5, 20);
		g.setFont(gtaFont.deriveFont((float)fontSize));
		int i = 1;					
		for(Drink drink : machine.drinks){
			if(drink.outOfStock())
				g.setColor(colorRed);
			else
				g.setColor(colorBlack);
			g.drawString(drink.toString(), 5, 20 + fontSize * (i++));
		}

	}

	private void writeMouseText(Graphics2D g){
		int fontSize = 18;
		g.setColor(colorGtaGreen);
		Font font = gtaFont.deriveFont((float)fontSize);
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics(font);
		String string = mouseText;
		int fontWidth = metrics.stringWidth(string);
		g.drawString(string, mousex - fontWidth / 2, mousey + gtaFontHeight / 2);
	}

	private void writeMachineLCD(Graphics2D g, float zoomRatio){
		g.setFont(lcdFont);
		g.setColor(colorLCDBlue);
		String string = machine.getLCD();
		if(string.length() > 12){
			int begin = lcdIndex % string.length();
			int end = Math.min(string.length(), begin + 12);
			string = string.substring(begin, end);
		}
		g.drawString(string, mousex + (int)((469 - mousex) * zoomRatio), mousey + (int)((155 - mousey) * zoomRatio));

	}

	public static String cashFormat(double d){
		DecimalFormat twoDForm = new DecimalFormat("0.00");
		return (twoDForm.format(d));
	}

	private void setMoneyBoxes(){
		boxes.add(new RectangleBox(PAPERMONEYX, PAPERMONEYY, 100));
		boxes.add(new RectangleBox(this.width - PAPERMONEYWIDTH, PAPERMONEYY, 50));
		boxes.add(new RectangleBox(PAPERMONEYX, PAPERMONEYY + PAPERMONEYHEIGHT + PAPERMONEYGAP, 20));
		boxes.add(new RectangleBox(this.width - PAPERMONEYWIDTH, PAPERMONEYY + PAPERMONEYHEIGHT + PAPERMONEYGAP, 10));
		boxes.add(new RectangleBox(PAPERMONEYX, PAPERMONEYY + PAPERMONEYHEIGHT * 2 + PAPERMONEYGAP * 2, 5));
		boxes.add(new RectangleBox(this.width - PAPERMONEYWIDTH, PAPERMONEYY + PAPERMONEYHEIGHT * 2 + PAPERMONEYGAP * 2, 2));
		boxes.add(new RectangleBox(PAPERMONEYX + (paperMoneyImage.getWidth(null) - PAPERMONEYWIDTH) / 2, PAPERMONEYY + PAPERMONEYHEIGHT * 3 + PAPERMONEYGAP * 3, 1));
	}

	private void setCoinBoxes(){
		boxes.add(new EllipseBox(COINSX+77, COINSCENTERY, 55 , 0.25));
		boxes.add(new EllipseBox(COINSX+188, COINSCENTERY, 40 , 0.10));
		boxes.add(new EllipseBox(COINSX+298, COINSCENTERY, 48 , 0.05));
		boxes.add(new EllipseBox(COINSX+410, COINSCENTERY, 42 , 0.01));		
		//boxes.add(new EllipseBox(COINSX+COINGAP*2+COINWIDTH, COINSY, COINWIDTH, COINWIDTH , 0.10));

		//boxes.add((RectangularShape)(new EllipseBox(COINSX + COINWIDTH / 2, COINWIDTH / 2, COINWIDTH, COINWIDTH , 0.25)));
	}

	private void setButtonBoxes(){
		boxes.add(new RectangleBox(BUTTONSX, BUTTONSY, 'A'));
		boxes.add(new RectangleBox(BUTTONSX, BUTTONSY+BUTTONHEIGHT, 'B'));
		boxes.add(new RectangleBox(BUTTONSX, BUTTONSY+BUTTONHEIGHT*2, 'C'));
		boxes.add(new RectangleBox(BUTTONSX, BUTTONSY+BUTTONHEIGHT*3, 'D'));
		boxes.add(new RectangleBox(BUTTONSX, BUTTONSY+BUTTONHEIGHT*4, 'E'));
		boxes.add(new RectangleBox(BUTTONSX, BUTTONSY+BUTTONHEIGHT*5, 'F'));

		boxes.add(new RectangleBox(BUTTONSX+BUTTONWIDTH, BUTTONSY, '1'));
		boxes.add(new RectangleBox(BUTTONSX+BUTTONWIDTH, BUTTONSY+BUTTONHEIGHT, '3'));
		boxes.add(new RectangleBox(BUTTONSX+BUTTONWIDTH, BUTTONSY+BUTTONHEIGHT*2, '5'));
		boxes.add(new RectangleBox(BUTTONSX+BUTTONWIDTH, BUTTONSY+BUTTONHEIGHT*3, '7'));
		boxes.add(new RectangleBox(BUTTONSX+BUTTONWIDTH, BUTTONSY+BUTTONHEIGHT*4, '9'));
		boxes.add(new RectangleBox(BUTTONSX+BUTTONWIDTH, BUTTONSY+BUTTONHEIGHT*5, '*'));

		boxes.add(new RectangleBox(BUTTONSX+BUTTONWIDTH*2, BUTTONSY, '2'));
		boxes.add(new RectangleBox(BUTTONSX+BUTTONWIDTH*2, BUTTONSY+BUTTONHEIGHT, '4'));
		boxes.add(new RectangleBox(BUTTONSX+BUTTONWIDTH*2, BUTTONSY+BUTTONHEIGHT*2, '6'));
		boxes.add(new RectangleBox(BUTTONSX+BUTTONWIDTH*2, BUTTONSY+BUTTONHEIGHT*3, '8'));
		boxes.add(new RectangleBox(BUTTONSX+BUTTONWIDTH*2, BUTTONSY+BUTTONHEIGHT*4, '0'));
		boxes.add(new RectangleBox(BUTTONSX+BUTTONWIDTH*2, BUTTONSY+BUTTONHEIGHT*5, '!'));

		//refund button
		boxes.add(new RectangleBox(488, 250, '-'));


	}

	private void setMoneySlots(){
		slots[0] = new Rectangle(464, 203, 24, 7);
		slots[1] = new Rectangle(488, 230, 5, 13);

	}




}
