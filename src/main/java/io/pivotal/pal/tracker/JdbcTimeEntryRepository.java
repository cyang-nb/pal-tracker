package io.pivotal.pal.tracker;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry t) {
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO time_entries (project_id, user_id, date, hours) " +
                            "VALUES (?, ?, ?, ?)",
                    RETURN_GENERATED_KEYS
            );

            statement.setLong(1, t.getProjectId());
            statement.setLong(2, t.getUserId());
            statement.setDate(3, Date.valueOf(t.getDate()));
            statement.setInt(4, t.getHours());

            return statement;
        }, generatedKeyHolder);

        return find(generatedKeyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long id) {
        return jdbcTemplate.query(
                "SELECT id, project_id, user_id, date, hours FROM time_entries WHERE id = ?",
                new Object[]{id},
                new ResultSetExtractor<TimeEntry>() {
                    @Override
                    public TimeEntry extractData(ResultSet rs) throws SQLException, DataAccessException {
                        return rs.next() ? new TimeEntry(rs.getLong("id"),
                                rs.getLong("project_id"),
                                rs.getLong("user_id"),
                                rs.getDate("date").toLocalDate(),
                                rs.getInt("hours")) : null;
                    }
                });
    }

    @Override
    public List<TimeEntry> list() {
        return jdbcTemplate.query("select id, project_id, user_id, date, hours from time_entries",
                new Object[]{},
                new RowMapper<TimeEntry>() {
                    @Override
                    public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new TimeEntry(rs.getLong("id"),
                                rs.getLong("project_id"),
                                rs.getLong("user_id"),
                                rs.getDate("date").toLocalDate(),
                                rs.getInt("hours"));
                    }
                });
    }

    @Override
    public TimeEntry update(long id, TimeEntry t) {
        jdbcTemplate.update("UPDATE time_entries " +
                        "SET project_id = ?, user_id = ?, date = ?,  hours = ? " +
                        "WHERE id = ?",
                t.getProjectId(),
                t.getUserId(),
                Date.valueOf(t.getDate()),
                t.getHours(),
                id);

        return find(id);
    }

    @Override
    public TimeEntry delete(long id) {
        TimeEntry t = find(id);
        jdbcTemplate.update("DELETE FROM time_entries WHERE id = ?", id);

        return t;
    }
}
