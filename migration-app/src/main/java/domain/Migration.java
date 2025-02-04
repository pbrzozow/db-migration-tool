package domain;

import lombok.Value;

@Value
public class Migration {
    long id;
    String fileName;
    String sql;


}
