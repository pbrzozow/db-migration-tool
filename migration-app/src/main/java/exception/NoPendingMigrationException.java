package exception;

public class NoPendingMigrationException extends RuntimeException {
    public NoPendingMigrationException(String message) {
        super(message);
    }
}
