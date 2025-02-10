package migration;
import model.Migration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MigrationLog {
    private static final Logger logger
            = LoggerFactory.getLogger(MigrationLog.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final DataSource dataSource;
    private final MigrationRepository migrationRepository;
    private TreeMap<Long, Migration> pendingMigrations;

    public MigrationLog(DataSource dataSource,MigrationRepository migrationRepository) {
        this.dataSource=dataSource;
        this.migrationRepository = migrationRepository;
        initializeMigrationLogTable();
        this.pendingMigrations=fetchPendingMigrations();
    }

    private TreeMap<Long,Migration> fetchPendingMigrations() {
        Map<Long, Migration> migrations = getVersionedMigrations();
        Map<Long, Migration> pendingMigrations = getPendingMigrations(migrations);
        return new TreeMap<>(pendingMigrations);
    }

    private Map<Long, Migration> getVersionedMigrations() {
        return migrationRepository.getVersionedMigrations();
    }

    private Map<Long, Migration> getPendingMigrations(Map<Long, Migration> migrations) {
        long lastMigrationIndex = fetchMigrationIndexFromDatabase();
        Map<Long, Migration> pendingMigrations = filterPendingMigrations(migrations, lastMigrationIndex);
        return pendingMigrations;
    }

    private void initializeMigrationLogTable() {
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

    private Map<Long, Migration> filterPendingMigrations(Map<Long,Migration> allMigrations,long lastMigrationIndex) {
        return allMigrations.entrySet()
                .stream()
                .filter(entry -> entry.getKey() > lastMigrationIndex)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Optional<Migration> getCurrentMigration(){
        Migration migration = null;
        if (!pendingMigrations.isEmpty()){
         migration = pendingMigrations.firstEntry().getValue();}
        return Optional.ofNullable(migration);
    }

    public void deleteCurrentMigration(){
        if (!pendingMigrations.isEmpty()){
        Long key = pendingMigrations.firstEntry().getKey();
        pendingMigrations.remove(key);}
        else {throw new RuntimeException("Migration doesn't exists");}
    }

    public void saveMigration(Migration migration, Connection connection) throws SQLException {
        logger.info("Saving migration info to database...");
        String sql = "INSERT INTO MIGRATIONS ( file_name, migration_date, checksum) VALUES ( ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, migration.getFileName());
            ps.setString(2, getCurrentDate().toString());
            ps.setString(3, migration.getChecksum().key());
            ps.executeUpdate();
        }
        logger.info("Migration info saved");
    }

    protected long fetchMigrationIndexFromDatabase() {
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
        return lastMigrationIndex;    }

    private String getCurrentDate() {
        return LocalDateTime.now().format(DATE_FORMATTER);
    }

    public Optional<Migration> getUndoMigration(String rollbackId) {
        long id= Long.parseLong(rollbackId);
        Migration migration = migrationRepository.getUndoMigrations().get(id);
        return Optional.ofNullable(migration);
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
