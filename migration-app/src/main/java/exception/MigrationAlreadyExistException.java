package exception;

public class MigrationAlreadyExistException extends RuntimeException {
    public MigrationAlreadyExistException(String message){
        super(message);
    }
}
