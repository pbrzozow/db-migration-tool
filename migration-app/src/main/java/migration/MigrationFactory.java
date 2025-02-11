package migration;

import database.DataSourceProvider;

import javax.sql.DataSource;

public class MigrationFactory {

    public static MigrationManager getMigrationManagerInstance(String folderPath){
        DataSource dataSource = DataSourceProvider.getDataSource();
        MigrationRepository migrationRepository = new MigrationRepository(folderPath);
        MigrationLogRepository migrationLogRepository = new MigrationLogRepository(dataSource);
        MigrationLog migrationLog = new MigrationLog(migrationLogRepository,migrationRepository);
        MigrationValidator migrationValidator = new MigrationValidator(dataSource);
        MigrationService migrationService = new MigrationService(migrationLog,migrationValidator);
        return new MigrationManager(migrationLog,migrationService);
    }
}
