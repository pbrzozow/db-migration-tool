package migration;

import lombok.Getter;
import model.Migration;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
public class MigrationRepository {
    private final Map<Long,Migration> versionedMigrations;
    private final Map<Long,Migration> undoMigrations;

    public MigrationRepository(String folderPath){
        this.versionedMigrations = loadMigrations(folderPath+"/changelog");
        this.undoMigrations = loadMigrations(folderPath+"/rollback");
    }

private Map<Long, Migration> loadMigrations(String folderPath) {
        validateFolderPath(folderPath);
        File[] files = getMigrationFiles(folderPath);
        return files.length == 0 ? Collections.emptyMap() : parseMigrations(files);
    }

    private void validateFolderPath(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("Cannot find a folder with file path " + folderPath);
        }
    }

    private File[] getMigrationFiles(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        return files != null ? files : new File[0];
    }

    private Map<Long, Migration> parseMigrations(File[] files) {
        Map<Long, Migration> migrationsMap = new HashMap<>();
        for (File file : files) {
            Migration migration = MigrationReader.readMigration(file);
            migrationsMap.put(extractId(migration), migration);
        }
        return migrationsMap;
    }

    private Long extractId(Migration migration) {
        String version = migration.getFileName().split("__")[0];
        String id = version.replace("V", "").replace("U","");
        return Long.parseLong(id);
    }
}
