package util;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChecksumGenerator {
public static Checksum generate(File file)  {
    String checksum = null;
    try {
        byte[] data = Files.readAllBytes(file.toPath());
        byte[] hash = MessageDigest.getInstance("MD5").digest(data);
        checksum = new BigInteger(1, hash).toString(16);
    } catch (IOException e) {
        throw new RuntimeException(e);
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
    }
    return new Checksum(checksum);
    }
}
