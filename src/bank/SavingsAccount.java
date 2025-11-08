package bank;

public class SavingsAccount extends Account{
	private static final double MINIMUM_BALANCE = 100.0;
	
	public SavingsAccount(String accountNumber, String holderName){
		super(accountNumber, holderName, 0.0);
	}
	
	   @Override
	public synchronized void withdraw(double amount) throws InvalidAmountException, InsufficientBalanceException{
		if(amount <= 0) throw new InvalidAmountException("Amount must be positive.");
		if(amount > balance) throw new InsufficientBalanceException("Insufficient balance.");
		if((balance - amount) < MINIMUM_BALANCE) {
			throw new InsufficientBalanceException(
					"Cannot go below minimum balance of "+MINIMUM_BALANCE);
		}
		balance -= amount;
	}
}
