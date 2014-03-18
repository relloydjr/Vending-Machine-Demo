package vmd;
import java.util.ArrayList;
public class VendingMachine{
	public static int pennies, nickels, dimes, quarters, dollars;
	public static ArrayList<Drink> drinks = new ArrayList<Drink>();
	private static double transaction;
	private static final int MAXCOINS = 100;
	private static final int MAXDRINKS = 15;
	private static String LCDCURRENT;
	private static final String LCDDEFAULT = " - Insert Money (drag & drop)- ";
	private static String LCDMONEY = "";
	private static String LCDBUTTONS = "";
	private static String LCDERROR = "";
	private static int tempError = 0;
	boolean outOfService = false;


	public VendingMachine(){
		LCDCURRENT = LCDDEFAULT;
		randomizeMachine();
		checkService();
	}

	public void press(Customer customer, char c){
		if(outOfService)
			return;
		switch(c){
			case '*':clearButtons();break;
			case '-':refund(customer);break;
			case '!':buy(customer);break;
			default:appendButtonText(c);
		}
	}

	public void incrementErrorTime(){
		this.tempError++;
	}
	
	private void checkService(){
		int check = 5;
		while(check <= 2000){
			if(getChange(check/100.0, true) == null){
				outOfService = true;
				return;
			}
			check+=5;
		}
		outOfService = false;
		/*if(pennies < 5 || nickels < 1 || dimes < 2 || quarters < 3)//0-100 in change
			outOfService = true;
		else
			outOfService = false;*/
	}


	public void insertMoney(Customer customer, double amount){//one item at a time
		LCDERROR = "";
		if(outOfService){
			customer.transact(amount);
			return;
		}
		if(this.transaction + amount > 20){
			if(amount > 20)
				setError("No Bills Over 20");
			else
				setError("$20 MAX");
			customer.transact(amount);
			return;
		}
		this.transaction += amount;
		if(amount >= 1)
			this.dollars += (int)amount;
		else{
			switch((int)(amount*100)){
				case 1: this.pennies++;break;
				case 5: this.nickels++;break;
				case 10: this.dimes++;break;
				case 25: this.quarters++;break;
			}
		}
		LCDCURRENT = LCDMONEY = "$"+VendingMachineCanvas.cashFormat(this.transaction);					
	}

	public String getLCD(){
		if(LCDERROR.length() > 0 && tempError < 10)
			return LCDERROR;
		else if(outOfService)
			return "Out of Service";
		else
			return LCDCURRENT;
	}

	public double getMachineTotal(){
		return (dollars + 0.25 * quarters + 0.10 * dimes + 0.05 * nickels + 0.01 * pennies);
	}

	public void appendButtonText(char s){
		LCDERROR = "";
		LCDBUTTONS+=s;
		LCDCURRENT = LCDBUTTONS;
	}

	public void clearButtons(){
		LCDBUTTONS = "";
		if(LCDMONEY.length() > 0)
			LCDCURRENT = LCDMONEY;
		else
			LCDCURRENT = LCDDEFAULT;
	}

	public void refund(Customer c){
		int[] config = getChange(transaction, false);
		double change = transaction;
		int quartersLoss = quarters - config[0];
		transaction -= quartersLoss * 0.25;
		quarters -= quartersLoss;
		c.transact(quartersLoss * 0.25);
		
		int dimesLoss = dimes - config[1];
		transaction -= dimesLoss * 0.10;
		dimes -= dimesLoss;
		c.transact(dimesLoss * 0.10);
		
		int nickelsLoss = nickels - config[2];
		transaction -= nickelsLoss * 0.05;
		nickels -= nickelsLoss;
		c.transact(nickelsLoss * 0.05);

		int penniesLoss = pennies - config[3];
		transaction -= penniesLoss * 0.01;
		pennies -= penniesLoss;
		c.transact(penniesLoss * 0.01);
		
		//System.out.println("q: "+quartersLoss+"\td: "+dimesLoss+"\tn: "+nickelsLoss+"\tp: "+penniesLoss);

		this.transaction = 0;//force
		LCDMONEY = "";
		setError("Change: $"+VendingMachineCanvas.cashFormat(change));
		checkService();
	}

