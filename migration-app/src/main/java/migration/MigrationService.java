package migration;

import database.DataSourceProvider;
import model.Migration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MigrationService {
    private static final Logger logger
            = LoggerFactory.getLogger(MigrationService.class);

    private final MigrationLog migrationLog;
    private final MigrationValidator migrationValidator;

    public MigrationService( MigrationLog migrationLog,MigrationValidator migrationValidator) {
        this.migrationLog = migrationLog;
        this.migrationValidator = migrationValidator;
    }

    public synchronized void executeMigration(Migration migration) {
        try (Connection connection = DataSourceProvider.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            migrationValidator.validate(migration);
            executeMigration(migration, connection);
            migrationLog.saveMigration(migration, connection);
            connection.commit();
            if (migration.getFileName().startsWith("V") ){
            migrationLog.deleteCurrentMigration();
            }
        } catch (SQLException e) {
            logger.error("Error occurred during migration execution", e);
        }
    }

    private void executeMigration(Migration migration, Connection connection) throws SQLException {
        logger.info("Executing migration...");
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(migration.getSql());
        }
        logger.info("Migration executed");
    }
}
