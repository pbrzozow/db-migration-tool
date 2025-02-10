package migration;
import util.Checksum;
import model.Migration;
import util.ChecksumGenerator;

import java.io.*;

public class MigrationReader {

    public static Migration readMigration(File file) {
        String fileName = file.getName();
        String sql = extractSql(file);
        Checksum checksum = ChecksumGenerator.generate(file);
        return new Migration(fileName,sql,checksum);
    }

    private static String extractSql(File file) {
        StringBuilder sql = new StringBuilder();
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine())!=null){
                sql.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot read a file",e);
        }
        return sql.toString();}}

