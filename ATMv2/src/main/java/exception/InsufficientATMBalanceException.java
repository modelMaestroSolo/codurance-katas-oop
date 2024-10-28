package exception;

public class InsufficientATMBalanceException extends Exception{
    public InsufficientATMBalanceException(String message) {
        super(message);
    }
}
