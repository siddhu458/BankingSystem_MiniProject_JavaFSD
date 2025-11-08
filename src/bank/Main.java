package bank;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
	private static final Scanner sc = new Scanner(System.in);
	private static final Bank bank = new Bank();
	public static void main(String[] args) {
		System.out.println("===Banking System===");
		boolean running = true;
		
		while(running) {
			try {
				switch(mainMenu()) {
				case 1 -> createAccountFlow();
				case 2 -> existingAccountFlow();
				case 3 -> searchByNameFlow();
				case 4 -> simulateConcurrencyFlow();
				case 5 -> { running = false; System.out.println("Goodbye!"); }
				default -> System.out.println("Invalid choice. Try again.");
				}
			} catch(InputMismatchException ime) {
				System.out.println("Invalid input type. Please enter numbers where excepted.");
				sc.nextLine();
			} catch (Exception e) {
				System.out.println("Unexpected error: "+e.getMessage());
			}
		}
	}
	
	private static void simulateConcurrencyFlow() {
		 System.out.println("Creating a demo account for concurrency test...");
	        Account demo;
	        try {
	            demo = bank.createAccount("Concurrency Demo");
	            bank.deposit(demo.getAccountNumber(), 1000.0);
	        	} catch (Exception e) {
	            System.out.println("Setup failed: " + e.getMessage());
	            return;
	        }

	        String accNo = demo.getAccountNumber();
	        System.out.println("Demo Account -> " + demo);

	        Thread t1 = new Thread(() -> runDeposits(accNo, 100, 10));
	        Thread t2 = new Thread(() -> runWithdrawals(accNo, 50, 10));
	        Thread t3 = new Thread(() -> runDeposits(accNo, 20, 25));
	        Thread t4 = new Thread(() -> runWithdrawals(accNo, 10, 25));

	        t1.start(); t2.start(); t3.start(); t4.start();
	        try { t1.join(); t2.join(); t3.join(); t4.join(); } catch (InterruptedException ignored) {}

	        try {
	            System.out.println("Final balance (thread-safe): " +
	                    String.format("%.2f", bank.get(accNo).getBalance()));
	        } catch (AccountNotFoundException e) {
	            System.out.println("Error: " + e.getMessage());
	        }
	}
	
	private static void searchByNameFlow() {
		 System.out.print("Enter part of the holder name to search: ");
	        String q = sc.nextLine();
	        List<Account> results = bank.searchByName(q);
	        if (results.isEmpty()) {
	            System.out.println("No accounts matched.");
	        } else {
	            System.out.println("Matches:");
	            results.forEach(System.out::println);
	        }
	}
	
	private static void existingAccountFlow() {
		System.out.println("Enter account number: ");
		String accNo = sc.nextLine().trim();
		
		if(!bank.exists(accNo)) {
			System.out.println("No account with number: "+accNo);
			return;
		}
		boolean back = false;
		while(!back) {
			System.out.println("\nAccount Menu [" + accNo + "]");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer");
            System.out.println("4. Show balance");
            System.out.println("5. Back to main");
            System.out.print("Choose: ");
            
            int ch = safeNextInt();
            switch (ch) {
            case 1 -> {
                System.out.print("Amount to deposit: ");
                double amt = safeNextDouble();
                try {
                    bank.deposit(accNo, amt);
                    System.out.println("Deposited. New balance: " +
                            String.format("%.2f", bank.get(accNo).getBalance()));
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            case 2 -> {
                System.out.print("Amount to withdraw: ");
                double amt = safeNextDouble();
                try {
                    bank.withdraw(accNo, amt);
                    System.out.println("Withdrawn. New balance: " +
                            String.format("%.2f", bank.get(accNo).getBalance()));
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            case 3 -> {
                System.out.print("Destination account number: ");
                String to = sc.nextLine().trim();
                System.out.print("Amount to transfer: ");
                double amt = safeNextDouble();
                try {
                    bank.transfer(accNo, to, amt);
                    System.out.println("Transferred. Source new balance: " +
                            String.format("%.2f", bank.get(accNo).getBalance()));
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            case 4 -> {
                try {
                    Account a = bank.get(accNo);
                    System.out.println(a);
                } catch (AccountNotFoundException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            case 5 -> back = true;
            default -> System.out.println("Invalid choice.");
            }
		}
	}
	
	private static void createAccountFlow() {
		System.out.println("Enter account holder name: ");
		String name = sc.nextLine().trim();
		try {
			Account acc = bank.createAccount(name);
			System.out.println("Account created -> "+acc);
		} catch(InvalidNameException ine) {
			 System.out.println("Error: " + ine.getMessage());
		}
	}
	
	private static int mainMenu() {
		System.out.println("\nMain Menu");
		System.out.println("1) Create an account");
		System.out.println("2) Operating on an existing account");
		System.out.println("3) Search accounts by holder name");
		System.out.println("4) Simulate concurrent operations");
		System.out.println("5) Exit");
		System.out.println("Choose: ");
		return safeNextInt();
	}
	
	 private static void runDeposits(String accNo, double amount, int times) {
	        for (int i = 0; i < times; i++) {
	            try { bank.deposit(accNo, amount); } catch (Exception ignored) {}
	        }
	    }

	    private static void runWithdrawals(String accNo, double amount, int times) {
	        for (int i = 0; i < times; i++) {
	            try { bank.withdraw(accNo, amount); } catch (Exception ignored) {}
	        }
	    }

	    // --- Safe numeric input helpers ---

	    private static int safeNextInt() {
	        while (true) {
	            try {
	                int v = Integer.parseInt(sc.nextLine().trim());
	                return v;
	            } catch (NumberFormatException e) {
	                System.out.print("Please enter a valid integer: ");
	            }
	        }
	    }
	    
	    private static double safeNextDouble() {
	        while (true) {
	            try {
	                double v = Double.parseDouble(sc.nextLine().trim());
	                return v;
	            } catch (NumberFormatException e) {
	                System.out.print("Please enter a valid number: ");
	            }
	        }
	    }
	}
