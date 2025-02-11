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
            try {
                migrationService.executeNextMigration();
            }catch (NoPendingMigrationException e){
                System.out.println("There are no available migrations");
            }
        }
    }
    public void rollback(String id){
        synchronized (this) {
            try{
                migrationService.rollbackMigrations(id);
            }catch (UndoMigrationNotFoundException e){
                System.out.println("Cannot execute rollbacks, error occurred");
            }
        }
    }

    public void showHistory(){
        String migrationHistory = migrationLog.getMigrationHistory();
        System.out.println(migrationHistory);
    }

}
