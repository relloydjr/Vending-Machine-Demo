package vmd;
public class Drink{
	String name;
	int quantity;
	double price;
	String[] codes;

	public Drink(String name, double price, int quantity, String... codes){
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.codes = codes;
	}

	public double getPrice(){
		return price;
	}

	public boolean outOfStock(){
		return quantity == 0;
	}

	public boolean codeMatch(String s){
		for(int i = 0; i < codes.length; i++)
			if(codes[i].equals(s))
				return true;
		return false;
	}

	public String toString(){
		return name+": "+quantity;
	}
}

