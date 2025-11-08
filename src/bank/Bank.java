package bank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Bank {
	private final Map<String, Account> accounts = new HashMap<>();
	
public synchronized Account createAccount(String holderName)throws InvalidNameException {
	String cleaned = holderName == null ? "" : holderName.trim();
	if(cleaned.isEmpty()) throw new InvalidNameException("Name cannot be empty.");
	
	String accNo;
	do {
		accNo = generateAccountNumber(cleaned);
	}while(accounts.containsKey(accNo));
	
	Account acc = new SavingsAccount(accNo, cleaned);
	accounts.put(accNo, acc);
	return acc;
}

public synchronized boolean exists(String accountNUmber) {
	return accounts.containsKey(accountNUmber);
}

public synchronized Account get(String accountNumber) throws AccountNotFoundException{
	Account acc = accounts.get(accountNumber);
	if(acc == null) throw new AccountNotFoundException("Account not found"+accountNumber);
	return acc;
}

public List<Account> searchByName(String namePart){
	String needie = namePart == null ? "" : namePart.trim().toLowerCase();
	return accounts.values().stream()
			.filter(a -> a.getHolderName().toLowerCase().contains(needie))
			.sorted(Comparator.comparing(Account::getHolderName)
			.thenComparing(Account::getAccountNumber))
			.collect(Collectors.toList());

}

public void deposit(String accountNumber, double amount)
	throws AccountNotFoundException, InvalidAmountException{
	Account acc = get(accountNumber);
	acc.deposit(amount);
}

public void withdraw(String accountNumber, double amount)
		throws AccountNotFoundException, InvalidAmountException, InsufficientBalanceException{
		Account acc = get(accountNumber);
		acc.withdraw(amount);
}

public void transfer(String fromAcc, String toAcc, double amount)
		throws AccountNotFoundException, InvalidAmountException, InsufficientBalanceException{ 
	if(fromAcc.equals(toAcc))
		throw new InvalidAmountException("Source and destination cannot be the same.");
		
		Account src = get(fromAcc);
		Account des = get(toAcc);
		

        // Deadlock-safe ordering: lock on the lower identity hash first
		Object first = System.identityHashCode(src) < System.identityHashCode(des) ? src : des;
		Object second = (first == src) ? des : src;
		
		synchronized (first) {
			synchronized(second) {
				src.withdraw(amount);
				try {
					des.deposit(amount);
				} catch(InvalidAmountException e) {
					try {src.deposit(amount);} catch(InvalidAmountException ex)
						{/* ignore */}	
					throw e;
					
				}
			}
		}
	}

	
	private String generateAccountNumber(String name) {
		String initials = Arrays.stream(name.trim().split("\\s+"))
				.filter(s -> !s.isEmpty())
				.map(s -> s.substring(0,1).toUpperCase())
				.collect(Collectors.joining());
		int rnd = ThreadLocalRandom.current().nextInt(1000, 10000);
		return (initials.isEmpty() ? "AC" : initials) + "-" + rnd;
	}
	
	   public synchronized Collection<Account> allAccounts() {
	        return new ArrayList<>(accounts.values());
	    }
}
