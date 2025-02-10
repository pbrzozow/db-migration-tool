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
        Optional<Migration> currentMigration = migrationLog.getCurrentMigration();
        if (currentMigration.isPresent()){
           migrationService.executeMigration(currentMigration.get());
        } else {
            throw new NoPendingMigrationException("There are no available migrations! ");
        }
    }

    public void rollback(String id){
            Optional<Migration> undoMigration = migrationLog.getUndoMigration(id);
            if (undoMigration.isPresent()){
                migrationService.executeMigration(undoMigration.get());
            }else {throw new UndoMigrationNotFoundException("Undo migration with id: "+ id +" does not exist");}
    }



    public void showHistory(){
        String migrationHistory = migrationLog.getMigrationHistory();
        System.out.println(migrationHistory);
    }

}
