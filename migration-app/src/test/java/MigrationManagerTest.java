import exception.NoPendingMigrationException;
import exception.UndoMigrationNotFoundException;
import migration.MigrationLog;
import migration.MigrationManager;
import migration.MigrationService;
import model.Migration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Checksum;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MigrationManagerTest {

    private MigrationLog migrationLog;
    private MigrationManager migrationManager;
    private MigrationService migrationService;

    @BeforeEach
    public void setup(){
        this.migrationLog = mock(MigrationLog.class);
        this.migrationService = mock(MigrationService.class);
        this.migrationManager = new MigrationManager(migrationLog,migrationService);
    }

    @Test
    void executeMigrationWhenAvailable(){
        Migration migration = new Migration("V2__smth.sql","INSERT INTO EMP(name) VALUES('Kasia');",new Checksum("abc"));
        when(migrationLog.getCurrentMigration()).thenReturn(Optional.of(migration));

        migrationManager.migrate();

        verify(migrationService, times(1)).executeMigration(any(Migration.class));
    }

    @Test
    void shouldThrowExceptionWhenExecutionNotAvailable(){
        assertThrows(NoPendingMigrationException.class,()->migrationManager.migrate());
    }

    @Test
    void shouldThrowExceptionWhenRollbackNotAvailable(){
        assertThrows(UndoMigrationNotFoundException.class,()->migrationManager.rollback("2"));
    }
    @Test
    void executeRollbackWhenAvailable(){
        String id = "3";
        Migration migration = new Migration("U2__smth.sql","DELETE FROM EMP WHERE name = 'Kasia",new Checksum("cde"));
        when(migrationLog.getUndoMigration(id)).thenReturn(Optional.of(migration));

        migrationManager.rollback(id);

        verify(migrationService, times(1)).executeMigration(migration);
    }
}
