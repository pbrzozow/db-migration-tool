package model;

import lombok.Value;
import util.Checksum;

@Value
public class Migration {
    String fileName;
    String sql;
    Checksum checksum;
}
