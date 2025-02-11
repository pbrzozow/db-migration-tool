package migration;
import model.Migration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MigrationLog {
    private static final Logger logger
            = LoggerFactory.getLogger(MigrationLog.class);

    private final MigrationLogRepository migrationLogRepository;
    private final MigrationRepository migrationRepository;
    private TreeMap<Long, Migration> pendingMigrations;

    public MigrationLog(MigrationLogRepository migrationLogRepository,MigrationRepository migrationRepository) {
        this.migrationRepository = migrationRepository;
        this.migrationLogRepository = migrationLogRepository;
        migrationLogRepository.initializeMigrationLogTable();
        this.pendingMigrations=fetchPendingMigrations();
    }

    public void updatePendingMigrations(){
        pendingMigrations = fetchPendingMigrations();
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
        long lastMigrationIndex = fetchLastMigrationIndex();
        Map<Long, Migration> pendingMigrations = filterPendingMigrations(migrations, lastMigrationIndex);
        return pendingMigrations;
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

    public Optional<Migration> getUndoMigration(String rollbackId) {
        long id= Long.parseLong(rollbackId);
        Migration migration = migrationRepository.getUndoMigrations().get(id);
        return Optional.ofNullable(migration);
    }
    public long fetchLastMigrationIndex(){
        return migrationLogRepository.fetchLastMigrationIndex();
    }
    public String getMigrationHistory() {
        return migrationLogRepository.getMigrationHistory();
    }
    public void saveMigration(Migration migration, Connection connection) throws SQLException {
        migrationLogRepository.saveMigration(migration,connection);
    }
    public void deleteMigrationInfo(Long id,Connection connection) throws SQLException {
        migrationLogRepository.deleteMigrationInfo(id,connection);
    }
}
