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

//        jdbc.execute("TRUNCATE TABLE author CASCADE ;");

        jdbc.execute("DO\n"
                + "$func$\n"
                + "BEGIN\n"
                + "   EXECUTE "
                + "   (SELECT 'TRUNCATE TABLE ' || string_agg(c.oid::regclass::text, ', ') || ' CASCADE'\n"
                + "    FROM   pg_class c\n"
                + "    WHERE  c.relkind = 'r'\n"
                + "    AND    c.relnamespace = 'public'::regnamespace\n"
                + "    AND    c.relname <> 'flyway_schema_history'\n"
                + "   );\n"
                + "END\n"
                + "$func$;");


        jdbc.queryForList("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'")
                .stream()
                .map(sq -> sq.get("SEQUENCE_NAME"))
                .forEach(sq -> jdbc.execute("ALTER SEQUENCE " + sq + " RESTART WITH 1"));
    }

}
