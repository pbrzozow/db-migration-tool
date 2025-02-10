import lombok.extern.slf4j.Slf4j;
import migration.MigrationFactory;
import migration.MigrationManager;

@Slf4j
public class Application {
    public static void main(String[] args) {
        MigrationManager migration = MigrationFactory.getMigrationManagerInstance();
        migration.showHistory();
        migration.rollback("5");
        migration.showHistory();
    }
}
