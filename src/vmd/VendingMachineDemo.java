package vmd;
import javax.swing.*;
import java.awt.*;

public class VendingMachineDemo{

	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				UIManager.put("swing.boldMetal", Boolean.FALSE); 
				try{
					createGUI();
				}catch(Exception e){
					e.printStackTrace();
					System.exit(1);
				}
			}
		});

	}


	

	private static void createGUI() throws Exception{
		JFrame frame = new JFrame("The Vending Machine");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable( false );
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setPreferredSize(new Dimension(1100,(int) 600/*dim.getHeight()*/));
		frame.setLocation((int)dim.getWidth()/2 - 550,(int)(dim.getHeight()/2) - 300);
		frame.setVisible(true);
		int w = (int) (1100 - (frame.getInsets().right + frame.getInsets().left));
		int h = (int) (600 - (frame.getInsets().top + frame.getInsets().bottom));
		Customer c = new Customer();
		VendingMachine vm = new VendingMachine();
		VendingMachineCanvas vmc = new VendingMachineCanvas(vm, c, w, h);
		frame.add(vmc);
		frame.pack();
	}
}
