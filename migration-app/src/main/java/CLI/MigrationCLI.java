package CLI;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import migration.MigrationFactory;
import migration.MigrationManager;
import migration.MigrationService;
import picocli.CommandLine;

@Slf4j
@AllArgsConstructor
@CommandLine.Command
public class MigrationCLI implements Runnable {
    private MigrationManager migrationManager;


    @CommandLine.Command(name = "migrate", description = "Execute next migration")
    public void migrate(){
        migrationManager.migrate();
    }

    @CommandLine.Command(name = "rollback", description = "Undo a migration with particular id ")
    public void rollback(@CommandLine.Parameters(paramLabel = "<id>", description = "ID of migration to rollback ") String id) {
        migrationManager.rollback(id);
    }

    @CommandLine.Command(name = "history",description = "Show migration history")
    public void showHistory(){
        migrationManager.showHistory();
    }

    @Override
    public void run() {
        CommandLine.usage(this,System.out);
    }


    public static void main(String[] args) {
        String folderPath = ConfigurationRetriever.getDefaultFilePath();
        MigrationManager manager = MigrationFactory.getMigrationManagerInstance(folderPath);
        MigrationCLI migrationCLI = new MigrationCLI(manager);
        new CommandLine(migrationCLI).execute(args);
    }


}
