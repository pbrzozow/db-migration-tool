package migration;

import exception.NoPendingMigrationException;
import exception.UndoMigrationNotFoundException;
import model.Migration;

import java.util.Optional;

public class MigrationManager {
    private final MigrationLog migrationLog;
    private final MigrationService migrationService;

    public MigrationManager(MigrationLog migrationLog,MigrationService migrationService) {
        this.migrationLog = migrationLog;
        this.migrationService = migrationService;
    }

public void migrate(){
        synchronized (this) {
            Optional<Migration> currentMigration = migrationLog.getCurrentMigration();
            if (currentMigration.isPresent()) {
                migrationService.executeMigration(currentMigration.get());
            } else {
                throw new NoPendingMigrationException("There are no available migrations! ");
            }
        }
    }
    public void rollback(String id){
        synchronized (this) {
            migrationService.rollbackMigrations(id);
        }
    }

    public void showHistory(){
        String migrationHistory = migrationLog.getMigrationHistory();
        System.out.println(migrationHistory);
    }

}
