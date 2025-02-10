package exception;

public class UndoMigrationNotFoundException extends RuntimeException {
    public UndoMigrationNotFoundException(String message) {
        super(message);
    }
}
