Vending Machine Demo:
----------------------

To run:
-----------------
    java -jar vmd.jar

Upon running, the Vending Machine will randomly set the number of drinks for each item.
The Vending Machine will also set a random number of quarters, nickels, dimes, pennies, and dollars it already has.

If the machine is unable to make change for all possible amounts under $20 (incremented by 5 cents, i.e. $0.05, $0.10, ... $19.90, $19.95)
the machine will be 'Out of Service' and you will have to re-run the program.

Drag and drop bills and coins to the appropriate slot on the machine.

The Keypad works and you must use it to select a drink.

The refund button works so use it to cancel your order.

Functionality:
---------------------------------
The Machine can take in dollars and coins from a customer. The machine will not take in bills larger than $20. The machine will not take more than $20 per transaction (if you give it a $20 and then another dollar, the machine refuses the other dollar).

The Machine reads user input from the keypad. The code for the drink can be entered on the keypad. If the user presses '*', the input is cleared. If the user presses 'O.K.', the machine will:

	a. Vend the drink if enough money has been given to buy the drink. Change is then given back to the customer.
	b. Display the price of the item if the customer didn't put enough money into the machine.
	c. Display an 'Invalid Item' error if the code entered has no matching drink.
	d. Display an 'Out of Stock' error if the corresponding drink is out of stock.

The Machine will dispense coins back to the customer if change is due or the customer cancels their order. The change given back uses a non greedy recursive algorithm to determine the best combination of coins that would reslut in the least number of coins dispensed. The algorithm looks at all possible combinations and never looks as the same combination twice as it recursively looks for a solution until a max depth (given by a previous solution's depth) or the target price is reached or over-shot. After change is given, the machine checks if it is 'out of service' again. 
