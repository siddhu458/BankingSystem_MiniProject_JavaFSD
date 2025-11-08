package bank;

public class InvalidAmountException extends Exception{
	public InvalidAmountException(String message){
		super(message); //super() is used to explicitly call a constructor of the parent class from within a child class's constructor. 
	}
}
