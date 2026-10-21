package courier.exceptions;

public class DatabaseOperationException extends Exception{
    public DatabaseOperationException(String Message){
        super(Message);
    }
    public DatabaseOperationException(String Message,Throwable Cause){
        super(Message,Cause);
    }
}
