package courier.exceptions;

public class InvalidStatusTransitionException extends Exception{
    public InvalidStatusTransitionException(String Message){
        super(Message);
    }
}
