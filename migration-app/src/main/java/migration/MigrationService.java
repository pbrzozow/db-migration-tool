package migration;

import database.DataSourceProvider;
import exception.NoPendingMigrationException;
import exception.UndoMigrationNotFoundException;
import model.Migration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class MigrationService {
    private static final Logger logger
            = LoggerFactory.getLogger(MigrationService.class);

    private final MigrationLog migrationLog;
    private final MigrationValidator migrationValidator;

    public MigrationService( MigrationLog migrationLog,MigrationValidator migrationValidator) {
        this.migrationLog = migrationLog;
        this.migrationValidator = migrationValidator;
    }
    public void executeNextMigration() throws NoPendingMigrationException{
        Optional<Migration> currentMigration = migrationLog.getCurrentMigration();
        if (currentMigration.isPresent()) {
            executeMigration(currentMigration.get());
        } else {
            throw new NoPendingMigrationException("There are no available migrations! ");
        }
    }

    public void executeMigration(Migration migration) {
        try (Connection connection = DataSourceProvider.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            try {
                migrationValidator.validate(migration);
                executeMigration(migration, connection);
                migrationLog.saveMigration(migration, connection);
                connection.commit();
                migrationLog.deleteCurrentMigration();
            } catch (SQLException e) {
                connection.rollback();
                logger.error("Error occurred during migration execution, rolled back transaction",e);
            }
        } catch (SQLException e) {
            logger.error("Database connection error during migration execution", e);
        }
    }

    public void rollbackMigrations(String rollbackId){
        long target = Long.parseLong(rollbackId);
        try (Connection connection = DataSourceProvider.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            try {
            long lastMigrationIndex = migrationLog.fetchLastMigrationIndex();
            while (lastMigrationIndex != target-1) {
                rollback(String.valueOf(lastMigrationIndex), connection);
                migrationLog.deleteMigrationInfo(lastMigrationIndex,connection);
                lastMigrationIndex--;
            }
            migrationLog.updatePendingMigrations();
            connection.commit();
            }catch (Exception e){
                connection.rollback();
                logger.error("Error occurred during rollback execution, transaction rolled back",e);
            }
    } catch (SQLException e) {
            logger.error("Database connection error during rollback execution", e);
        }
    }

    private void rollback(String id,Connection connection) throws SQLException,UndoMigrationNotFoundException {
        Optional<Migration> undoMigration = migrationLog.getUndoMigration(id);
        if (undoMigration.isPresent()){
            executeMigration(undoMigration.get(),connection);
        }else {throw new UndoMigrationNotFoundException("Undo migration with id: "+ id +" does not exist");}
    }


    private void executeMigration(Migration migration, Connection connection) throws SQLException {
        logger.info("Executing migration...");
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(migration.getSql());
        }
        logger.info("Migration executed");
    }
}
