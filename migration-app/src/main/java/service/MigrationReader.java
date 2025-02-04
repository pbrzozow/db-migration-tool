package service;
import domain.Migration;

import java.io.*;

public class MigrationReader {
        public static Migration readMigration(File file) throws IOException {
        String fileName = file.getName();
        long id = extractId(fileName);
        String sql = extractSql(file);
        return new Migration(id,fileName,sql);
    }
    private static long extractId(String fileName){
        String id = fileName.substring(0, 2);
        return Long.parseLong(id);
    }
    private static String extractSql(File file) throws IOException {
        StringBuilder sql = new StringBuilder();
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine())!=null){
                sql.append(line);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return sql.toString();
    }
}
