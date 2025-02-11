package migration;

import database.DataSourceProvider;
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

    public void executeMigration(Migration migration) {
        try (Connection connection = DataSourceProvider.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            migrationValidator.validate(migration);
            executeMigration(migration, connection);
            migrationLog.saveMigration(migration, connection);
            connection.commit();
            migrationLog.deleteCurrentMigration();
        } catch (SQLException e) {
            logger.error("Error occurred during migration execution", e);
        }
    }

    public void rollbackMigrations(String rollbackId){
        long target = Long.parseLong(rollbackId);
        try (Connection connection = DataSourceProvider.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            long lastMigrationIndex = migrationLog.fetchLastMigrationIndex();
            while (lastMigrationIndex != target-1) {
                rollback(String.valueOf(lastMigrationIndex), connection);
                migrationLog.deleteMigrationInfo(lastMigrationIndex,connection);
                lastMigrationIndex--;
            }
            migrationLog.updatePendingMigrations();
            connection.commit();
    } catch (SQLException e) {
            logger.error("Error occurred during rollbacks execution", e);
        }
    }

    private void rollback(String id,Connection connection) throws SQLException {
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
