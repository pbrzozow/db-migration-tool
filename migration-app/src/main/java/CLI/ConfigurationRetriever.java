package CLI;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationRetriever {

    public static String getDefaultFilePath() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
            return properties.getProperty("migration.dir");
        } catch (IOException e) {
            System.out.println("Upload a valid diretory path.");
            return null;
        }
    }

}