	private int[] getChange(double change, boolean instant){
		return r(quarters, dimes, nickels, pennies, (int)(change*100), 0, Integer.MAX_VALUE, true, true, true, true, instant);
	}
	private int[] r(int q, int d, int n, int p, int target, int coins, int maxDepth, boolean qq, boolean dd, boolean nn, boolean pp, boolean instantReturn){
		if(target == 0){
			return new int[]{q, d, n, p, coins};
		}
		if(target < 0 || coins >= maxDepth)
			return null;

		int[] result = null;
		int[] toReturn = null;

		if(qq && q > 0 && (q * 2500 + d * 1000 + n * 500 + p * 100) - target >= 0 && Math.ceil(target/2500.0) + coins < maxDepth)
			result = r(q - 1, d, n, p, target - 25, coins + 1, maxDepth, true, true, true, true, instantReturn);
		if(result != null){
			if(result[4] < maxDepth){
				maxDepth = Math.min(maxDepth, result[4]);
				toReturn = result;
				if(instantReturn)
					return toReturn;
			}
		}

		if(dd && d > 0 && (d * 1000 + n * 500 + p * 100) - target >= 0 && Math.ceil(target/1000.0) + coins < maxDepth)
			result = r(q, d - 1, n, p, target - 10, coins + 1, maxDepth, false, true, true, true, instantReturn);
		if(result != null){
			if(result[4] < maxDepth){
				maxDepth = Math.min(maxDepth, result[4]);
				toReturn = result;
				if(instantReturn)
					return toReturn;

			}
		}

		if(nn && n > 0 && (n * 500 + p * 100) - target >= 0 && Math.ceil(target/500.0) + coins < maxDepth)
			result = r(q, d, n - 1, p, target - 5, coins + 1, maxDepth, false, false, true, true, instantReturn);
		if(result != null){
			if(result[4] < maxDepth){
				maxDepth = Math.min(maxDepth, result[4]);
				toReturn = result;
				if(instantReturn)
					return toReturn;

			}
		}

		if(pp && p > 0 && p*100 - target >= 0 && (target/100.0) + coins < maxDepth)
			result = r(q, d, n, p - 1, target - 1, coins + 1, maxDepth, false, false, false, true, instantReturn);	
		if(result != null){
			if(result[4] < maxDepth){
				maxDepth = Math.min(maxDepth, result[4]);
				toReturn = result;
				if(instantReturn)
					return toReturn;

			}
		}

		
		return toReturn;
	}

	public void buy(Customer customer){
		if(LCDBUTTONS.length() == 0){
			setError("Please Enter an Item Code (Use Keypad)");
			return;
		}
		else		
			for(Drink drink : drinks){
				if(drink.codeMatch(LCDBUTTONS)){
					if(drink.outOfStock())
						setError("Out of Stock");
					else if(transaction < drink.getPrice())
						setError("Price: $"+VendingMachineCanvas.cashFormat(drink.getPrice()));
					else
						vend(drink, customer);
					return;
				}
			}
		setError("Invalid Item");
	}

	private void vend(Drink drink, Customer customer){
		drink.quantity--;
		transaction -= drink.getPrice();
		refund(customer);
	}

	private void setError(String msg){
		LCDERROR = msg;
		tempError = 0;
		clearButtons();	
	}
	

	public void randomizeMachine(){
		dollars = (int)Math.round(Math.random() * MAXCOINS);		
		quarters = (int)Math.round(Math.random() * MAXCOINS);
		dimes = (int)Math.round(Math.random() * MAXCOINS);
		nickels = (int)Math.round(Math.random() * MAXCOINS);
		pennies = (int)Math.round(Math.random() * MAXCOINS);

	
		drinks.add(new Drink("Full Throttle", 	2.25, (int)Math.round(Math.random() * MAXDRINKS), "A1", "A2", "A3", "A4"));
		drinks.add(new Drink("Vitamin Water", 	2.25, (int)Math.round(Math.random() * MAXDRINKS), "A5", "A6", "A7"));
		drinks.add(new Drink("Apple Juice", 	1.50, (int)Math.round(Math.random() * MAXDRINKS), "A8", "A9"));
		drinks.add(new Drink("Coke", 		1.25, (int)Math.round(Math.random() * MAXDRINKS), "B1", "B2", "B3", "B4", "B5"));
		drinks.add(new Drink("Coke Zero", 	1.25, (int)Math.round(Math.random() * MAXDRINKS), "B6", "B7", "B8", "B9"));
		drinks.add(new Drink("Diet Coke", 	1.25, (int)Math.round(Math.random() * MAXDRINKS), "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9"));
		drinks.add(new Drink("Sprite", 		1.25, (int)Math.round(Math.random() * MAXDRINKS), "D1", "D2", "D3", "D4"));
		drinks.add(new Drink("Lemonade", 	1.00, (int)Math.round(Math.random() * MAXDRINKS), "D5", "D6", "D7"));
		drinks.add(new Drink("Sweet Tea", 	1.75, (int)Math.round(Math.random() * MAXDRINKS), "D8", "D9"));
		drinks.add(new Drink("Water", 		1.10, (int)Math.round(Math.random() * MAXDRINKS), "E1", "E2", "E3", "E4", "E5", "E6", "E7"));
		drinks.add(new Drink("Powerade", 	2.00, (int)Math.round(Math.random() * MAXDRINKS), "E8", "E9"));
	}
}
