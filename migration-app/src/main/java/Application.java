import migration.MigrationFactory;
import migration.MigrationManager;

public class Application {
    private static final String FOLDER_PATH = "src/main/resources/db";

    public static void main(String[] args) {
        MigrationManager manager = MigrationFactory.getMigrationManagerInstance(FOLDER_PATH);
//        example
        manager.showHistory();
        manager.migrate();
        manager.migrate();

        manager.showHistory();
        manager.rollback("2");
        manager.showHistory();
    }
}
