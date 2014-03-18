package vmd;

public class Customer{
	double balance = 250000;

	public void transact(double amount){
		this.balance += amount;
	}
	
	public double getBalance(){
		return balance;
	}
}
