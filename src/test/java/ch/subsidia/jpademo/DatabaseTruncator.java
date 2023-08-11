package ch.subsidia.jpademo;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DatabaseTruncator {
    private final JdbcTemplate jdbc;

    public void truncate() {

        jdbc.execute("TRUNCATE TABLE author CASCADE ;");

        jdbc.queryForList("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'")
                .stream()
                .map(sq -> sq.get("SEQUENCE_NAME"))
                .forEach(sq -> jdbc.execute("ALTER SEQUENCE " + sq + " RESTART WITH 1"));
    }

}
