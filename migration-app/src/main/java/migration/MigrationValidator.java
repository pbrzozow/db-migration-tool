package migration;

import exception.MigrationAlreadyExistException;
import lombok.AllArgsConstructor;
import model.Migration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
@AllArgsConstructor
public class MigrationValidator {
    private DataSource dataSource;
    private static final Logger logger
            = LoggerFactory.getLogger(MigrationValidator.class);
        public void validate(Migration migration) throws SQLException {
        logger.debug("Validating migration...");
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT COUNT(checksum) FROM migrations WHERE CHECKSUM = ?")) {
            ps.setString(1, migration.getChecksum().key());
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    throw new MigrationAlreadyExistException("Migration already exists in database");
                }
            }
        }
        logger.debug("Migration validated");
    }
}
