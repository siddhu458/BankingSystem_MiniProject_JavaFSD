package bank;

public abstract class Account {
	private final String accountNumber;
	private String holderName;
	protected double balance;
	protected Account(String accountNumber, String holderName, double initialBalance){
		this.accountNumber = accountNumber;//The this keyword is a reference variable that refers to the current object being created.
		this.holderName = holderName;
		this.balance = initialBalance;
		
		
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public String getHolderName() {
		return holderName;
	}
	public synchronized double getBalance() {
		return balance;
	}
	
	public synchronized void deposit(double amount) throws InvalidAmountException {
		if(amount <= 0) throw new InvalidAmountException("Amount must be positive.");
		balance += amount;
	}
	
	public synchronized void withdraw(double amount) throws InvalidAmountException, InsufficientBalanceException{
		if(amount <= 0) throw new InvalidAmountException("Amount must be positive.");
		if(amount > balance) throw new InsufficientBalanceException("Insufficient balance.");
		// Base account has no min-balance rule; subclasses may override
        balance -= amount;
	}
	@Override
	public String toString() {
		return "Account [accountNumber=" + accountNumber + ", holderName=" + holderName + ", balance=" + balance + "]";
	}
	
}
