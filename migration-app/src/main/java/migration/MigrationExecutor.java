package migration;


import model.Migration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;

public class MigrationExecutor {
    private static final Logger logger
            = LoggerFactory.getLogger(MigrationExecutor.class);
    private final DataSource dataSource;
    private final MigrationLog migrationLog;
    private final MigrationValidator migrationValidator;


    public MigrationExecutor(DataSource dataSource,MigrationLog migrationLog,MigrationValidator migrationValidator) {
        this.dataSource = dataSource;
        this.migrationLog = migrationLog;
        this.migrationValidator = migrationValidator;

    }

    public void executeAndSaveMigration(Migration migration) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            logger.info("Connected to database successfully");
            connection.setAutoCommit(false);
            migrationValidator.validate(migration);
            executeMigration(migration,connection);
            migrationLog.saveMigration(migration,connection);
            connection.commit();
            logger.info("Migration has been commited");
        } catch (SQLException e) {
            logger.error("Migration was not successful",e);
            rollbackTransaction(connection);
        }finally {
            closeConnection(connection);
        }
    }

    private static void closeConnection(Connection connection) {
        if (connection !=null){
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("Connection cannot be closed",e);
            }
        }
        logger.info("Closing connection");
    }
    void executeMigration(Migration migration, Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(migration.getSql());
        }
    }



    private void rollbackTransaction(Connection connection) {
        try {
            if (connection != null) {
                connection.rollback();
                logger.info("Migration was rolled back");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
