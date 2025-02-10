package database;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
public class DataSourceProvider {
    private static final Logger logger
            = LoggerFactory.getLogger(DataSourceProvider.class);
    private static DataSource dataSource;
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/userdb";
    public static final String USERNAME = "myusername";
    public static final String PASSWORD = "password";

    public static synchronized DataSource getDataSource() {
        if (dataSource==null){
        var ds = new HikariDataSource();
        ds.setJdbcUrl(DATABASE_URL);
        ds.setUsername(USERNAME);
        ds.setPassword(PASSWORD);
        dataSource = ds;
        }
        return dataSource;
    }
}




