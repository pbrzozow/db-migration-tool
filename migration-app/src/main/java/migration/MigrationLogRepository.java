package migration;

import model.Migration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class MigrationLogRepository {
    private static final Logger logger
            = LoggerFactory.getLogger(MigrationLogRepository.class);
    private final DataSource dataSource;

    public MigrationLogRepository(DataSource dataSource){
        this.dataSource = dataSource;
    }

    protected void initializeMigrationLogTable() {
        try(Connection connection = dataSource.getConnection()) {
            String createTableSQL = """
            CREATE TABLE IF NOT EXISTS migrations (
                id SERIAL PRIMARY KEY,
                file_name VARCHAR(100) UNIQUE,
                migration_date VARCHAR(20),
                checksum VARCHAR(100)
            );
        """;
            try(Statement statement = connection.createStatement();) {
                statement.execute(createTableSQL);
            }
        } catch (SQLException e) {
            logger.error("Cannot initialize migration table",e);
            throw new RuntimeException("Cannot initalize migration table",e);
        }
    }

    public long fetchLastMigrationIndex() {
        long lastMigrationIndex = 0;
        String query= "SELECT COUNT(*) FROM migrations WHERE file_name LIKE 'V%';";
        try(Connection connection = dataSource.getConnection()) {
            ResultSet resultSet;
            try (Statement statement = connection.createStatement()) {
                resultSet = statement.executeQuery(query);
                if (resultSet.next()) {
                    lastMigrationIndex = resultSet.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot retrieve information from database: "+ e.getMessage());
        }
        return lastMigrationIndex;}

    public void saveMigration(Migration migration, Connection connection) throws SQLException {
        logger.info("Saving migration info to database...");
        String sql = "INSERT INTO MIGRATIONS ( file_name, migration_date, checksum) VALUES ( ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, migration.getFileName());
            ps.setString(2, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            ps.setString(3, migration.getChecksum().key());
            ps.executeUpdate();
        }
        logger.info("Migration info saved");
    }

    public void deleteMigrationInfo(Long id,Connection connection) throws SQLException {
        String sql = "DELETE FROM MIGRATIONS WHERE file_name LIKE ? ;";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            String version = "V"+id+"%";
            ps.setString(1,version);
            ps.execute();
        }
    }
    public String getMigrationHistory() {
        StringBuilder history = new StringBuilder();
        String query = "SELECT file_name,migration_date FROM migrations";
        try(Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                ResultSet rs = statement.executeQuery(query);
                while (rs.next()){
                    String fileName = rs.getString("file_name");
                    String migrationDate = rs.getString("migration_date");
                    String info = String.format("File name: %s, migration date: %s \n",fileName,migrationDate);
                    history.append(info);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return history.toString();
    }
}
