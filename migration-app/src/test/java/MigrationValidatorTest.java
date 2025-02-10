import database.DataSourceProvider;
import migration.MigrationLog;
import migration.MigrationValidator;
import model.Migration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Checksum;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MigrationValidatorTest {
    private MigrationValidator migrationValidator;
    private ResultSet resultSet;
    private Migration migration;

    @BeforeEach
    void setUp() throws SQLException {
        DataSource ds = mock(DataSource.class);
        migrationValidator= new MigrationValidator(ds);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        Connection connection = mock(Connection.class);
        resultSet = mock(ResultSet.class);
        Checksum checksum = new Checksum("12340");
        migration = new Migration("","", checksum);
        when(ds.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(this.resultSet);
    }

    @Test
    void shouldThrowExceptionWhenAddingInvalidMigration() throws SQLException {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(2);
        assertThrows(SQLException.class,()->migrationValidator.validate(migration));
    }
    @Test
    void shouldNotThrowExceptionWithValidMigration() throws SQLException {
        when(resultSet.next()).thenReturn(false);
        assertDoesNotThrow(() -> migrationValidator.validate(migration));
    }
}
